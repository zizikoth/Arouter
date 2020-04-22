package com.memo.news

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.memo.annotation.Route
import com.memo.router.core.ARouter
import com.memo.router.path.RouterPath
import kotlinx.android.synthetic.main.activity_news.*

@Route(path = RouterPath.NewsActivity)
class NewsActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_news)
		title = "News"
		mBtn.setOnClickListener {
			// 方案一
			// val clazz = ARouter.get().getActivityClazz(RouterPath.MemberActivity)
			// startActivity(Intent(this, clazz))
			// 方案二
			ARouter.get().startActivity(RouterPath.MemberActivity)
		}
	}
}
