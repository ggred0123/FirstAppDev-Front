package com.example.mydev

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mydev.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Define the fragments for each tab
        val fragments = arrayListOf(
            ContactFragment(),
            ImagesFragment(),
            ThirdTabFragment()
        )

        // Adapter to manage fragments in the ViewPager
        val tabAdapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragments.size
            override fun createFragment(position: Int): Fragment = fragments[position]
        }

        // Connect adapter to ViewPager2
        binding.viewPager2.adapter = tabAdapter
        window.statusBarColor = Color.BLACK


        // Connect TabLayout with ViewPager2 using TabLayoutMediator
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = "Contact"
                1 -> tab.text = "Images"
                2 -> tab.text = "Albums"
            }
        }.attach()
    }
}

