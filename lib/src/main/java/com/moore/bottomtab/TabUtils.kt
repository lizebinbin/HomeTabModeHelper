package com.moore.bottomtab

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.regex.Pattern

/**
 * Created by moore on 2019/11/27.
 */
object TabUtils {
    /**
     * 检查颜色是否合法
     */
    fun isColorLegal(color: String): Boolean {
        if (TextUtils.isEmpty(color)) {
            return false
        }
        //#aabbcc
        val matchRegix1 = "^#[0-9a-fA-F]{6}"
        //#00aabbcc
        val matchRegix2 = "^#[0-9a-fA-F]{8}"
        //#ccc
        val matchRegix3 = "^#[0-9a-fA-F]{3}"
        //去匹配
        val compile = Pattern.compile(matchRegix1)
        if (compile.matcher(color).find()) {
            return true
        }
        val compile1 = Pattern.compile(matchRegix2)
        if (compile1.matcher(color).find()) {
            return true
        }
        val compile2 = Pattern.compile(matchRegix3)
        if (compile2.matcher(color).find()) {
            return true
        }
        return false
    }

    fun parseJsonToConfig(inputStream: InputStream): TabConfig? {
        val resultArray = ByteArrayOutputStream()
        val temp = ByteArray(256)
        var read = inputStream.read(temp)
        while (read != -1) {
            resultArray.write(temp, 0, read)
            read = inputStream.read(temp)
            resultArray.flush()
        }
        val readJson = resultArray.toString("utf-8")
        inputStream.close()
        resultArray.close()
        if (readJson.isNotEmpty()) {
            try {
                val obj = JSONObject(readJson)
                //颜色
                val textColorNormal = obj.optString("textColorNormal")
                val textColorSelected = obj.optString("textColorSelected")
                val normalColor = if (isColorLegal(textColorNormal)) {
                    Color.parseColor(textColorNormal)
                } else {
                    Color.BLACK
                }
                val selectedColor = if (isColorLegal(textColorSelected)) {
                    Color.parseColor(textColorSelected)
                } else {
                    Color.GRAY
                }
                val textSizeNormal = obj.optInt("textSizeNormal", 14)
                val textSizeSelected = obj.optInt("textSizeSelected", 14)
                val isTabNameResId = obj.optBoolean("isNameResId", true)
                val isTitleVisible = obj.optBoolean("isTitleVisible", true)
                //标签
                val itemList = ArrayList<ItemConfig>()
                val tabs = obj.optJSONArray("tabs")
                if (tabs != null && tabs.length() > 0) {
                    for (i in 0 until tabs.length()) {
                        val tabJson = tabs.optJSONObject(i)
                        val tabName = tabJson.optString("tabName")
                        val tabTag = tabJson.optString("tabTag")
                        val tabIconNormal = tabJson.optString("iconNormal")
                        val tabIconSelected = tabJson.optString("iconSelected")
                        val isOverSide = tabJson.optBoolean("isOverSide", false)
                        val tabItemConfig =
                            ItemConfig(tabName, tabTag, tabIconNormal, tabIconSelected, isOverSide)
                        val itemBg = tabJson.optString("itemBg", "")
                        tabItemConfig.itemBg = itemBg
                        itemList.add(tabItemConfig)
                    }
                }
                return TabConfig(
                    normalColor,
                    selectedColor,
                    textSizeNormal,
                    textSizeSelected,
                    isTabNameResId,
                    isTitleVisible,
                    itemList
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return null
    }

    fun getResourceDrawableId(context: Context, drawableName: String): Int {
        return context.resources.getIdentifier(drawableName, "drawable", context.packageName)
    }

    fun getStringByResId(context: Context, strResId: String): String {
        val resId = context.resources.getIdentifier(strResId, "string", context.packageName)
        return context.resources.getString(resId)
    }
}