package com.example.zzh.videodemo_kotlin.cover

import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.zzh.videodemo_kotlin.R
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.player.IPlayer
import com.kk.taurus.playerbase.receiver.BaseCover
import com.kk.taurus.playerbase.receiver.ICover
import com.kk.taurus.playerbase.receiver.PlayerStateGetter

/**
 * Created by zhangzhihao on 2018/7/6 13:39.
 */
class LoadingCover(context: Context) : BaseCover(context) {
    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_START,
            OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET,
            OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START,
            OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_TO -> setCoverVisibility(View.VISIBLE)

            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START,
            OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_END,
            OnPlayerEventListener.PLAYER_EVENT_ON_STOP,
            OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_ERROR,
            OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE -> setCoverVisibility(View.GONE)
        }
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {
        setCoverVisibility(View.GONE)
    }

    override fun onCreateCoverView(context: Context?): View {
        return View.inflate(context, R.layout.cover_loading, null)
    }

    override fun getCoverLevel(): Int {
        return ICover.COVER_LEVEL_LOW
    }

    override fun onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow()
        val state = playerStateGetter
        if (state != null && isInPlaybackState(state)) {
            if (state.isBuffering) {
                setCoverVisibility(View.VISIBLE)
            } else {
                setCoverVisibility(View.GONE)
            }
        }
    }

    fun isInPlaybackState(playerStateGetter: PlayerStateGetter): Boolean {
        val state = playerStateGetter.state
        return (state != IPlayer.STATE_END
                && state != IPlayer.STATE_ERROR
                && state != IPlayer.STATE_IDLE
                && state != IPlayer.STATE_INITIALIZED
                && state != IPlayer.STATE_STOPPED)
    }
}