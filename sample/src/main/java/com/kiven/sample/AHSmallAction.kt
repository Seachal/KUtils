package com.kiven.sample

import android.app.ActivityManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.support.animation.DynamicAnimation
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.iflytek.cloud.*
import com.iflytek.msc.MSC
import com.jaredrummler.android.processes.AndroidProcesses
import com.kiven.kutils.activityHelper.KActivityHelper
import com.kiven.kutils.activityHelper.KHelperActivity
import com.kiven.kutils.logHelper.KLog
import com.kiven.kutils.tools.KAlertDialogHelper
import com.kiven.kutils.tools.KGranting
import com.kiven.sample.anim.AHAnim
import com.kiven.sample.service.LiveWallpaper
import com.kiven.sample.service.LiveWallpaper2
import com.kiven.sample.util.WallpaperUtil

/**
 * Created by wangk on 2018/3/28.
 */
class AHSmallAction : KActivityHelper() {
    override fun onCreate(activity: KHelperActivity, savedInstanceState: Bundle?) {
        super.onCreate(activity, savedInstanceState)
        val flexboxLayout = FlexboxLayout(activity)
        flexboxLayout.flexWrap = FlexWrap.WRAP
        flexboxLayout.alignContent = AlignContent.FLEX_START

        setContentView(flexboxLayout)

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
        // TODO: 2018/3/28 ----------------------------------------------------------
        addTitle("检测与杀死app")

        addView("再运行的进程，系统方法", View.OnClickListener {
            val am = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            // 通过调用ActivityManager的getRunningAppServicees()方法获得系统里所有正在运行的进程
            val serviceList = am.runningAppProcesses
            serviceList.forEach {
                KLog.i(it.processName)
            }
        })

        addView("再运行的进程，AndroidProcesses", View.OnClickListener {
            val process = AndroidProcesses.getRunningAppProcesses()
            process.forEach {
                KLog.i("name = ${it.name}, pkgName = ${it.packageName}")
            }
        })
        addView("关闭省心宝", View.OnClickListener {
            val am = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.killBackgroundProcesses("com.jeeinc.save.worry")

            // 需要系统权限（Manifest.permission.FORCE_STOP_PACKAGES），无法获取。
            /*val method = am::class.java.getMethod("forceStopPackage", String::class.java)
            method.invoke(am, "com.jeeinc.save.worry")*/
        })
        // TODO: 2018/3/31 ----------------------------------------------------------
        // Android锁屏实现与总结: https://www.jianshu.com/p/6c3a6b0f145e

        addTitle("壁纸锁屏")
        addView("静态壁纸锁屏", View.OnClickListener {
            val wallPaperManager = WallpaperManager.getInstance(mActivity)

            // FLAG_LOCK 设置锁屏，FLAG_SYSTEM 设置壁纸
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallPaperManager.setResource(R.mipmap.fish, WallpaperManager.FLAG_LOCK)
                Snackbar.make(flexboxLayout, "设置锁屏", Snackbar.LENGTH_LONG).show()
            } else {
                // 7.0以下，似乎只能设置壁纸。7.0及之后，这个方法似乎同时设置壁纸和锁屏
                wallPaperManager.setResource(R.mipmap.fish)
                Snackbar.make(flexboxLayout, "设置壁纸和锁屏", Snackbar.LENGTH_LONG).show()
            }
        })

        // 没有系统权限，用不了
        addView("动态壁纸", View.OnClickListener {
            Snackbar.make(flexboxLayout, "没有系统权限，用不了", Snackbar.LENGTH_LONG).show()
            val intent = Intent(mActivity, LiveWallpaper2::class.java)
            mActivity.startService(intent)
//            WallpaperUtil.setLiveWallpaper(mActivity, 322)
        })

        // TODO: 2018/3/28 ----------------------------------------------------------
        addTitle("语音识别")

        // http://doc.xfyun.cn/msc_android/%E9%A2%84%E5%A4%87%E5%B7%A5%E4%BD%9C.html
        SpeechUtility.createUtility(mActivity, SpeechConstant.APPID + "=5a15147f")
        val mAsr = SpeechRecognizer.createRecognizer(mActivity, {code ->
            KLog.i("SpeechRecognizer init() code = $code")
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：$code")
            }

        })
        // 设置引擎类型
        //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
        mAsr.setParameter( SpeechConstant.CLOUD_GRAMMAR, null )
        mAsr.setParameter( SpeechConstant.SUBJECT, null )

        mAsr.setParameter( SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD )
        mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8")


        /* 其中 "abnf" 指定语法类型为 ABNF,  grammarContent 为语法内容，grammarListener 为构建结果监听器*/
        /*val ret = mAsr.buildGrammar( "abnf", "", {a, b ->

        } )*/

        addView("讯飞识别", View.OnClickListener {
            val ret = mAsr.startListening(mRecognizerListener)
            if (ret == ErrorCode.SUCCESS) {
                showTip("听写失败,错误码：$ret")
            }
        })

        // TODO: 2018/3/28 ----------------------------------------------------------
        addTitle("其他")

        // https://developer.android.google.cn/guide/topics/graphics/spring-animation.html
        addView("动画", View.OnClickListener {
            AHAnim().startActivity(mActivity)
        })
        addView("杀死一个进程杀死一个进程杀死一个进程", View.OnClickListener { })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        KGranting.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showTip(word: String) {
        KAlertDialogHelper.Show1BDialog(mActivity, word)
    }

    private val mRecognizerListener = object : RecognizerListener {

        override fun onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话")
        }

        override fun onError(error: SpeechError) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            if (mTranslateEnable && error.errorCode == 14002) {
                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能")
            } else {
                showTip(error.getPlainDescription(true))
            }
        }

        override fun onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话")
        }

        override fun onResult(results: RecognizerResult, isLast: Boolean) {
            KLog.i(results.resultString)
            if (mTranslateEnable) {
                printTransResult(results)
            } else {
                printResult(results)
            }

            if (isLast) {
                // TODO 最后的结果
            }
        }

        override fun onVolumeChanged(volume: Int, data: ByteArray) {
            showTip("当前正在说话，音量大小：$volume")
            KLog.i("返回音频数据：" + data.size)
        }

        override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    }
}