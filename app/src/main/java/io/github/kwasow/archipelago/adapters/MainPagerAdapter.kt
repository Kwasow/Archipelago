package io.github.kwasow.archipelago.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.github.kwasow.archipelago.fragments.BankingFragment
import io.github.kwasow.archipelago.fragments.CryptoFragment
import io.github.kwasow.archipelago.fragments.ErrorFragment
import io.github.kwasow.archipelago.fragments.HomeFragment
import io.github.kwasow.archipelago.fragments.SettingsFragment
import io.github.kwasow.archipelago.fragments.StocksFragment

class MainPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val pageCount = 5

    override fun getItemCount(): Int = pageCount

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> BankingFragment()
            2 -> StocksFragment()
            3 -> CryptoFragment()
            4 -> SettingsFragment()
            else -> ErrorFragment()
        }
    }
}
