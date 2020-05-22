package finalyear.project.patientinfobank.Adapter.BottomNavigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

class BottomNavigationAdapter(var fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {
    val fragmentList = arrayListOf<Fragment>()
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int = fragmentList.size

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }
}