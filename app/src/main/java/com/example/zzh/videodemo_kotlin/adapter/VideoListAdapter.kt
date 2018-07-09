package com.example.zzh.videodemo_kotlin.adapter

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListener
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.zzh.videodemo_java.bean.NewsBean
import com.example.zzh.videodemo_java.play.AssistPlayer
import com.example.zzh.videodemo_kotlin.MainActivity
import com.example.zzh.videodemo_kotlin.R
import com.example.zzh.videodemo_kotlin.bean.ViewAttr
import com.kk.taurus.playerbase.entity.DataSource
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder

/**
 * Created by zhangzhihao on 2018/7/5 13:56.
 */
class VideoListAdapter(var mContext: Context, var mList: ArrayList<NewsBean>) : RecyclerView.Adapter<VideoListAdapter.VideoHolder>() {
    var isAttach: Boolean = false
    var mPlayPosition: Int = -1
    var attr: ViewAttr? = null
    var mOnAnimationFinishListener: onAnimationFinishListener? = null
    var mOnMessageClickListener: onMessageClickListener? = null
    var mOnVideoPlayClickListener: onVideoPlayClickListener? = null

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        val bean = mList[position]
        holder.title.text = bean.title
        holder.message.text = bean.commentNum.toString()
        Glide.with(mContext).load(bean.imageUrl).into(holder.image)
        holder.container.removeAllViews()
        val options = RequestOptions().optionalCircleCrop()
        Glide.with(mContext).load(R.mipmap.ic_launcher).apply(options).into(holder.icon)
        holder.image.setOnClickListener {
            val dataSource = DataSource(bean.videoUrl)
            AssistPlayer.get().play(holder.container, dataSource)
            mPlayPosition = holder.layoutPosition
            mOnVideoPlayClickListener?.onVideoPlay(mPlayPosition)
        }
        if (isAttach && position == 0) {
            holder.itemView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    holder.itemView.viewTreeObserver.removeOnPreDrawListener(this)
                    val l = IntArray(2)
                    holder.itemView.getLocationOnScreen(l)
                    holder.itemView.translationY = (attr!!.y - l[1]).toFloat()
                    holder.containerLayout.scaleX = attr!!.width / holder.containerLayout.measuredWidth.toFloat()
                    holder.containerLayout.scaleY = attr!!.height / holder.containerLayout.measuredHeight.toFloat()
                    holder.title.alpha = 0f
                    holder.bottomLayout.alpha = 0f
                    holder.itemView.animate().translationY(0f).duration = MainActivity.DURATION
                    holder.containerLayout.animate().scaleX(1f).scaleY(1f).duration = MainActivity.DURATION
                    holder.title.animate().alpha(1f).duration = MainActivity.DURATION
                    holder.bottomLayout.animate().alpha(1f).duration = MainActivity.DURATION
                    holder.image.postDelayed({ mOnAnimationFinishListener?.onAnimationEnd() }, MainActivity.DURATION)
                    AssistPlayer.get().play(holder.container, null)
                    isAttach = false
                    mPlayPosition = 0
                    return true
                }
            })
        }
        holder.message.setOnClickListener {
            if (holder.layoutPosition != mPlayPosition) {
                holder.image.performClick()
            } else {
                val attr = ViewAttr()
                val l = IntArray(2)
                holder.container.getLocationOnScreen(l)
                attr.x = l[0]
                attr.y = l[1]
                attr.width = holder.container.width
                attr.height = holder.container.height
                mOnMessageClickListener?.onMessageClick(bean, attr)
            }
        }
        holder.itemView.setOnClickListener {
            if (holder.layoutPosition != mPlayPosition) {
                holder.image.performClick()
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        return VideoHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_video_list, parent, false))
    }

    class VideoHolder(itemView: View) : RecyclerView.ViewHolder(itemView), AnimateViewHolder {
        var container: FrameLayout = itemView.findViewById(R.id.adapter_video_list_container)
        internal var containerLayout: FrameLayout = itemView.findViewById(R.id.adapter_video_list_container_layout)
        internal var title: TextView = itemView.findViewById(R.id.adapter_video_list_title)
        internal var message: TextView = itemView.findViewById(R.id.adapter_video_list_message_num)
        internal var icon: ImageView = itemView.findViewById(R.id.adapter_video_list_icon)
        internal var image: ImageView = itemView.findViewById(R.id.adapter_video_list_image)
        internal var bottomLayout: LinearLayout = itemView.findViewById(R.id.bottom_layout)

        override fun preAnimateAddImpl(holder: RecyclerView.ViewHolder) {
            itemView.alpha = 0f
        }

        override fun preAnimateRemoveImpl(holder: RecyclerView.ViewHolder) {

        }

        override fun animateAddImpl(holder: RecyclerView.ViewHolder, listener: ViewPropertyAnimatorListener) {
            ViewCompat.animate(itemView)
                    .alpha(1f).setDuration(MainActivity.DURATION).start()
        }

        override fun animateRemoveImpl(holder: RecyclerView.ViewHolder, listener: ViewPropertyAnimatorListener) {
            ViewCompat.animate(itemView)
                    .alpha(0f).setDuration(MainActivity.DURATION).start()
        }
    }


    interface onAnimationFinishListener {
        fun onAnimationEnd()
    }

    public interface onMessageClickListener {
        fun onMessageClick(bean: NewsBean, attr: ViewAttr)
    }

    public interface onVideoPlayClickListener {
        fun onVideoPlay(position: Int)
    }
}