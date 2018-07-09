package com.example.zzh.videodemo_kotlin.fragment

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.zzh.videodemo_java.play.AssistPlayer
import com.example.zzh.videodemo_kotlin.MainActivity.Companion.DURATION
import com.example.zzh.videodemo_kotlin.R

import com.example.zzh.videodemo_kotlin.adapter.CommentAdapter
import com.example.zzh.videodemo_kotlin.bean.CommentBean
import com.example.zzh.videodemo_kotlin.bean.ViewAttr
import kotlinx.android.synthetic.main.fragment_comment.*


/**
 * Created by zhangzhihao on 2018/7/6 14:13.
 */
class CommentFragment : Fragment() {

    val mList = arrayListOf<CommentBean>()
    var mAttr: ViewAttr? = null
    var mAdapter: CommentAdapter? = null
    var location = IntArray(2)
    var closeClickListener: onCloseClickListener? = null
    lateinit var container:FrameLayout
    lateinit var recycler:RecyclerView
    lateinit var close:ImageView
    lateinit var comment_num:TextView
    lateinit var root:RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (i in 0 until 35) {
            val bean = CommentBean()
            bean.id = i.toString()
            bean.userName = "我就是个开发仔" + i
            bean.content = "大佬不要再秀了，学不动啦......"
            bean.praiseNum = 12138
            this.mList.add(bean)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_comment, c, false)
        container=view.findViewById(R.id.container)
        recycler=view.findViewById(R.id.recycler)
        close=view.findViewById(R.id.close)
        comment_num=view.findViewById(R.id.comment_num)
        root=view.findViewById(R.id.fragment_comment_root)
        initData()
        close.setOnClickListener {
            if (closeClickListener != null) {
                closeClickListener!!.closeCommentFragment()
            }
        }
        return view
    }

    fun initData() {
        mAttr = arguments!!.getParcelable("attr")
        val animator = ObjectAnimator.ofInt(root, "backgroundColor", Color.argb(0, 0, 0, 0)
                , Color.argb(255, 0, 0, 0))
        animator.setEvaluator(ArgbEvaluator())
        animator.duration = DURATION
        animator.start()
        container.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                container.viewTreeObserver.removeOnPreDrawListener(this)
                container.getLocationOnScreen(location)
                container.translationX = (mAttr!!.x - location[0]).toFloat()
                container.translationY = (mAttr!!.y - location[1]).toFloat()
                container.scaleX = mAttr!!.width / container.width.toFloat()
                container.scaleY = mAttr!!.height / container.height.toFloat()
                recycler.alpha = 0f
                textView.alpha = 0f
                close.alpha = 0f
                comment_num.alpha = 0f
                container.animate().translationX(0f).translationY(0f).scaleX(1f).scaleY(1f).duration = DURATION
                recycler.animate().alpha(1f).duration = DURATION
                textView.animate().alpha(1f).duration = DURATION
                close.animate().alpha(1f).duration = DURATION
                comment_num.animate().alpha(1f).duration = DURATION
                AssistPlayer.get().play(container, null)
               return true
            }
        })
        recycler.layoutManager = LinearLayoutManager(context)
        mAdapter = CommentAdapter(context!!, mList)
        recycler.adapter = mAdapter
    }

    public interface onCloseClickListener {
        fun closeCommentFragment()
    }

    fun attachContainer() {
        AssistPlayer.get().play(container, null)
    }

    fun closeFragment() {
        recycler.animate().alpha(0f).duration = DURATION
        textView.animate().alpha(0f).duration = DURATION
        close.animate().alpha(0f).duration = DURATION
        comment_num.animate().alpha(0f).duration = DURATION
        container.animate().translationY((mAttr!!.y - location[1]).toFloat()).duration = DURATION
        val animator = ObjectAnimator.ofInt(root, "backgroundColor", Color.argb(255, 0, 0, 0)
                , Color.argb(0, 0, 0, 0))
        animator.setEvaluator(ArgbEvaluator())
        animator.duration = DURATION
        animator.start()
    }
}