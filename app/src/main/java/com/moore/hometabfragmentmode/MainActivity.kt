package com.moore.hometabfragmentmode

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.moore.bottomtab.HomeBottomTabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), HomeBottomTabLayout.HomeBottomTabLayoutCallback {

    //每个tab对应的tag，和json配置文件中保持一致
    val TAG_INDEX = "key_index_fragment"
    val TAG_NAME = "key_name_fragment"
    val TAG_DISCOVERY = "key_discover_fragment"
    val TAG_COLLECT = "key_collect_fragment"
    val TAG_USER = "key_user_fragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //设置回调
        main_tab_layout.setHomeBottomTabLayoutCallback(this)
        //初始化，参数为默认展示第几个tab
        main_tab_layout.initFirstTab(2)
        //如果需要展示未读消息，通过该方法展示
        main_tab_layout.setUnreadTip(TAG_USER, "12")
        main_tab_layout.setUnreadTip(TAG_INDEX, "99+")
        main_tab_layout.setUnreadTip(TAG_COLLECT, null, false)
    }

    override fun getFragmentByTag(tabTag: String): Fragment? {
        //根据对应Tag名称，返回具体的Fragment对象
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
        //点击tab回调，可在此进行统计事件、隐藏消息提示等操作
    }
}
