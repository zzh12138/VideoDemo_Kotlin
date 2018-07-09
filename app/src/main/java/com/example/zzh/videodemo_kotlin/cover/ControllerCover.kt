package com.example.zzh.videodemo_java.cover

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.example.zzh.videodemo_kotlin.R
import com.example.zzh.videodemo_kotlin.play.DataInter
import com.kk.taurus.playerbase.entity.DataSource
import com.kk.taurus.playerbase.event.BundlePool
import com.kk.taurus.playerbase.event.EventKey
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.log.PLog
import com.kk.taurus.playerbase.player.IPlayer
import com.kk.taurus.playerbase.player.OnTimerUpdateListener
import com.kk.taurus.playerbase.receiver.ICover
import com.kk.taurus.playerbase.receiver.IReceiverGroup
import com.kk.taurus.playerbase.touch.OnTouchGestureListener
import com.kk.taurus.playerbase.receiver.BaseCover
import com.kk.taurus.playerbase.utils.TimeUtil


/**
 * Created by Taurus on 2018/4/15.
 */

class ControllerCover(context: Context) : BaseCover(context), OnTimerUpdateListener, OnTouchGestureListener, View.OnClickListener {

    private val MSG_CODE_DELAY_HIDDEN_CONTROLLER = 101

    private var mTopContainer: View? = null
    private var mBottomContainer: View? = null
    private var mBackIcon: ImageView? = null
    private var mTopTitle: TextView? = null
    private var mStateIcon: ImageView? = null
    private var mCurrTime: TextView? = null
    private var mTotalTime: TextView? = null
    private var mSwitchScreen: ImageView? = null
    private var mSeekBar: SeekBar? = null

    private var mBufferPercentage: Int = 0

    private var mSeekProgress = -1

    private var mTimerUpdateProgressEnable = true

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_CODE_DELAY_HIDDEN_CONTROLLER -> {
                    PLog.d(tag.toString(), "msg_delay_hidden...")
                    setControllerState(false)
                }
            }
        }
    }

    private var mGestureEnable = true

    private var mTimeFormat: String? = null

    private var mControllerTopEnable: Boolean = false
    private var mBottomAnimator: ObjectAnimator? = null
    private var mTopAnimator: ObjectAnimator? = null


    private val mOnGroupValueUpdateListener = object : IReceiverGroup.OnGroupValueUpdateListener {
        override fun filterKeys(): Array<String> {
            return arrayOf(DataInter.Key.KEY_COMPLETE_SHOW, DataInter.Key.KEY_TIMER_UPDATE_ENABLE, DataInter.Key.KEY_DATA_SOURCE, DataInter.Key.KEY_IS_LANDSCAPE, DataInter.Key.KEY_CONTROLLER_TOP_ENABLE)
        }

        override fun onValueUpdate(key: String, value: Any) {
            if (key == DataInter.Key.KEY_COMPLETE_SHOW) {
                val show = value as Boolean
                if (show) {
                    setControllerState(false)
                }
                setGestureEnable(!show)
            } else if (key == DataInter.Key.KEY_CONTROLLER_TOP_ENABLE) {
                mControllerTopEnable = value as Boolean
                if (!mControllerTopEnable) {
                    setTopContainerState(false)
                }
            } else if (key == DataInter.Key.KEY_IS_LANDSCAPE) {
                setSwitchScreenIcon(value as Boolean)
            } else if (key == DataInter.Key.KEY_TIMER_UPDATE_ENABLE) {
                mTimerUpdateProgressEnable = value as Boolean
            } else if (key == DataInter.Key.KEY_DATA_SOURCE) {
                val dataSource = value as DataSource
                setTitle(dataSource)
            }
        }
    }

    private val onSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (fromUser)
                updateUI(progress, seekBar.max)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            sendSeekEvent(seekBar.progress)
        }
    }

    private val mSeekEventRunnable = Runnable {
        if (mSeekProgress < 0)
            return@Runnable
        val bundle = BundlePool.obtain()
        bundle.putInt(EventKey.INT_DATA, mSeekProgress)
        requestSeek(bundle)
    }

    private val isControllerShow: Boolean
        get() = mBottomContainer!!.visibility == View.VISIBLE

    override fun onReceiverBind() {
        super.onReceiverBind()
        mTopContainer = findViewById(R.id.cover_player_controller_top_container)
        mBottomContainer = findViewById(R.id.cover_player_controller_bottom_container)
        mBackIcon = findViewById(R.id.cover_player_controller_image_view_back_icon)
        mTopTitle = findViewById(R.id.cover_player_controller_text_view_video_title)
        mStateIcon = findViewById(R.id.cover_player_controller_image_view_play_state)
        mCurrTime = findViewById(R.id.cover_player_controller_text_view_curr_time)
        mTotalTime = findViewById(R.id.cover_player_controller_text_view_total_time)
        mSwitchScreen = findViewById(R.id.cover_player_controller_image_view_switch_screen)
        mSeekBar = findViewById(R.id.cover_player_controller_seek_bar)
        mBackIcon!!.setOnClickListener(this)
        mStateIcon!!.setOnClickListener(this)
        mSwitchScreen!!.setOnClickListener(this)
        mSeekBar!!.setOnSeekBarChangeListener(onSeekBarChangeListener)

        groupValue.registerOnGroupValueUpdateListener(mOnGroupValueUpdateListener)

    }

    override fun onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow()
        val dataSource = groupValue.get<DataSource>(DataInter.Key.KEY_DATA_SOURCE)
        setTitle(dataSource)

        val topEnable = groupValue.getBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false)
        mControllerTopEnable = topEnable
        if (!topEnable) {
            setTopContainerState(false)
        }

        val screenSwitchEnable = groupValue.getBoolean(DataInter.Key.KEY_CONTROLLER_SCREEN_SWITCH_ENABLE, true)
        setScreenSwitchEnable(screenSwitchEnable)
    }

    override fun onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow()
        mTopContainer!!.visibility = View.GONE
        mBottomContainer!!.visibility = View.GONE
        removeDelayHiddenMessage()
    }

    override fun onReceiverUnBind() {
        super.onReceiverUnBind()

        cancelTopAnimation()
        cancelBottomAnimation()

        groupValue.unregisterOnGroupValueUpdateListener(mOnGroupValueUpdateListener)
        removeDelayHiddenMessage()
        mHandler.removeCallbacks(mSeekEventRunnable)


    }

    private fun sendSeekEvent(progress: Int) {
        mTimerUpdateProgressEnable = false
        mSeekProgress = progress
        mHandler.removeCallbacks(mSeekEventRunnable)
        mHandler.postDelayed(mSeekEventRunnable, 300)
    }

    private fun setTitle(dataSource: DataSource?) {
        if (dataSource != null) {
            val title = dataSource.title
            if (!TextUtils.isEmpty(title)) {
                setTitle(title)
                return
            }
            val data = dataSource.data
            if (!TextUtils.isEmpty(data)) {
                setTitle(data)
            }
        }
    }

    private fun setTitle(text: String) {
        mTopTitle!!.text = text
    }

    private fun setSwitchScreenIcon(isFullScreen: Boolean) {
        mSwitchScreen!!.setImageResource(if (isFullScreen) R.drawable.icon_exit_full_screen else R.drawable.icon_full_screen)
    }

    private fun setScreenSwitchEnable(screenSwitchEnable: Boolean) {
        mSwitchScreen!!.visibility = if (screenSwitchEnable) View.VISIBLE else View.GONE
    }

    private fun setGestureEnable(gestureEnable: Boolean) {
        this.mGestureEnable = gestureEnable
    }

    private fun cancelTopAnimation() {
        if (mTopAnimator != null) {
            mTopAnimator!!.cancel()
            mTopAnimator!!.removeAllListeners()
            mTopAnimator!!.removeAllUpdateListeners()
        }
    }

    private fun setTopContainerState(state: Boolean) {
        if (mControllerTopEnable) {
            mTopContainer!!.clearAnimation()
            cancelTopAnimation()
            mTopAnimator = ObjectAnimator.ofFloat(mTopContainer,
                    "alpha", if (state) 0f else 1f, if (state) 1f else 0f).setDuration(300)
            mTopAnimator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    if (state) {
                        mTopContainer!!.visibility = View.VISIBLE
                    }
                }

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (!state) {
                        mTopContainer!!.visibility = View.GONE
                    }
                }
            })
            mTopAnimator!!.start()
        } else {
            mTopContainer!!.visibility = View.GONE
        }
    }

    private fun cancelBottomAnimation() {
        if (mBottomAnimator != null) {
            mBottomAnimator!!.cancel()
            mBottomAnimator!!.removeAllListeners()
            mBottomAnimator!!.removeAllUpdateListeners()
        }
    }

    private fun setBottomContainerState(state: Boolean) {
        mBottomContainer!!.clearAnimation()
        cancelBottomAnimation()
        mBottomAnimator = ObjectAnimator.ofFloat(mBottomContainer,
                "alpha", if (state) 0f else 1f, if (state) 1f else 0f).setDuration(300)
        mBottomAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                if (state) {
                    mBottomContainer!!.visibility = View.VISIBLE
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (!state) {
                    mBottomContainer!!.visibility = View.GONE
                }
            }
        })
        mBottomAnimator!!.start()
        if (state) {
            PLog.d(tag.toString(), "requestNotifyTimer...")
            requestNotifyTimer()
        } else {
            PLog.d(tag.toString(), "requestStopTimer...")
            requestStopTimer()
        }
    }

    private fun setControllerState(state: Boolean) {
        if (state) {
            sendDelayHiddenMessage()
        } else {
            removeDelayHiddenMessage()
        }
        setTopContainerState(state)
        setBottomContainerState(state)
    }

    private fun toggleController() {
        if (isControllerShow) {
            setControllerState(false)
        } else {
            setControllerState(true)
        }
    }

    private fun sendDelayHiddenMessage() {
        removeDelayHiddenMessage()
        mHandler.sendEmptyMessageDelayed(MSG_CODE_DELAY_HIDDEN_CONTROLLER, 5000)
    }

    private fun removeDelayHiddenMessage() {
        mHandler.removeMessages(MSG_CODE_DELAY_HIDDEN_CONTROLLER)
    }

    private fun setCurrTime(curr: Int) {
        mCurrTime!!.text = TimeUtil.getTime(mTimeFormat, curr.toLong())
    }

    private fun setTotalTime(duration: Int) {
        mTotalTime!!.text = TimeUtil.getTime(mTimeFormat, duration.toLong())
    }

    private fun setSeekProgress(curr: Int, duration: Int) {
        mSeekBar!!.max = duration
        mSeekBar!!.progress = curr
        val secondProgress = mBufferPercentage * 1.0f / 100 * duration
        setSecondProgress(secondProgress.toInt())
    }

    private fun setSecondProgress(secondProgress: Int) {
        mSeekBar!!.secondaryProgress = secondProgress
    }

    override fun onTimerUpdate(curr: Int, duration: Int, bufferPercentage: Int) {
        if (!mTimerUpdateProgressEnable)
            return
        if (mTimeFormat == null) {
            mTimeFormat = TimeUtil.getFormat(duration.toLong())
        }
        mBufferPercentage = bufferPercentage
        updateUI(curr, duration)
    }

    private fun updateUI(curr: Int, duration: Int) {
        setSeekProgress(curr, duration)
        setCurrTime(curr)
        setTotalTime(duration)
    }

    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET -> {
                mBufferPercentage = 0
                mTimeFormat = null
                updateUI(0, 0)
                val data = bundle!!.getSerializable(EventKey.SERIALIZABLE_DATA) as DataSource
                groupValue.putObject(DataInter.Key.KEY_DATA_SOURCE, data)
                setTitle(data)
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_STATUS_CHANGE -> {
                val status = bundle!!.getInt(EventKey.INT_DATA)
                if (status == IPlayer.STATE_PAUSED) {
                    mStateIcon!!.isSelected = true
                } else if (status == IPlayer.STATE_STARTED) {
                    mStateIcon!!.isSelected = false
                }
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START, OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE -> mTimerUpdateProgressEnable = true
        }
    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {

    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {

    }

    override fun onPrivateEvent(eventCode: Int, bundle: Bundle?): Bundle? {
        when (eventCode) {
            DataInter.PrivateEvent.EVENT_CODE_UPDATE_SEEK -> if (bundle != null) {
                val curr = bundle.getInt(EventKey.INT_ARG1)
                val duration = bundle.getInt(EventKey.INT_ARG2)
                updateUI(curr, duration)
            }
        }
        return null
    }

    override fun onCreateCoverView(context: Context): View {
        return View.inflate(context, R.layout.cover_controller, null)
    }

    override fun getCoverLevel(): Int {
        return ICover.COVER_LEVEL_LOW
    }

    override fun onSingleTapUp(event: MotionEvent) {
        if (!mGestureEnable)
            return
        toggleController()
    }

    override fun onDoubleTap(event: MotionEvent) {}

    override fun onDown(event: MotionEvent) {}

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float) {
        if (!mGestureEnable)
            return
    }

    override fun onEndGesture() {}

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cover_player_controller_image_view_back_icon -> notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_BACK, null)
            R.id.cover_player_controller_image_view_play_state -> {
                val selected = mStateIcon!!.isSelected
                if (selected) {
                    requestResume(null)
                } else {
                    requestPause(null)
                }
                mStateIcon!!.isSelected = !selected
            }
            R.id.cover_player_controller_image_view_switch_screen -> notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN, null)
        }
    }
}
