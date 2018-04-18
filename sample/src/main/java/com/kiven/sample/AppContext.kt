package com.kiven.sample

import android.content.Context
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechUtility

import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KContext
import com.kiven.kutils.tools.KUtil

/**
 *
 * Created by kiven on 2017/2/16.
 */

class AppContext : KContext() {


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        KLog.i("AppContext attachBaseContext")
    }

    override fun init() {
        super.init()
        KLog.i("AppContext init")
        KUtil.setApp(this)
    }
}
