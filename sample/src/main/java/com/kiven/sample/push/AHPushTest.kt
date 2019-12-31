package com.kiven.sample.push

import android.Manifest
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.kiven.kutils.activityHelper.KActivityDebugHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KGranting
import com.xiaomi.mipush.sdk.MiPushClient
import org.jetbrains.anko.support.v4.nestedScrollView

/**
 * Created by oukobayashi on 2019-12-31.
 * 小米推送文档：https://dev.mi.com/console/doc/detail?pId=41
 */
class AHPushTest : KActivityDebugHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        mActivity.nestedScrollView { addView(flexboxLayout) }

        val addTitle = fun(text: String) {
            val tv = TextView(activity)
            tv.text = text
            tv.layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            flexboxLayout.addView(tv)
        }

        val addView = fun(text: String, click: View.OnClickListener) {
            val btn = Button(activity)
            btn.text = text
            btn.setOnClickListener(click)
            flexboxLayout.addView(btn)
        }
        addTitle("小米推送")
        addView("注册", View.OnClickListener {
            KGranting.requestPermissions(mActivity, 3344, arrayOf(
                    Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), arrayOf("拨号", "存储")) {
                if (it) {
                    MiPushHelper.initMiPush(mActivity)
                }
            }
        })
        addView("注销", View.OnClickListener { MiPushClient.unregisterPush(mActivity) })

        addView("禁用推送服务", View.OnClickListener { MiPushClient.disablePush(mActivity)})
        addView("启用推送服务", View.OnClickListener { MiPushClient.enablePush(mActivity) })

        addView("暂停接收推送", View.OnClickListener { MiPushClient.pausePush(mActivity, null) })
        addView("恢复接收推送", View.OnClickListener { MiPushClient.resumePush(mActivity, null)})

        addView("设置账号", View.OnClickListener { MiPushClient.setUserAccount(mActivity, "1", null) })
        addView("清除账号", View.OnClickListener { MiPushClient.unsetUserAccount(mActivity, "1", null) })

        addView("设置别名", View.OnClickListener { MiPushClient.setAlias(mActivity, "user", null) })
        addView("清除别名", View.OnClickListener { MiPushClient.unsetAlias(mActivity, "user", null) })

        addView("订阅topic", View.OnClickListener { MiPushClient.subscribe(mActivity, "topic1", null) })
        addView("取消topic", View.OnClickListener { MiPushClient.unsubscribe(mActivity, "topic1", null) })

        addView("打印RegId和Region", View.OnClickListener {
            KLog.i("RegId: ${MiPushClient.getRegId(mActivity)}")
            KLog.i("Region: ${MiPushClient.getAppRegion(mActivity)}")
        })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
        addView("", View.OnClickListener { })
    }
}