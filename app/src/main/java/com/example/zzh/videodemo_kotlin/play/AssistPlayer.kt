package com.example.zzh.videodemo_java.play

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import com.example.zzh.videodemo_kotlin.App

import com.example.zzh.videodemo_kotlin.play.DataInter
import com.kk.taurus.playerbase.assist.AssistPlay
import com.kk.taurus.playerbase.assist.OnAssistPlayEventHandler
import com.kk.taurus.playerbase.entity.DataSource
import com.kk.taurus.playerbase.event.OnErrorEventListener
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.log.PLog
import com.kk.taurus.playerbase.player.IPlayer
import com.kk.taurus.playerbase.provider.IDataProvider
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener
import com.kk.taurus.playerbase.receiver.ReceiverGroup
import com.kk.taurus.playerbase.assist.RelationAssist

import java.util.ArrayList

class AssistPlayer private constructor() {

    private val mRelationAssist: RelationAssist

    private val mAppContext: Context

    var dataSource: DataSource? = null

    private val mOnPlayerEventListeners: MutableList<OnPlayerEventListener>
    private val mOnErrorEventListeners: MutableList<OnErrorEventListener>
    private val mOnReceiverEventListeners: MutableList<OnReceiverEventListener>

    private val mInternalPlayerEventListener = OnPlayerEventListener { eventCode, bundle -> callBackPlayerEventListeners(eventCode, bundle) }

    private val mInternalErrorEventListener = OnErrorEventListener { eventCode, bundle -> callBackErrorEventListeners(eventCode, bundle) }

    private val mInternalReceiverEventListener = OnReceiverEventListener { eventCode, bundle -> callBackReceiverEventListeners(eventCode, bundle) }

    private val mInternalEventAssistHandler = object : OnAssistPlayEventHandler() {
        override fun onAssistHandle(assistPlay: AssistPlay, eventCode: Int, bundle: Bundle?) {
            super.onAssistHandle(assistPlay, eventCode, bundle)
            when (eventCode) {
                DataInter.Event.EVENT_CODE_ERROR_SHOW -> reset()
            }
        }
    }

    var receiverGroup: ReceiverGroup?
        get() = mRelationAssist.receiverGroup
        set(receiverGroup) {
            mRelationAssist.receiverGroup = receiverGroup
        }

    val isInPlaybackState: Boolean
        get() {
            val state = state
            PLog.d("AssistPlayer", "isInPlaybackState : state = " + state)
            return (state != IPlayer.STATE_END
                    && state != IPlayer.STATE_ERROR
                    && state != IPlayer.STATE_IDLE
                    && state != IPlayer.STATE_INITIALIZED
                    && state != IPlayer.STATE_PLAYBACK_COMPLETE
                    && state != IPlayer.STATE_STOPPED)
        }

    val isPlaying: Boolean
        get() = mRelationAssist.isPlaying

    val state: Int
        get() = mRelationAssist.state


    init {
        mAppContext = App.instance!!
        mRelationAssist = RelationAssist(mAppContext)
        mRelationAssist.setEventAssistHandler(mInternalEventAssistHandler)
        mRelationAssist.superContainer.setBackgroundColor(Color.BLACK)
        mRelationAssist.receiverGroup = ReceiverGroupManager.get().getLiteReceiverGroup(mAppContext)
        mOnPlayerEventListeners = ArrayList()
        mOnErrorEventListeners = ArrayList()
        mOnReceiverEventListeners = ArrayList()
    }

    fun addOnPlayerEventListener(onPlayerEventListener: OnPlayerEventListener) {
        if (mOnPlayerEventListeners.contains(onPlayerEventListener))
            return
        mOnPlayerEventListeners.add(onPlayerEventListener)
    }

    fun removePlayerEventListener(onPlayerEventListener: OnPlayerEventListener): Boolean {
        return mOnPlayerEventListeners.remove(onPlayerEventListener)
    }

    fun addOnErrorEventListener(onErrorEventListener: OnErrorEventListener) {
        if (mOnErrorEventListeners.contains(onErrorEventListener))
            return
        mOnErrorEventListeners.add(onErrorEventListener)
    }

    fun removeErrorEventListener(onErrorEventListener: OnErrorEventListener): Boolean {
        return mOnErrorEventListeners.remove(onErrorEventListener)
    }

    fun addOnReceiverEventListener(onReceiverEventListener: OnReceiverEventListener) {
        if (mOnReceiverEventListeners.contains(onReceiverEventListener))
            return
        mOnReceiverEventListeners.add(onReceiverEventListener)
    }

    fun removeReceiverEventListener(onReceiverEventListener: OnReceiverEventListener): Boolean {
        return mOnReceiverEventListeners.remove(onReceiverEventListener)
    }

    private fun callBackPlayerEventListeners(eventCode: Int, bundle: Bundle?) {
        for (listener in mOnPlayerEventListeners) {
            listener.onPlayerEvent(eventCode, bundle)
        }
    }

    private fun callBackErrorEventListeners(eventCode: Int, bundle: Bundle?) {
        for (listener in mOnErrorEventListeners) {
            listener.onErrorEvent(eventCode, bundle)
        }
    }

    private fun callBackReceiverEventListeners(eventCode: Int, bundle: Bundle?) {
        for (listener in mOnReceiverEventListeners) {
            listener.onReceiverEvent(eventCode, bundle)
        }
    }

    private fun attachListener() {
        mRelationAssist.setOnPlayerEventListener(mInternalPlayerEventListener)
        mRelationAssist.setOnErrorEventListener(mInternalErrorEventListener)
        mRelationAssist.setOnReceiverEventListener(mInternalReceiverEventListener)
    }

    fun play(userContainer: ViewGroup, dataSource: DataSource?) {
        if (dataSource != null) {
            this.dataSource = dataSource
        }
        attachListener()
        val receiverGroup = receiverGroup
        if (receiverGroup != null && dataSource != null) {
            receiverGroup.groupValue.putBoolean(DataInter.Key.KEY_COMPLETE_SHOW, false)
        }
        mRelationAssist.attachContainer(userContainer)
        if (dataSource != null)
            mRelationAssist.setDataSource(dataSource)
        if (receiverGroup != null && receiverGroup.groupValue.getBoolean(DataInter.Key.KEY_ERROR_SHOW)) {
            return
        }
        if (dataSource != null)
            mRelationAssist.play(true)
    }

    fun setDataProvider(dataProvider: IDataProvider) {
        mRelationAssist.setDataProvider(dataProvider)
    }

    fun pause() {
        mRelationAssist.pause()
    }

    fun resume() {
        mRelationAssist.resume()
    }

    fun stop() {
        mRelationAssist.stop()
    }

    fun reset() {
        mRelationAssist.reset()
    }

    fun destroy() {
        mOnPlayerEventListeners.clear()
        mOnErrorEventListeners.clear()
        mOnReceiverEventListeners.clear()
        mRelationAssist.destroy()
        i = null
    }

    companion object {

        private var i: AssistPlayer? = null

        fun get(): AssistPlayer {
            if (null == i) {
                synchronized(AssistPlayer::class.java) {
                    if (null == i) {
                        i = AssistPlayer()
                    }
                }
            }
            return i!!
        }
    }
}
