package com.moore.hometabfragmentmode

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.moore.bottomtab.HomeBottomTabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), HomeBottomTabLayout.HomeBottomTabLayoutCallback {

    val TAG_INDEX = "key_index_fragment"
    val TAG_NAME = "key_name_fragment"
    val TAG_DISCOVERY = "key_discover_fragment"
    val TAG_COLLECT = "key_collect_fragment"
    val TAG_USER = "key_user_fragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_tab_layout.setHomeBottomTabLayoutCallback(this)
        main_tab_layout.initFirstTab(2)

        main_tab_layout.setUnreadTip(TAG_USER, "12")
        main_tab_layout.setUnreadTip(TAG_INDEX, "99+")
        main_tab_layout.setUnreadTip(TAG_COLLECT, null, false)
    }

    override fun getFragmentByTag(tabTag: String): Fragment? {
        when (tabTag) {
            TAG_INDEX -> return Fragment1()
            TAG_NAME -> return Fragment2()
            TAG_DISCOVERY -> return Fragment3()
            TAG_COLLECT -> return Fragment4()
            TAG_USER -> return Fragment5()
        }
        return null
    }

    override fun onClickChangeTab(selectedIndex: Int, selectedTag: String?) {
        selectedTag?.let {
            main_tab_layout.hideUnReadTip(it)
        }
    }
}
