package com.example.pulsa.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.pulsa.utils.LoggedIn
import com.example.pulsa.R
import com.example.pulsa.databinding.BaseLayoutBinding

open class BaseLayoutActivity : AppCompatActivity() {

    private lateinit var binding: BaseLayoutBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = BaseLayoutBinding.inflate(layoutInflater)
        drawerLayout = binding.drawerLayout
        setSupportActionBar(binding.myToolbar.toolbar)
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
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setHomeButtonEnabled(true)

        binding.navViewMenu.setNavigationItemSelectedListener {
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
                    startActivity(i)
                }
                R.id.register -> {
                    val i = Intent(this, RegisterActivity::class.java)
                    startActivity(i)
                }
            }
            true
        }
    }

    private fun setupUserMenu() {
        binding.myToolbar.userMenu.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) binding.drawerLayout.closeDrawer(
                GravityCompat.END
            )
            else binding.drawerLayout.openDrawer(GravityCompat.END)
        }

        if (LoggedIn.getLoggedIn()) {
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
                            it.menu.clear()
                            it.inflateMenu(R.menu.nav_drawer_user)
                            setDefaultNav()
                        }
                        startActivity(Intent(this, MainActivity::class.java))

                    }
                }
                true
            }
        } else {
            setDefaultNav()
        }

    }
}