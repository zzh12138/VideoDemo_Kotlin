package com.example.zzh.videodemo_kotlin.itemDecoration

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by zhangzhihao on 2018/7/4 11:36.
 */
class EmptyItemDecoration(val paddingTop: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent!!.getChildAdapterPosition(view)
        if (position == 0) {
            outRect!!.top = paddingTop
        }
    }
}