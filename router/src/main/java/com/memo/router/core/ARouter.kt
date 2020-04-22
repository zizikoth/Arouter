package com.memo.router.core

import android.app.Activity

/**
 * title:
 * describe:
 *
 * @author memo
 * @date 2020-04-22 09:34
 * @email zhou_android@163.com
 *
 * Talk is cheap, Show me the code.
 */
class ARouter {

    private object Holder {
        val instance = ARouter()
    }

    companion object {
        fun get() = Holder.instance
    }

    // 需要一个Map 存放当前App中所有需要跳转的Activity的Class对象
    private val activityMap: HashMap<String, Class<out Activity>> = hashMapOf()

    /*** 将地址和Activity的Class对象存入 ***/
    fun setActivityClazz(path: String, clazz: Class<out Activity>) {
        if (path.isNotEmpty()) {
            activityMap.put(path, clazz)
        }
    }

    /*** 读取存放的Activity ***/
    fun getActivityClazz(path: String): Class<out Activity>? {
        return activityMap[path]
    }


}