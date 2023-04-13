package com.example.pulsa.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.blankj.utilcode.util.UriUtils
import com.example.pulsa.databinding.ActivityNewSubBinding
import com.example.pulsa.networking.NetworkManager
import com.example.pulsa.objects.Sub
import com.example.pulsa.utils.MediaUtils
import com.google.gson.reflect.TypeToken

private const val SELECT_PICTURE = 200

class NewSubActivity : BaseLayoutActivity() {
    private lateinit var binding: ActivityNewSubBinding
    private lateinit var imageUri: Uri
    private lateinit var sub: Sub
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewSubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.newSubImage.setOnClickListener {
            val i = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            startActivityForResult(Intent.createChooser(i, "Select picture"), SELECT_PICTURE)
        }
        binding.newSubSubmit.setOnClickListener {

            MediaUtils().verifyStoragePermissions(this)
            val map: HashMap<String, Any> = HashMap()
            map["url"] = "newSub"
            map["type"] = object : TypeToken<Sub>() {}
            map["name"] = binding.newSubName.text.toString()
            if (this::imageUri.isInitialized) {
                map["image"] = UriUtils.uri2File(imageUri)
                map["imageType"] = this.contentResolver.getType(imageUri).toString()
            }

            runOnUiThread { NetworkManager().post(this, map) }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return

        when (requestCode) {
            SELECT_PICTURE -> {
                data?.data?.let { image ->
                    imageUri = image
                    binding.newSubBanner.apply {
                        setImageURI(image)
                    }
                }
            }
        }
    }

    override fun resolvePost(content: Any) {
        sub = content as Sub
        val intent = intent
        intent.putExtra("sub", sub)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }
}