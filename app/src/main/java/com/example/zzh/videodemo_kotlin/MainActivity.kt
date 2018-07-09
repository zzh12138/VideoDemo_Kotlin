package com.example.zzh.videodemo_kotlin


import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE
import android.text.TextUtils
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.zzh.videodemo_java.adapter.NewsAdapter
import com.example.zzh.videodemo_java.bean.NewsBean
import com.example.zzh.videodemo_java.play.AssistPlayer
import com.example.zzh.videodemo_kotlin.bean.ViewAttr
import com.example.zzh.videodemo_kotlin.fragment.VideoListFragment
import com.example.zzh.videodemo_kotlin.itemDecoration.LineItemDecoration
import com.example.zzh.videodemo_kotlin.play.DataInter
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NewsAdapter.onVideoTitleClickListener, OnReceiverEventListener, OnPlayerEventListener, VideoListFragment.onBackClickListener {

    companion object {
        const val DURATION = 550L
    }

    var mList: ArrayList<NewsBean>? = null
    var mAdapter: NewsAdapter? = null
    var mFragment: VideoListFragment? = null
    var isShowVideoList: Boolean = false
    var isLandScape = false          //是否横屏
    var mFullContainer: FrameLayout? = null   //全屏container
    var clickPosition: Int = -1       //点击跳到视频列表的pos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        mAdapter = NewsAdapter(mList, this)
        mAdapter!!.setOnVideoTitleClickListener(this)
        recycler.adapter = mAdapter
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(LineItemDecoration(R.drawable.line, this))
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    //滑动屏幕中间开始
                    val manager = recyclerView!!.layoutManager as LinearLayoutManager
                    val first = manager.findFirstVisibleItemPosition()
                    val last = manager.findLastVisibleItemPosition()
                    for (i in first until last+1) {
                        if (!TextUtils.isEmpty(mList!![i].videoUrl)) {
                            //列表视频
                            val view = recyclerView.getChildAt(i - first)
                            val container = view.findViewById<FrameLayout>(R.id.adapter_video_container)
                            val l = IntArray(2)
                            container.getLocationOnScreen(l)
                            val top = l[1] - Util.getStatusBarHeight(applicationContext)//y坐标包括状态栏的，所以要减掉
                            val screenHeight = Util.getScreenHeight(applicationContext)
                            if (top >= screenHeight / 2 - Util.dip2px(applicationContext, 200f) &&
                                    top <= screenHeight / 2 + Util.dip2px(applicationContext, 200f) && !AssistPlayer.get().isPlaying) {
                                val image = view.findViewById<ImageView>(R.id.adapter_video_image)
                                image.performClick()
                                break
                            }
                        }
                    }
                    //滑出屏幕高度一半停止播放
                    val playPosition = mAdapter!!.playPosition
                    if (playPosition != -1) {
                        val view = recyclerView.getChildAt(playPosition - first)
                        if (view != null) {
                            val container = view.findViewById<FrameLayout>(R.id.adapter_video_container)
                            val l = IntArray(2)
                            container.getLocationOnScreen(l)
                            val top = l[1] - Util.getStatusBarHeight(this@MainActivity)
                            if (top < Util.dip2px(this@MainActivity, -100f) ||
                                    top > Util.getScreenHeight(this@MainActivity) - Util.dip2px(this@MainActivity, 100f)) {
                                AssistPlayer.get().stop()
                                mAdapter!!.notifyItemRangeChanged(playPosition, 1)
                                mAdapter!!.playPosition = -1
                            }
                        } else {
                            AssistPlayer.get().stop()
                            mAdapter!!.notifyItemRangeChanged(playPosition, 1)
                            mAdapter!!.playPosition = -1
                        }
                    }
                }
            }
        })
        AssistPlayer.get().addOnReceiverEventListener(this)
        AssistPlayer.get().addOnPlayerEventListener(this)
    }

    fun initData() {
        mList = ArrayList(20)
        for (i in 0..16) {
            val bean = NewsBean()
            bean.type = R.layout.adapter_normal
            bean.title = "我是新闻标题新闻标题我是新闻标题新闻标题我是新闻标题新闻标题" + i
            val result = i % 3
            when (result) {
                0 -> bean.imageUrl = "http://img5.imgtn.bdimg.com/it/u=2539397329,4056054332&fm=27&gp=0.jpg"
                1 -> bean.imageUrl = "http://img3.imgtn.bdimg.com/it/u=3159360602,2315537063&fm=27&gp=0.jpg"
                2 -> bean.imageUrl = "http://img1.imgtn.bdimg.com/it/u=2156236282,1270726641&fm=27&gp=0.jpg"
            }
            mList!!.add(bean)
        }

        val v1 = NewsBean()
        v1.title = "视频新闻1"
        v1.type = R.layout.adapter_video
        v1.imageUrl = "http://img5.imgtn.bdimg.com/it/u=3577771133,2332148944&fm=27&gp=0.jpg"
        v1.videoUrl = "https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4"
        v1.commentNum = 666
        mList!!.add(4, v1)

        val v2 = NewsBean()
        v2.title = "视频新闻2视频新闻2视频新闻2视频新闻2视频新闻2视频新闻2视频新闻2"
        v2.type = R.layout.adapter_video
        v2.imageUrl = "http://img0.imgtn.bdimg.com/it/u=3622851037,3121030191&fm=27&gp=0.jpg"
        v2.videoUrl = "https://mov.bn.netease.com/open-movie/nos/mp4/2018/01/12/SD70VQJ74_sd.mp4"
        v2.commentNum = 666
        mList!!.add(9, v2)

        val v3 = NewsBean()
        v3.title = "视频新闻3视频新闻3视频新闻3视频新闻3视频新闻3视频新闻3视频新闻3"
        v3.type = R.layout.adapter_video
        v3.imageUrl = "http://img5.imgtn.bdimg.com/it/u=3974436224,4269321529&fm=27&gp=0.jpg"
        v3.videoUrl = "https://mov.bn.netease.com/open-movie/nos/mp4/2017/12/04/SD3SUEFFQ_hd.mp4"
        v3.commentNum = 666
        mList!!.add(10, v3)
    }

    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE -> AssistPlayer.get().stop()
        }
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            DataInter.Event.EVENT_CODE_REQUEST_BACK -> onBackPressed()
            DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN -> requestedOrientation = if (isLandScape)
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }
    //点击标题，添加视频列表list
    override fun onTitleClick(position: Int, attr: ViewAttr) {
        if (mFragment == null) {
            mFragment = VideoListFragment()
            mFragment!!.backClickListener=this
        }
        clickPosition = position
        isShowVideoList = true
        val bundle = Bundle()
        bundle.putParcelable("attr", attr)
        bundle.putParcelable("news", mList!![position])
        bundle.putBoolean("isAttach", AssistPlayer.get().isPlaying)
        mFragment!!.arguments = bundle
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.root, mFragment)
        transaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        recycler.clearOnScrollListeners()
        AssistPlayer.get().removeReceiverEventListener(this)
        AssistPlayer.get().removePlayerEventListener(this)
        AssistPlayer.get().destroy()
    }

    override fun onBackPressed() {
        if (isLandScape) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else if (isShowVideoList) {
            //显示了视频列表
            if (mFragment!!.isShowComment) {
                //显示了评论数据
                mFragment!!.closeCommentFragment()
            } else {
                removeVideoListFragment()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun removeVideoListFragment() {
        isShowVideoList = false
        if (mFragment!!.isPlayingFirst()) {
            mFragment!!.removeVideoList()
            recycler.postDelayed( {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.remove(mFragment)
                transaction.commit()
                val holder = recycler.findViewHolderForLayoutPosition(clickPosition) as NewsAdapter.VideoHolder
                AssistPlayer.get().play(holder.container, null)
            }, 800)
        } else {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.remove(mFragment)
            transaction.commit()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        isLandScape = newConfig!!.orientation == Configuration.ORIENTATION_LANDSCAPE
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            attachFullScreen()
        } else {
            attachList()
        }
        AssistPlayer.get().receiverGroup!!.groupValue.putBoolean(DataInter.Key.KEY_IS_LANDSCAPE, isLandScape)
    }

    private fun attachFullScreen() {
        AssistPlayer.get().receiverGroup!!.groupValue.putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true)
        if (mFullContainer == null) {
            mFullContainer = FrameLayout(this)
            mFullContainer!!.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }
        root.addView(mFullContainer, -1)
        AssistPlayer.get().play(mFullContainer!!, null)
    }

    private fun attachList() {
        root.removeView(mFullContainer)
        AssistPlayer.get().receiverGroup!!.groupValue.putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false)
        if (isShowVideoList) {
            if (mFragment!!.isShowComment) {
                //绑定回评论页面
                mFragment!!.attachCommentContainer()
            } else {
                //绑定回视频列表页面
                mFragment!!.attachList()
            }
        } else {
            if (mAdapter != null) {
                val holder = recycler.findViewHolderForLayoutPosition(mAdapter!!.playPosition) as NewsAdapter.VideoHolder
                AssistPlayer.get().play(holder.container, null)
            }
        }
    }
}
