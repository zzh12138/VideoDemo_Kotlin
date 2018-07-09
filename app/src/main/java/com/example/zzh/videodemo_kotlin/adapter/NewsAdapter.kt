package com.example.zzh.videodemo_java.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide


import com.example.zzh.videodemo_java.bean.NewsBean
import com.example.zzh.videodemo_java.play.AssistPlayer
import com.example.zzh.videodemo_kotlin.R
import com.example.zzh.videodemo_kotlin.bean.ViewAttr
import com.kk.taurus.playerbase.entity.DataSource


/**
 * Created by zhangzhihao on 2018/6/19 17:35.
 */

class NewsAdapter(private val mList: List<NewsBean>?, private val mContext: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var playPosition: Int = 0
    private var videoTitleClickListener: onVideoTitleClickListener? = null

    init {
        playPosition = -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(viewType, parent, false)
        return if (viewType == R.layout.adapter_normal) {
            NormalHolder(view)
        } else {
            VideoHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NormalHolder) {
            setNormalData(holder, position)
        } else {
            setVideoData(holder as VideoHolder, position)
        }
    }

    private fun setNormalData(holder: NormalHolder, position: Int) {
        val bean = mList!![position]
        holder.title.text = bean.title
        Glide.with(mContext).load(bean.imageUrl).into(holder.image)
    }

    private fun setVideoData(holder: VideoHolder, position: Int) {
        val bean = mList!![position]
        holder.title.text = bean.title
        Glide.with(mContext).load(bean.imageUrl).into(holder.imageView)
        holder.container.removeAllViews()
        holder.title.setOnClickListener {
            if (videoTitleClickListener != null) {
                val location = IntArray(2)
                holder.container.getLocationOnScreen(location)
                val attr = ViewAttr()
                attr.x=location[0]
                attr.y=location[1]
                attr.width=holder.container.width
                attr.height=holder.container.height
                videoTitleClickListener!!.onTitleClick(holder.layoutPosition, attr)
            }
        }

        holder.imageView.setOnClickListener {
            val dataSource = DataSource(bean.videoUrl)
            playPosition = holder.layoutPosition
            if (dataSource != AssistPlayer.get().dataSource) {
                AssistPlayer.get().play(holder.container, dataSource)
            } else {
                AssistPlayer.get().play(holder.container, null)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return mList!![position].type
    }

    fun setOnVideoTitleClickListener(onVideoTitleClickListener: NewsAdapter.onVideoTitleClickListener) {
        this.videoTitleClickListener = onVideoTitleClickListener
    }

    internal inner class NormalHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title: TextView
        var image: ImageView

        init {
            title = itemView.findViewById(R.id.adapter_normal_title)
            image = itemView.findViewById(R.id.adapter_normal_image)
        }
    }

    inner class VideoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var imageView: ImageView = itemView.findViewById(R.id.adapter_video_image)
        internal var title: TextView = itemView.findViewById(R.id.adapter_video_title)
        var container: FrameLayout = itemView.findViewById(R.id.adapter_video_container)

    }

    interface onVideoTitleClickListener {
        fun onTitleClick(position: Int, attr: ViewAttr)
    }
}
