package com.example.pulsa.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.pulsa.R
import com.example.pulsa.databinding.ActivityUserBinding
import com.example.pulsa.fragments.AccountPostsFragment
import com.example.pulsa.fragments.AccountRepliesFragment
import com.example.pulsa.fragments.EditAccountFragment
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.Post
import com.example.pulsa.objects.Reply
import com.example.pulsa.objects.User
import com.example.pulsa.utils.MediaUtils
import com.example.pulsa.utils.UserUtils
import com.google.gson.reflect.TypeToken
import okhttp3.Response

class UserActivity : BaseLayoutActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var user: User
    private lateinit var editAccountFragment: EditAccountFragment
    private var receivedEdits = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fragmentManager = supportFragmentManager

        MediaUtils().verifyStoragePermissions(this);

        var map: HashMap<String, Any> = HashMap()

        val userName = UserUtils.getUserName(this)

        map["type"] = object : TypeToken<User>() {}
        map["url"] = "u/${userName}/"

        runOnUiThread { NetworkManager().get(this, map) }
    }

    override fun resolveGet(content: Any) {
        user = content as User
        editAccountFragment = getCurrentFragment() as EditAccountFragment
        editAccountFragment.setUserFields(user)
    }

    override fun resolvePost(content: Any) {
        user = content as User
        receivedEdits++
        if (receivedEdits < editAccountFragment.getEditCount()) return
        receivedEdits = 0
        editAccountFragment.resetEditCount()
        editAccountFragment.setUserFields(user)
        this.updateAvatar(user.avatar)
        editAccountFragment.displayResultMessage()
    }

    override fun resolveFailure(response: Response) {
        if (response.code == 401) {
            editAccountFragment.displayResultMessage("Session invalid, log in again")
        }
    }

    private fun getCurrentFragment(): Fragment? {
        return fragmentManager.findFragmentById(R.id.userfragment)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}