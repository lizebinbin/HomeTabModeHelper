## 前言
在如今各种App模式如出一辙、各个程序员们却各式各样花式实现效果的环境下，每接手一个项目，就能看到项目代码又不一样，分分钟想统一。同时为了加速后续新应用的开发，封装搭建了这么一个初步的简易框架，实现两分钟快速无脑构建首页。

话不多说，先看实现的效果：
![效果展示](img/sample.gif)

## 简介
实现功能：
- tab+fragment模式首页搭建
- 支持ViewPager
- 支持小红点展示（可选是否展示数量）
- 支持突出展示某一个tab
- 动态隐藏tab

## 使用
### 导入库
```
implementation 'com.moore.lib:homeTabModeHelper:1.0.0'
```
若没有添加jcenter()仓库地址记得添加～
### 配置tab
我们使用.json文件进行配置，然后该文件存放于assets中。配置参考如下：
```
{
  "textColorNormal": "#A4A3A3",
  "textColorSelected": "#C02221",
  "textSizeNormal": 14,
  "textSizeSelected": 14,
  "backgroundColor": "#e9e9e9",
  "isNameResId": false,
  "isTitleVisible": true,
  "tabs": [
    {
      "tabName": "首页",
      "tabTag": "key_index_fragment",
      "iconNormal": "main_home_tab_index_normal",
      "iconSelected": "main_home_tab_index_selected"
    },
    {
      "tabName": "发现",
      "tabTag": "key_discover_fragment",
      "iconNormal": "main_home_tab_discovery_normal",
      "iconSelected": "main_home_tab_discovery_selected",
      "isOverSide": true,
      "itemBg": "home_tab_center_bg"
    },
    {
      "tabName": "我的",
      "tabTag": "key_user_fragment",
      "iconNormal": "main_home_tab_mine_normal",
      "iconSelected": "main_home_tab_mine_selected"
    }
  ]
}
```
这里解释一下各个配置的作用：  

首先是tab文字描述的2个属性，2种状态`textColorNormal`，`textColorSelected`，`textSizeNormal`，`textSizeSelected`，这几个属性作用于tab标题的文字颜色和大小。  
`isNameResId`：表示标题是否为string中资源id，为false的时候直接取配置的tabName，否则获取对应id的资源文件。默认为true  
`isTitleVisible`：标题是否可见。默认为true  
`tabs`：tab列表

每一个tab的配置中：  
`tabName`：tab标题，若使用string.xml配置，则为string对应的id  
`tabTag`：重要，用于区分每个tab对应的Fragment的tag，不能重复  
`iconNormal`，`iconSelected`：两种状态对应的图标，请放在drawable而不是mipmap中  
可选项：  
`isOverSide`：Boolean型，如果需要凸出展示，可设置该属性，样式参考上面效果图  
`itemBg`：设置凸起展示后，可设置背景，一般是个半圆、原型背景之类的  

### 首页使用
在主页对应的xml文件中，只要简单的进行如下使用就可以了：  
关键参数配置：  
`container_id`：指定Fragment展示的布局id  
`config_file_name`：assets中配置文件名称，全名  
`viewPager_id`：和container_id二选一，设置这个的话则为ViewPager可滑动模式，对应的id关联的是ViewPager布局
```
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#999999">

    <FrameLayout
        android:id="@+id/main_content_layout"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        app:layout_constraintBottom_toTopOf="@id/main_tab_layout"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_weight="1" />

    <com.moore.bottomtab.HomeBottomTabLayout
        android:id="@+id/main_tab_layout"
        android:layout_width="match_parent"
        android:layout_height="58dp"
        android:background="#ffffff"
        app:config_file_name="tab.json"
        app:container_id="@id/main_content_layout"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintTop_toBottomOf="@id/main_content_layout" />

</androidx.constraintlayout.widget.ConstraintLayout>
```
### 主页代码
```
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
```
至此，您可以运行您的app试一下，可以看到一个Tab+Fragment的结构已经出来了，非常简单方便，再也不需要各种xml排版、代码中点击回调、Fragment切换等等复杂操作，代码也很清晰明了。
### 更多方法
`setUnreadTip("tagName","unReadCount",isShowCount)`：设置未读消息提示，根据tag设定对应的tab，第二个参数为提示数值，第三个为是否显示具体数量还是只展示小红点。  
`hideUnReadTip(tag: String)`：隐藏未读消息提示  
`getFragmentList(): List<Fragment?>?`：获取添加的Fragment列表  
`getFragmentByTag(tabTag: String): Fragment?`：根据Tag获取Fragment  
`getCurrentSelectedFragment(): Fragment?`：获取当前选中Fragment  
`getCurrentSelectedIndex(): Int`：获取当前选中Fragment的下标  
`getCurrentSelectedTag(): String`：获取当前选中Fragment的Tag  
`selectByTag(tabTag: String)`：根据tag选中该tab  
 `hideTab(index: Int)`：隐藏某个tab  

## 结语
该框架主要还是针对日常开发中经常遇到的一些功能的封装，不涉及太多复杂的逻辑和处理，旨在帮助提升我们的开发效率，如有用到觉得还不错的小伙伴，也请点个赞哦～