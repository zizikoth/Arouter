package com.memo.router.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.memo.router.ClassUtils


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

	/*** 存放ApplicationContext ***/
	private lateinit var mContext: Context

	// 需要一个Map 存放当前App中所有需要跳转的Activity的Class对象
	private val activityMap: HashMap<String, Class<out Activity>> = hashMapOf()

	private object Holder {
		val instance = ARouter()
	}

	companion object {
		@JvmStatic
		fun get() = Holder.instance
	}

	fun init(context: Context) {
		this.mContext = context
		val files = ClassUtils.getFileNameByPackageName(mContext, "com.memo.router.utils")
		files.forEach {
			try {
				val clazz = Class.forName(it)
				// 查看是否实现了IRouter
				if (IRouter::class.java.isAssignableFrom(clazz)) {
					// 开始调用putActivity方法
					(clazz.newInstance() as IRouter).putActivity()
				}
			} catch (e: Exception) {
			}
		}
	}


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


	/*** 通过ApplicationContext 直接跳转 ***/
	fun startActivity(path: String, bundle: Bundle? = null) {
		val clazz = activityMap[path]
		clazz?.let {
			val intent = Intent(mContext, it)
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			if (bundle != null) intent.putExtras(bundle)
			mContext.startActivity(intent)
		}
	}

}