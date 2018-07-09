package com.example.zzh.videodemo_kotlin.fragment

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.zzh.videodemo_java.bean.NewsBean
import com.example.zzh.videodemo_java.play.AssistPlayer
import com.example.zzh.videodemo_kotlin.MainActivity
import com.example.zzh.videodemo_kotlin.MainActivity.Companion.DURATION
import com.example.zzh.videodemo_kotlin.R
import com.example.zzh.videodemo_kotlin.Util
import com.example.zzh.videodemo_kotlin.adapter.VideoListAdapter
import com.example.zzh.videodemo_kotlin.bean.ViewAttr
import com.example.zzh.videodemo_kotlin.itemDecoration.EmptyItemDecoration
import com.example.zzh.videodemo_kotlin.play.DataInter
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator
import java.util.ArrayList

/**
 * Created by zhangzhihao on 2018/7/6 14:39.
 */
class VideoListFragment : Fragment(), VideoListAdapter.onAnimationFinishListener, VideoListAdapter.onVideoPlayClickListener, VideoListAdapter.onMessageClickListener, OnPlayerEventListener, CommentFragment.onCloseClickListener {


    var mList: ArrayList<NewsBean>? = null
    var mAdapter: VideoListAdapter? = null
    var mAttr: ViewAttr? = null
    var commentFragment: CommentFragment? = null
    var isShowComment: Boolean = false
    var backClickListener: onBackClickListener? = null
    lateinit var recycler: RecyclerView
    lateinit var root: FrameLayout
    lateinit var back: ImageView
    lateinit var container: FrameLayout
    lateinit var top_layout: FrameLayout


    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_video_list, c, false)
        recycler = view.findViewById(R.id.recycler)
        root = view.findViewById(R.id.root)
        back = view.findViewById(R.id.back)
        container = view.findViewById(R.id.container)
        top_layout = view.findViewById(R.id.top_layout)
        initData()
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    val manager = recyclerView!!.layoutManager as LinearLayoutManager
                    val first = manager.findFirstVisibleItemPosition()
                    val pos = manager.findFirstCompletelyVisibleItemPosition()
                    if (pos != mAdapter!!.mPlayPosition) {
                        val view = recyclerView.getChildAt(pos - first)
                        val image = view.findViewById<ImageView>(R.id.adapter_video_list_image)
                        image.performClick()
                    }
                }
            }
        })
        AssistPlayer.get().receiverGroup!!.groupValue.putBoolean(DataInter.Key.KEY_IS_HAS_NEXT, true)
        AssistPlayer.get().addOnPlayerEventListener(this)
        return view
    }

    fun initData() {
        mAttr = arguments!!.getParcelable("attr")
        val bean: NewsBean = arguments!!.getParcelable("news")
        mList = ArrayList(5)
        mList!!.add(bean)
        mAdapter = VideoListAdapter(context!!, mList!!)
        mAdapter!!.isAttach = arguments!!.getBoolean("isAttach", false)
        mAdapter!!.attr = mAttr
        mAdapter!!.mOnAnimationFinishListener = this
        mAdapter!!.mOnMessageClickListener = this
        mAdapter!!.mOnVideoPlayClickListener = this
        recycler.adapter = mAdapter
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.itemAnimator = FadeInUpAnimator()
        recycler.addItemDecoration(EmptyItemDecoration(Util.dip2px(context!!, 40f)))
        back.setOnClickListener {
            if (isShowComment) {
                closeCommentFragment()
            } else {
                backClickListener?.removeVideoListFragment()
            }
        }
        //背景颜色从透明黑色过度到黑色
        val animator = ObjectAnimator.ofInt(root, "backgroundColor", Color.parseColor("#00000000"), Color.parseColor("#ff000000"))
        animator.setEvaluator(ArgbEvaluator())
        animator.duration = MainActivity.DURATION
        animator.start()
        if (!mAdapter!!.isAttach) {
            onAnimationEnd()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        AssistPlayer.get().receiverGroup!!.groupValue.putBoolean(DataInter.Key.KEY_IS_HAS_NEXT, false)
        AssistPlayer.get().removePlayerEventListener(this)
        recycler.clearOnScrollListeners()
    }

    //显示评论页面
    override fun onMessageClick(bean: NewsBean, attr: ViewAttr) {
        isShowComment = true
        if (commentFragment == null) {
            commentFragment = CommentFragment()
        }
        val transaction = fragmentManager!!.beginTransaction()
        val bundle = Bundle()
        bundle.putParcelable("attr", attr)
        bundle.putParcelable("news", bean)
        commentFragment!!.arguments = bundle
        commentFragment!!.closeClickListener = this
        transaction.add(R.id.container, commentFragment)
        transaction.commit()
    }

    override fun onVideoPlay(position: Int) {
        val manager = recycler.layoutManager as LinearLayoutManager
        val first = manager.findFirstVisibleItemPosition()
        if (position == 0 && !recycler.canScrollVertically(-1)) {
            return
        }
        val view = recycler.getChildAt(position - first)
        recycler.smoothScrollBy(0, view.top - Util.dip2px(context!!, 40f))
    }

    //过渡动画执行完毕，显示网络数据
    override fun onAnimationEnd() {
        for (i in 0 until 14) {
            val v3 = NewsBean()
            v3.title = "视频新闻视频新闻视频新闻视频新闻视频新闻视频新闻视频新闻" + i
            v3.type = R.layout.adapter_video
            v3.imageUrl = "http://img5.imgtn.bdimg.com/it/u=3974436224,4269321529&fm=27&gp=0.jpg"
            v3.videoUrl = "https://mov.bn.netease.com/open-movie/nos/mp4/2017/12/04/SD3SUEFFQ_hd.mp4"
            v3.commentNum = 666
            mList!!.add(v3)
        }
        mAdapter!!.notifyItemRangeInserted(1, 14)
        top_layout.animate().alpha(1f).duration = 250
    }

    /**
     * 监听是否有下一个
     */
    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE -> {
                if (!isShowComment) {
                    val pos = mAdapter!!.mPlayPosition
                    if (pos <= mList!!.size - 2) {
                        val manager = recycler.layoutManager as LinearLayoutManager
                        val first = manager.findFirstVisibleItemPosition()
                        val view = recycler.getChildAt(pos + 1 - first)
                        recycler.smoothScrollBy(0, view.top)
                    }
                }
            }
        }
    }

    override fun closeCommentFragment() {
        isShowComment = false
        commentFragment!!.closeFragment()
        recycler.postDelayed({
            val transaction = fragmentManager!!.beginTransaction()
            transaction.remove(commentFragment)
            transaction.commit()
            val holder = recycler.findViewHolderForLayoutPosition(mAdapter!!.mPlayPosition) as VideoListAdapter.VideoHolder
            AssistPlayer.get().play(holder.container, null)
        }, MainActivity.DURATION)
    }

    //去除list中除了第一条的数据，然后开始过渡动画
    fun removeVideoList() {
        val size = mList!!.size - 1
        for (i in size downTo 1) {
            mList!!.removeAt(i)
        }
        mAdapter!!.notifyItemRangeRemoved(1, size)
        val view = recycler.getChildAt(0)
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val image = view.findViewById<ImageView>(R.id.adapter_video_list_image)
        val container = view.findViewById<FrameLayout>(R.id.adapter_video_list_container)
        val title = view.findViewById<TextView>(R.id.adapter_video_list_title)
        val bottom = view.findViewById<LinearLayout>(R.id.bottom_layout)
        title.postDelayed({
            title.visibility = View.GONE
            bottom.visibility = View.GONE
            image.visibility = View.GONE
            container.animate().scaleX(container.measuredWidth / mAttr!!.width.toFloat())
                    .scaleY(container.measuredHeight / mAttr!!.height.toFloat()).duration = DURATION
            view.animate().translationY((mAttr!!.y - location[1]).toFloat()).duration = DURATION
            val animator = ObjectAnimator.ofInt(root, "backgroundColor", -0x1000000, 0x00000000)
            animator.setEvaluator(ArgbEvaluator())
            animator.duration = DURATION
            animator.start()
        }, 250)
    }

    fun isPlayingFirst(): Boolean {
        return AssistPlayer.get().isPlaying && mAdapter!!.mPlayPosition == 0
    }

    fun attachList() {
        val holder = recycler.findViewHolderForLayoutPosition(mAdapter!!.mPlayPosition) as VideoListAdapter.VideoHolder
        AssistPlayer.get().play(holder.container, null)
    }

    fun attachCommentContainer() {
        commentFragment!!.attachContainer()
    }

    public interface onBackClickListener {
        fun removeVideoListFragment()
    }
}