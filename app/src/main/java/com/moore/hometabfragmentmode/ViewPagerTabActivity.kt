package com.moore.hometabfragmentmode

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.moore.bottomtab.HomeBottomTabLayout
import kotlinx.android.synthetic.main.activity_viewpager_tab.*

class ViewPagerTabActivity : AppCompatActivity(), HomeBottomTabLayout.HomeBottomTabLayoutCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewpager_tab)

        tab_layout.setHomeBottomTabLayoutCallback(this)
        tab_layout.initFirstTab(2)
    }

    override fun getFragmentByTag(tabTag: String): Fragment? {
        when (tabTag) {
            "key_index_fragment" -> return Fragment1()
            "key_name_fragment" -> return Fragment2()
            "key_discover_fragment" -> return Fragment3()
            "key_collect_fragment" -> return Fragment4()
            "key_user_fragment" -> return Fragment5()
        }
        return null
    }

    override fun onClickChangeTab(selectedIndex: Int, selectedTag: String?) {

    }
}
