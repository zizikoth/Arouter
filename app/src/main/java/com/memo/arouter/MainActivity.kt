package com.memo.arouter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.memo.router.core.ARouter
import com.memo.router.path.RouterPath
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		// 方案一 首先在初始化的时候将各个模块的Router存入
		// MemberRouter.putActivity()
		// NewsRouter.putActivity()

		//方案二
		ARouter.get().init(this.applicationContext)

		mBtnMember.setOnClickListener {
			// 方案一 点击跳转
			// val activityClazz = ARouter.get().getActivityClazz(RouterPath.MemberActivity)
			// startActivity(Intent(this, activityClazz))
			// 方案二
			ARouter.get().startActivity(RouterPath.MemberActivity)
		}

	}
}
