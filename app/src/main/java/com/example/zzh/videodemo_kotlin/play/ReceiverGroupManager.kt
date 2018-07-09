package com.example.zzh.videodemo_java.play

import android.content.Context
import com.example.zzh.videodemo_java.cover.ControllerCover
import com.example.zzh.videodemo_kotlin.cover.CompleteCover
import com.example.zzh.videodemo_kotlin.cover.ErrorCover
import com.example.zzh.videodemo_kotlin.cover.LoadingCover


import com.example.zzh.videodemo_kotlin.play.DataInter.ReceiverKey.*
import com.kk.taurus.playerbase.receiver.GroupValue
import com.kk.taurus.playerbase.receiver.ReceiverGroup



/**
 * Created by zhangzhihao on 2018/6/19 15:34.
 */

class ReceiverGroupManager private constructor() {

    fun getLittleReceiverGroup(context: Context): ReceiverGroup {
        return getLiteReceiverGroup(context, null)
    }

    fun getLittleReceiverGroup(context: Context, groupValue: GroupValue): ReceiverGroup {
        val receiverGroup = ReceiverGroup(groupValue)
        receiverGroup.addReceiver(KEY_LOADING_COVER, LoadingCover(context))
        receiverGroup.addReceiver(KEY_COMPLETE_COVER, CompleteCover(context))
        receiverGroup.addReceiver(KEY_ERROR_COVER, ErrorCover(context))
        return receiverGroup
    }

    @JvmOverloads
    fun getLiteReceiverGroup(context: Context, groupValue: GroupValue? = null): ReceiverGroup {
        val receiverGroup = ReceiverGroup(groupValue)
        receiverGroup.addReceiver(KEY_LOADING_COVER, LoadingCover(context))
        receiverGroup.addReceiver(KEY_CONTROLLER_COVER, ControllerCover(context))
        receiverGroup.addReceiver(KEY_COMPLETE_COVER, CompleteCover(context))
        receiverGroup.addReceiver(KEY_ERROR_COVER, ErrorCover(context))
        return receiverGroup
    }

    @JvmOverloads
    fun getReceiverGroup(context: Context, groupValue: GroupValue? = null): ReceiverGroup {
        val receiverGroup = ReceiverGroup(groupValue)
        receiverGroup.addReceiver(KEY_LOADING_COVER, LoadingCover(context))
        receiverGroup.addReceiver(KEY_CONTROLLER_COVER, ControllerCover(context))
        //        receiverGroup.addReceiver(KEY_GESTURE_COVER, new GestureCover(context));
        receiverGroup.addReceiver(KEY_COMPLETE_COVER, CompleteCover(context))
        receiverGroup.addReceiver(KEY_ERROR_COVER, ErrorCover(context))
        return receiverGroup
    }

    companion object {

        private var i: ReceiverGroupManager? = null

        fun get(): ReceiverGroupManager {
            if (null == i) {
                synchronized(ReceiverGroupManager::class.java) {
                    if (null == i) {
                        i = ReceiverGroupManager()
                    }
                }
            }
            return i!!
        }
    }

}

