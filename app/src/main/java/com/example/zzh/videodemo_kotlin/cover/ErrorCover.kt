package com.example.zzh.videodemo_kotlin.cover

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.zzh.videodemo_kotlin.R
import com.example.zzh.videodemo_kotlin.play.DataInter
import com.kk.taurus.playerbase.event.BundlePool
import com.kk.taurus.playerbase.event.EventKey
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.receiver.BaseCover
import com.kk.taurus.playerbase.receiver.ICover
import com.kk.taurus.playerbase.utils.NetworkUtils

/**
 * Created by zhangzhihao on 2018/7/6 13:46.
 */
class ErrorCover(context: Context) : BaseCover(context) {
    var retry: TextView? = null
    var mCurrPosition: Int = 0
    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET -> {
                mCurrPosition = 0
                setCoverVisibility(View.GONE)
                groupValue.putBoolean(DataInter.Key.KEY_ERROR_SHOW, false)
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_TIMER_UPDATE -> mCurrPosition = bundle!!.getInt(EventKey.INT_ARG1)
        }
    }

    override fun onReceiverBind() {
        super.onReceiverBind()
        retry = findViewById(R.id.cover_error_retry)
        retry!!.setOnClickListener {
            val bundle = BundlePool.obtain()
            bundle.putInt(EventKey.INT_DATA, mCurrPosition)
            setCoverVisibility(View.GONE)
            groupValue.putBoolean(DataInter.Key.KEY_ERROR_SHOW, false)
            requestRetry(bundle)
        }
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {
        setCoverVisibility(View.VISIBLE)
        notifyReceiverEvent(DataInter.Event.EVENT_CODE_ERROR_SHOW, null)
        groupValue.putBoolean(DataInter.Key.KEY_ERROR_SHOW, true)
    }

    override fun onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow()
        val state = NetworkUtils.getNetworkState(context)
        if (state < 0) {
            notifyReceiverEvent(DataInter.Event.EVENT_CODE_ERROR_SHOW, null)
            setCoverVisibility(View.VISIBLE)
            groupValue.putBoolean(DataInter.Key.KEY_ERROR_SHOW, true)
        }
    }

    override fun getCoverLevel(): Int {
        return ICover.COVER_LEVEL_MEDIUM
    }

    override fun onCreateCoverView(context: Context?): View {
        return View.inflate(context, R.layout.cover_error, null)
    }

}