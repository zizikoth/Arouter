package com.memo.news

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.memo.router.core.ARouter
import com.memo.router.path.RouterPath
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        title = "News"
        mBtn.setOnClickListener {
            val clazz = ARouter.get().getActivityClazz(RouterPath.MemberActivity)
            startActivity(Intent(this, clazz))
        }
    }
}
