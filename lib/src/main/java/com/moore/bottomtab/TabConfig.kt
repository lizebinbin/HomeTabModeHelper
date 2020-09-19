package com.moore.bottomtab

import java.io.Serializable

/**
 * Created by moore on 2019/11/21.
 */
data class TabConfig(
    var tabNormalColor: Int,
    var tabSelectedColor: Int,
    var tabNormalTextSize: Int,
    var tabSelectedTextSize: Int,
    var isTabNameResId: Boolean = true,
    var isTitleVisible: Boolean = true,
    var tabs: List<ItemConfig>
) : Serializable

data class ItemConfig(
    var tabName: String,
    var tabTag: String,
    var iconNormal: String,
    var iconSelected: String,
    var isOverSide: Boolean = false,
    var itemBg: String? = null
) : Serializable