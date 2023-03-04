package com.example.pulsa

import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.pulsa.databinding.BaseLayoutBinding

open class BaseLayoutActivity : AppCompatActivity() {

    private lateinit var binding: BaseLayoutBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout


    override fun setContentView(view: View?) {
        binding = BaseLayoutBinding.inflate(layoutInflater)
        val container: FrameLayout = binding.myToolbar.activityContainer
        container.addView(view)
        super.setContentView(binding.root)
        setSupportActionBar(binding.myToolbar.toolbar)

        drawerLayout = binding.drawerLayout
        setupNavMenu()
        setupUserMenu()
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

    private fun setupUserMenu() {
        binding.myToolbar.userMenu.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) binding.drawerLayout.closeDrawer(
                GravityCompat.END
            )
            else binding.drawerLayout.openDrawer(GravityCompat.END)
        }
    }
}