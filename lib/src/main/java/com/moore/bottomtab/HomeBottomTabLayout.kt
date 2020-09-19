package com.moore.bottomtab

import android.content.Context
import android.content.res.TypedArray
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import java.io.IOException

/**
 * Created by moore on 2019/11/27.
 */
class HomeBottomTabLayout(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs),
    View.OnClickListener {

    private lateinit var mTabConfig: TabConfig

    private var mFragmentList = ArrayList<Fragment?>()
    private var mTabContainerList: ArrayList<ViewGroup>? = null

    private var mOverSideTab = ArrayList<View>(1)
    private var mCurrentSelectedIndex = 0
    private var mViewPager: ViewPager? = null
    private var mFragmentManager: FragmentManager? = null
    private var mCallback: HomeBottomTabLayoutCallback? = null
    private var mFragmentContainerId = -1
    private var mViewPagerId = -1
    private var isCurrentViewPager = false
    private var mIsCanClickTab = true

    init {
        val array: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.HomeBottomTabLayout)
        mFragmentContainerId = array.getResourceId(R.styleable.HomeBottomTabLayout_container_id, -1)
        mViewPagerId = array.getResourceId(R.styleable.HomeBottomTabLayout_viewPager_id, -1)
        val configFile = array.getString(R.styleable.HomeBottomTabLayout_config_file_name)
        isCurrentViewPager = mViewPagerId != -1
        array.recycle()

        this.orientation = HORIZONTAL
        this.clipChildren = false
        mFragmentManager = (context as FragmentActivity).supportFragmentManager
        configFile?.let {
            try {
                val tabConfigFileInputStream = context.assets.open(it)
                mTabConfig =
                    TabUtils.parseJsonToConfig(tabConfigFileInputStream)
                        ?: throw IllegalArgumentException("配置文件错误，请检查")
            } catch (e: IOException) {
                throw IllegalArgumentException("请检查${it}是否存在于assets中！")
            }
        } ?: let {
            throw IllegalArgumentException("请使用config_file_name设置配置文件名称！")
        }
        initTabs()
    }

    private fun initTabs() {
        if (mTabConfig.tabs.isNullOrEmpty()) {
            return
        }
        mTabContainerList = ArrayList(mTabConfig.tabs.size)
        mTabConfig.tabs.forEach { tab ->
            generateItemView(tab)
            mFragmentList.add(null)
        }
        mOverSideTab.forEach {
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val layoutParams = it.layoutParams as MarginLayoutParams
                    val height = (it.height * 2.5f).toInt()
                    layoutParams.height = height
                    layoutParams.width = height
                    if (!mTabConfig.isTitleVisible) {
                        layoutParams.setMargins(0, 0, 0, (it.height * 1.5f).toInt())
                    }
                    it.layoutParams = layoutParams
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    mOverSideTab.remove(it)
                }
            })
        }
    }

    fun initFirstTab(index: Int) {
        if (index < 0 || index > mTabContainerList?.size ?: 0) {
            throw IndexOutOfBoundsException("没有这么多tab，index值：$index")
        }
        if (mCallback == null) {
            throw IllegalStateException("需要先设置callback获取对应的Fragment")
        }
        if (isCurrentViewPager) {
            setupViewPager(index)
        } else {
            changeTab(index)
        }
    }

    fun hideTab(index: Int) {
        if (index < 0 || index > mTabContainerList?.size ?: 0) {
            throw IndexOutOfBoundsException("没有这么多tab，index值：$index")
        }
        mTabContainerList?.get(index)?.visibility = View.GONE
    }

    fun selectByTag(tabTag: String) {
        mTabConfig.tabs.forEachIndexed { index, tabConfig ->
            if (tabTag == tabConfig.tabTag) {
                changeTab(index)
                return
            }
        }
    }

    fun getCurrentSelectedTag(): String {
        return mTabConfig.tabs[mCurrentSelectedIndex].tabTag
    }

    fun getCurrentSelectedIndex(): Int {
        return mCurrentSelectedIndex
    }

    fun getCurrentSelectedFragment(): Fragment? {
        return mFragmentList[mCurrentSelectedIndex]
    }

    fun getFragmentByTag(tabTag: String): Fragment? {
        return mFragmentManager?.findFragmentByTag(tabTag)
    }

    fun getFragmentList(): List<Fragment?>? {
        return mFragmentList
    }

    fun setIsCanClickTab(isCanClick: Boolean) {
        this.mIsCanClickTab = isCanClick
    }

    fun setUnreadTip(tag: String, countStr: String?, isShowCount: Boolean = true) {
        mTabConfig.tabs.forEachIndexed { index, tabConfig ->
            if (tag == tabConfig.tabTag) {
                if (isShowCount) {
                    val tvUnRead =
                        mTabContainerList?.get(index)
                            ?.findViewById<TextView>(R.id.TabItem_tvUnReadTip)
                    tvUnRead?.visibility = View.VISIBLE
                    tvUnRead?.text = countStr
                } else {
                    val viewPoint =
                        mTabContainerList?.get(index)
                            ?.findViewById<View>(R.id.TabItem_viewPoint)
                    viewPoint?.visibility = View.VISIBLE
                }
                return
            }
        }
    }

    fun hideUnReadTip(tag: String) {
        mTabConfig.tabs.forEachIndexed { index, tabConfig ->
            if (tag == tabConfig.tabTag) {
                mTabContainerList?.get(index)
                    ?.findViewById<TextView>(R.id.TabItem_tvUnReadTip)?.visibility = View.GONE
                mTabContainerList?.get(index)
                    ?.findViewById<View>(R.id.TabItem_viewPoint)?.visibility = View.GONE
                return
            }
        }
    }

    private fun setupViewPager(selectedIndex: Int) {
        mViewPager = rootView.findViewById(mViewPagerId)
        mTabConfig.tabs.forEachIndexed { index, itemConfig ->
            mFragmentList[index] = mCallback!!.getFragmentByTag(itemConfig.tabTag)
        }
        val map = mFragmentList.map { it!! }
        mFragmentManager?.let {
            mViewPager?.adapter = TabFragmentPageAdapter(it, map)
        }
        mViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                setTabSelected(position)
            }
        })
        mViewPager?.currentItem = selectedIndex
    }

    override fun onClick(v: View?) {
        if (!mIsCanClickTab) {
            return
        }
        var tempIndex = 0
        val tag = v?.tag as String
        mTabContainerList?.forEachIndexed { index, viewGroup ->
            if ((viewGroup.tag as String) == tag) {
                tempIndex = index
                return@forEachIndexed
            }
        }
        changeTab(tempIndex)
        mCallback?.onClickChangeTab(tempIndex, tag)
    }

    /**
     * 切换tab操作，分两步
     * 1、切换按钮状态
     * 2、切换fragment
     */
    private fun changeTab(selectedIndex: Int) {
        if (isCurrentViewPager) {
            mViewPager?.currentItem = selectedIndex
        } else {
            //tab状态改变
            setTabSelected(selectedIndex)
            //fragment改变
            setFragmentSelected(selectedIndex)
        }
        //更改当前下标
        mCurrentSelectedIndex = selectedIndex
    }

    /**
     * 切换按钮状态
     */
    private fun setTabSelected(selectedIndex: Int) {
        mTabContainerList?.forEachIndexed { index, viewGroup ->
            val ivIcon = viewGroup.findViewById<ImageView>(R.id.TabItem_ivIcon)
            val tvName = viewGroup.findViewById<TextView>(R.id.TabItem_tvTitle)
            val itemConfig = mTabConfig.tabs[index]
            val textColor: Int
            val iconResource: Int
            val textSize: Float
            if (index == selectedIndex) {
                textColor = mTabConfig.tabSelectedColor
                textSize = mTabConfig.tabSelectedTextSize.toFloat()
                iconResource = TabUtils.getResourceDrawableId(context, itemConfig.iconSelected)
                viewGroup.isEnabled = false
            } else {
                textColor = mTabConfig.tabNormalColor
                textSize = mTabConfig.tabNormalTextSize.toFloat()
                iconResource = TabUtils.getResourceDrawableId(context, itemConfig.iconNormal)
                viewGroup.isEnabled = true
            }
            ivIcon.setImageResource(iconResource)
            tvName.setTextColor(textColor)
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize)
        }
    }

    /**
     * 切换fragment
     */
    private fun setFragmentSelected(selectedIndex: Int) {
        val beginTransaction = mFragmentManager?.beginTransaction()
        val itemConfig = mTabConfig.tabs[selectedIndex]
        var fragmentByTag = mFragmentManager?.findFragmentByTag(itemConfig.tabTag)
        val isFirstInit: Boolean
        if (fragmentByTag == null) {
            fragmentByTag = mCallback?.getFragmentByTag(itemConfig.tabTag)
            mFragmentList[selectedIndex] = fragmentByTag
            isFirstInit = true
        } else {
            isFirstInit = false
        }
        mFragmentList.forEach { fragment ->
            if (fragment != null && fragment.isAdded) {
                beginTransaction?.hide(fragment)
            }
        }
        fragmentByTag?.let {
            if (isFirstInit) {
                beginTransaction?.add(mFragmentContainerId, fragmentByTag, itemConfig.tabTag)
            } else {
                beginTransaction?.show(fragmentByTag)
            }
        }
        beginTransaction?.commitAllowingStateLoss()
    }

    private fun generateItemView(itemConfig: ItemConfig) {
        val view: ViewGroup =
            View.inflate(context, R.layout.bottom_tab_item_layout, null) as ViewGroup
        val tvName = view.findViewById<TextView>(R.id.TabItem_tvTitle)
        val ivIcon = view.findViewById<ImageView>(R.id.TabItem_ivIcon)
        //tabTitle
        if (mTabConfig.isTabNameResId) {
            tvName.text = TabUtils.getStringByResId(context, itemConfig.tabName)
        } else {
            tvName.text = itemConfig.tabName
        }
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTabConfig.tabNormalTextSize.toFloat())
        tvName.setTextColor(mTabConfig.tabNormalColor)
        //tabIcon
        ivIcon.setImageResource(TabUtils.getResourceDrawableId(context, itemConfig.iconNormal))
        //bg
        itemConfig.itemBg.apply {
            if (!TextUtils.isEmpty(this)) {
                ivIcon.setBackgroundResource(TabUtils.getResourceDrawableId(context, this!!))
            }
        }
        if (itemConfig.isOverSide) {
            mOverSideTab.add(ivIcon)
        }
        if (!mTabConfig.isTitleVisible) {
            tvName.visibility = View.GONE
        }
        val weight = if (itemConfig.isOverSide) 1.1f else 1f
        val params = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight)

        view.setOnClickListener(this)
        view.tag = itemConfig.tabTag
        this.addView(view, params)
        mTabContainerList?.add(view)
    }

    fun setHomeBottomTabLayoutCallback(homeBottomTabLayoutCallback: HomeBottomTabLayoutCallback?) {
        this.mCallback = homeBottomTabLayoutCallback
    }

    interface HomeBottomTabLayoutCallback {
        fun getFragmentByTag(tabTag: String): Fragment?

        fun onClickChangeTab(selectedIndex: Int, selectedTag: String?)
    }
}