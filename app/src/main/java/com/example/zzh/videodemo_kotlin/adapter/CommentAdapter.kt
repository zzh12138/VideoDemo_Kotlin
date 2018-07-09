package com.example.zzh.videodemo_kotlin.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.zzh.videodemo_kotlin.R
import com.example.zzh.videodemo_kotlin.bean.CommentBean


/**
 * Created by zhangzhihao on 2018/7/5 13:31.
 */
class CommentAdapter(val mContext: Context, val mList: List<CommentBean>) : RecyclerView.Adapter<CommentAdapter.CommentHolder>() {

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        val comment = mList[position]
        holder.name.text = comment.userName
        holder.num.text = comment.praiseNum.toString()
        holder.content.text = comment.content
        val options = RequestOptions().circleCrop()
        Glide.with(mContext).load(R.mipmap.ic_launcher).apply(options).into(holder.icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        return CommentHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_comment, parent, false))
    }

    override fun getItemCount(): Int {
        return mList.size
    }

      class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

         val icon: ImageView = itemView.findViewById(R.id.adapter_comment_icon)
         val name: TextView = itemView.findViewById(R.id.adapter_comment_name)
         val num: TextView = itemView.findViewById(R.id.adapter_comment_praiseNum)
         val content: TextView = itemView.findViewById(R.id.comment_content)

    }
}