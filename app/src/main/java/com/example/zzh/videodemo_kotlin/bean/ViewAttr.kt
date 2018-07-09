package com.example.zzh.videodemo_kotlin.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by zhangzhihao on 2018/7/5 13:54.
 */
class ViewAttr() :Parcelable{
    var x: Int = 0
    var y: Int = 0
    var width: Int = 0
    var height: Int = 0

    constructor(parcel: Parcel) : this() {
        x = parcel.readInt()
        y = parcel.readInt()
        width = parcel.readInt()
        height = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(x)
        parcel.writeInt(y)
        parcel.writeInt(width)
        parcel.writeInt(height)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ViewAttr> {
        override fun createFromParcel(parcel: Parcel): ViewAttr {
            return ViewAttr(parcel)
        }

        override fun newArray(size: Int): Array<ViewAttr?> {
            return arrayOfNulls(size)
        }
    }
}