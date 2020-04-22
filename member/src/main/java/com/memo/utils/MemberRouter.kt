package com.memo.utils

import com.memo.member.MemberActivity
import com.memo.router.core.ARouter
import com.memo.router.core.IRouter
import com.memo.router.path.RouterPath

/**
 * title:路由工具类，将需要跳转的MemberActivity的class存入ARouter集合中
 * describe:
 *
 * @author memo
 * @date 2020-04-22 09:47
 * @email zhou_android@163.com
 *
 * Talk is cheap, Show me the code.
 */
object MemberRouter : IRouter {

    override fun putActivity() {
        ARouter.get().setActivityClazz(RouterPath.MemberActivity, MemberActivity::class.java)
    }
}