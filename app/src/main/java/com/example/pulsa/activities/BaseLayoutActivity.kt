package com.example.pulsa.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.pulsa.R
import com.example.pulsa.databinding.BaseLayoutBinding
import com.example.pulsa.utils.LoggedIn
import com.example.pulsa.utils.UserUtils
import com.example.pulsa.utils.glideRequestListener
import okhttp3.Response

open class BaseLayoutActivity : AppCompatActivity() {

    private lateinit var binding: BaseLayoutBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = BaseLayoutBinding.inflate(layoutInflater)
        UserUtils.setup(this)
        drawerLayout = binding.drawerLayout
        setSupportActionBar(binding.myToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavMenu()
        setupUserMenu()
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(view: View?) {
        binding.myToolbar.activityContainer.addView(view)
        super.setContentView(binding.root)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    private fun setupNavMenu() {
        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.myToolbar.toolbar,
            R.string.open,
            R.string.close
        )
        toggle.drawerArrowDrawable.setColor(getColor(R.color.button_text))
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setHomeButtonEnabled(true)

        binding.navViewMenu.setNavigationItemSelectedListener {
            val intent = Intent(this, NewPostActivity::class.java)
            when (it.itemId) {
                R.id.item1 -> Toast.makeText(
                    applicationContext,
                    "Clicked item 1",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.item2 -> Toast.makeText(
                    applicationContext,
                    "Clicked item 2",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.item3 -> Toast.makeText(
                    applicationContext,
                    "Clicked item 3",
                    Toast.LENGTH_SHORT
                ).show()
            }
            true
        }
    }

    fun setDefaultNav() {
        binding.navViewUser.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.login -> {
                    val i = Intent(this, LoginActivity::class.java)
                    closeUserMenu()
                    startActivity(i)
                }
                R.id.register -> {
                    val i = Intent(this, RegisterActivity::class.java)
                    closeUserMenu()
                    startActivity(i)
                }
            }
            true
        }
    }

    private fun closeUserMenu() {
        binding.drawerLayout.closeDrawer(GravityCompat.END)
    }

    fun setupUserMenu() {
        binding.myToolbar.userMenu.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) closeUserMenu()
            else binding.drawerLayout.openDrawer(GravityCompat.END)
        }

        if (UserUtils.loggedIn()) {
            binding.navViewUser.menu.clear()
            binding.navViewUser.inflateMenu(R.menu.nav_drawer_loggedin)
            binding.navViewUser.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.editAccount -> {
                        val i = Intent(this, UserActivity::class.java)
                        startActivity(i)
                    }
                    R.id.logout -> {
                        LoggedIn.setLoggedIn(false)
                        binding.navViewUser.let {
                            getSharedPreferences(getString(R.string.user), MODE_PRIVATE).edit()
                                .clear().commit()
                            it.menu.clear()
                            it.inflateMenu(R.menu.nav_drawer_user)
                            setDefaultNav()
                        }
                        startActivity(Intent(this, MainActivity::class.java))

                    }
                }
                true
            }

            val avatar = UserUtils.getUserAvatar(this)

            if (URLUtil.isValidUrl(avatar)) {
                val circularProgressDrawable = CircularProgressDrawable(this)
                circularProgressDrawable.strokeWidth = 3f
                circularProgressDrawable.centerRadius = 15f
                circularProgressDrawable.start()

                Glide.with(this)
                    .load(avatar)
                    .placeholder(circularProgressDrawable)
                    .listener(glideRequestListener)
                    .into(binding.myToolbar.userMenu)
                    .view.visibility = View.VISIBLE
            }


        } else {
            setDefaultNav()
        }

    }

    public open fun resolveGet(content: Any) {
        return
    }

    public open fun resolvePost(content: Any) {
        return
    }

    public open fun resolveFailure(response: Response) {
        return
    }

    public open fun updateAvatar(avatar: String) {
        if (URLUtil.isValidUrl(avatar)) {
            val circularProgressDrawable = CircularProgressDrawable(this)
            circularProgressDrawable.strokeWidth = 3f
            circularProgressDrawable.centerRadius = 15f
            circularProgressDrawable.start()

            Glide.with(this)
                .load(avatar)
                .placeholder(circularProgressDrawable)
                .listener(glideRequestListener)
                .into(binding.myToolbar.userMenu)
                .view.visibility = View.VISIBLE
        }
    }
}