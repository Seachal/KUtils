package com.kiven.pushlibrary.mi

import android.content.Context
import android.content.pm.PackageManager
import com.kiven.kutils.logHelper.KLog
import com.kiven.pushlibrary.PushHelper
import com.xiaomi.channel.commonutils.logger.LoggerInterface
import com.xiaomi.mipush.sdk.Logger
import com.xiaomi.mipush.sdk.MiPushClient

/**
 * 接入指南：https://dev.mi.com/console/doc/detail?pId=100
 * SDK使用指南：https://dev.mi.com/console/doc/detail?pId=41
 */
class MiPushHelper : PushHelper {
    override fun initPush(context: Context) {
//        if (!shouldInitMiPush()) return

        val manifest = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
        val bundleData = manifest.metaData ?: throw Throwable("manifest中小米配置信息为空")

        val appId = bundleData.get("mi_app_id")?.toString() ?: throw Throwable("小米AppID为空")
        val appKey = bundleData.get("mi_app_key")?.toString() ?: throw Throwable("小米APPKey为空")

        //初始化push推送服务
        // TODO: 2019-12-31  在非MIUI平台下，如果targetSdkVersion>=23，需要动态申请电话和存储权限，请在申请权限后再调用注册接口，否则会注册失败。
//        MiPushClient.registerPush(context, "2882303761518292808", "5681829285808")
        MiPushClient.registerPush(context, appId, appKey)

        if (KLog.isDebug()){
            //打开Log
            val newLogger: LoggerInterface = object : LoggerInterface {
                override fun setTag(tag: String) { // ignore
                    KLog.i("MiPush注册标签:$tag")
                }

                override fun log(content: String, t: Throwable) {
                    KLog.e(Throwable("MiPush:$content", t))
                }

                override fun log(content: String) {
                    KLog.i("MiPush:$content")
                }
            }
            Logger.setLogger(context, newLogger)
        }
    }

    override fun setTags(context: Context, tags: Set<String>) {
        val oldTags = MiPushClient.getAllTopic(context)

        val addTags = mutableSetOf<String>()
        val delTags = mutableSetOf<String>()


        if (oldTags == null || oldTags.isEmpty()) {
            addTags.addAll(tags)
        } else {
            oldTags.forEach {
                if (!tags.contains(it)) delTags.add(it)
            }

            tags.forEach {
                if (!oldTags.contains(it)) addTags.add(it)
            }
        }

        addTags.forEach { tag ->
            MiPushClient.subscribe(context, tag, null)
        }

        delTags.forEach { tag ->
            MiPushClient.unsubscribe(context, tag, null)
        }
    }

    override fun setAccount(context: Context, account: String) {
        if (account.isBlank()) {
            MiPushClient.getAllUserAccount(context).forEach {
                MiPushClient.unsetUserAccount(context, it, null)
            }
        }else {
            MiPushClient.setUserAccount(context, account, null)
        }
    }
}