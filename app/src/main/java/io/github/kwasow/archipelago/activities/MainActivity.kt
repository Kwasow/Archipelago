package io.github.kwasow.archipelago.activities

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.kwasow.archipelago.R
import io.github.kwasow.archipelago.adapters.MainPagerAdapter
import io.github.kwasow.archipelago.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setupViewPager()

        setContentView(binding.root)
    }

    private fun setupViewPager() {
        val pager = binding.mainPager
        val pagerAdapter = MainPagerAdapter(this)
        pager.adapter = pagerAdapter
        pager.isUserInputEnabled = false

        binding.navigationBar.color = Color.parseColor("#6CB86A")
        binding.navigationBar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> pager.currentItem = 0
                R.id.action_banking -> pager.currentItem = 1
                R.id.action_stocks -> pager.currentItem = 2
                R.id.action_crypto -> pager.currentItem = 3
                R.id.action_settings -> pager.currentItem = 4
            }

            return@setOnNavigationItemSelectedListener true
        }
    }

}
