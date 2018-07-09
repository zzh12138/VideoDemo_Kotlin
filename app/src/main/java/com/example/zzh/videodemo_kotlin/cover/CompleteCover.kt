package com.example.zzh.videodemo_kotlin.cover

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.zzh.videodemo_kotlin.R
import com.example.zzh.videodemo_kotlin.play.DataInter
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.receiver.BaseCover
import com.kk.taurus.playerbase.receiver.ICover
import com.kk.taurus.playerbase.receiver.IReceiverGroup

/**
 * Created by zhangzhihao on 2018/7/6 13:56.
 */
class CompleteCover(context: Context) : BaseCover(context) {
    var replay: TextView? = null
    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET -> setPlayCompleteState(false)
            OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE -> setPlayCompleteState(true)
        }
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {
    }

    override fun onCreateCoverView(context: Context?): View {
        return View.inflate(context, R.layout.cover_complete, null)
    }

    override fun onCoverAttachedToWindow() {
        if (groupValue.getBoolean(DataInter.Key.KEY_COMPLETE_SHOW)) {
            setPlayCompleteState(true)
        }
    }

    override fun onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow()
        setCoverVisibility(View.GONE)
    }

    override fun onReceiverBind() {
        super.onReceiverBind()
        replay = findViewById(R.id.cover_complete_replay)
        replay!!.setOnClickListener {
            requestReplay(null)
            setPlayCompleteState(false)
        }
        groupValue.registerOnGroupValueUpdateListener(mOnGroupValueUpdateListener)
    }

    override fun onReceiverUnBind() {
        super.onReceiverUnBind()
        groupValue.unregisterOnGroupValueUpdateListener(mOnGroupValueUpdateListener)
    }

    private val mOnGroupValueUpdateListener = object : IReceiverGroup.OnGroupValueUpdateListener {
        override fun filterKeys(): Array<String> {
            return arrayOf(DataInter.Key.KEY_IS_HAS_NEXT)
        }

        override fun onValueUpdate(key: String, value: Any) {
            if (key == DataInter.Key.KEY_IS_HAS_NEXT) {
                notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_NEXT, null)
                //                        setNextState((Boolean) value);
                //播放下一个的监听
            }
        }
    }

    fun setPlayCompleteState(state: Boolean) {
        if (state) {
            setCoverVisibility(View.VISIBLE)
        } else {
            setCoverVisibility(View.GONE)
        }
        groupValue.putBoolean(DataInter.Key.KEY_COMPLETE_SHOW, state)
    }

    override fun getCoverLevel(): Int {
        return ICover.COVER_LEVEL_MEDIUM
    }
}