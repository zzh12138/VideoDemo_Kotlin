package com.example.zzh.videodemo_kotlin.bean

/**
 * Created by zhangzhihao on 2018/7/5 13:37.
 * describe 该类主要完成以下功能
 *  1.显示评论列表
 */
class CommentBean() {
    var id: String = ""
    var userIcon: String = ""
    var userName: String = ""
    var praiseNum: Int = 0
    var content: String = ""

    constructor(id: String, userIcon: String, userName: String, pariseNum: Int, content: String) : this() {
        this.id = id
        this.userIcon = userIcon
        this.userName = userName
        this.praiseNum = pariseNum
        this.content = content
    }

}