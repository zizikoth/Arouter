package com.memo.member

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.memo.annotation.Route
import com.memo.router.core.ARouter
import com.memo.router.path.RouterPath
import kotlinx.android.synthetic.main.activity_member.*

@Route(path = RouterPath.MemberActivity)
class MemberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)
        title = "Member"
        mBtn.setOnClickListener {
            // 方案一
            // val clazz = ARouter.get().getActivityClazz(RouterPath.NewsActivity)
            // startActivity(Intent(this,clazz))

            // 方案二
            ARouter.get().startActivity(RouterPath.NewsActivity)
        }
    }
}
