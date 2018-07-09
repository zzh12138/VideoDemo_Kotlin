package com.example.zzh.videodemo_java.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by zhangzhihao on 2018/6/19 17:36.
 */

class NewsBean : Parcelable {
    var title: String? = null
    var imageUrl: String? = null
    var videoUrl: String? = null
    var type: Int = 0
    var commentNum: Int = 0

    constructor() {}

    protected constructor(`in`: Parcel) {
        title = `in`.readString()
        imageUrl = `in`.readString()
        videoUrl = `in`.readString()
        type = `in`.readInt()
        commentNum = `in`.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(imageUrl)
        dest.writeString(videoUrl)
        dest.writeInt(type)
        dest.writeInt(commentNum)
    }

    companion object {

        val CREATOR: Parcelable.Creator<NewsBean> = object : Parcelable.Creator<NewsBean> {
            override fun createFromParcel(`in`: Parcel): NewsBean {
                return NewsBean(`in`)
            }

            override fun newArray(size: Int): Array<NewsBean?> {
                return arrayOfNulls(size)
            }
        }
    }
}
