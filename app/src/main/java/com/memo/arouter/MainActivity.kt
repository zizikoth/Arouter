package com.memo.arouter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.memo.router.core.ARouter
import com.memo.router.path.RouterPath
import com.memo.utils.MemberRouter
import com.memo.utils.NewsRouter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		// 方案一 首先在初始化的时候将各个模块的Router存入
		MemberRouter.putActivity()
		NewsRouter.putActivity()
		mBtnNews1.setOnClickListener {
			val activityClazz = ARouter.get().getActivityClazz(RouterPath.NewsActivity)
			startActivity(Intent(this, activityClazz))
		}
		mBtnMember1.setOnClickListener {
			val activityClazz = ARouter.get().getActivityClazz(RouterPath.MemberActivity)
			startActivity(Intent(this, activityClazz))
		}

		//方案二å
		ARouter.get().init(this.applicationContext)
		mBtnNews2.setOnClickListener {
			ARouter.get().startActivity(RouterPath.NewsActivity)
		}
		mBtnMember2.setOnClickListener {
			ARouter.get().startActivity(RouterPath.MemberActivity)
		}

	}
}
