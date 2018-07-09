package com.example.zzh.videodemo_kotlin.itemDecoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by zhangzhihao on 2018/7/4 11:08.
 * describe 该类主要完成以下功能
 *  1.显示评论列表
 */
class LineItemDecoration(val drawableId: Int, val mContext: Context) : RecyclerView.ItemDecoration() {
    val mDrawable by lazy { mContext.resources.getDrawable(drawableId) }

    override fun onDraw(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.onDraw(c, parent, state)
        val count = parent!!.childCount
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        for (i in 0 until count - 1) {
            val view = parent.getChildAt(i)
            val params = view.layoutParams as RecyclerView.LayoutParams
            val top = view.bottom + params.bottomMargin
            val bottom = top + mDrawable.intrinsicHeight
            mDrawable.setBounds(left, top, right, bottom)
            mDrawable.draw(c)
        }
    }

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent!!.getChildAdapterPosition(view)
        val count = parent.childCount
        if (position < count - 1) {
            outRect!!.set(0, 0, 0, mDrawable.intrinsicHeight)
        }
    }
}