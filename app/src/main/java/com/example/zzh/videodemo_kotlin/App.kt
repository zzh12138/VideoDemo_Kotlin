package com.example.zzh.videodemo_kotlin

import android.app.Application
import android.content.Context
import com.example.zzh.videodemo_kotlin.play.ExoMediaPlayer
import com.kk.taurus.playerbase.config.PlayerConfig
import com.kk.taurus.playerbase.config.PlayerLibrary
import com.kk.taurus.playerbase.entity.DecoderPlan

/**
 * Created by zhangzhihao on 2018/7/4 10:59.
 */
class App : Application() {

    val PLAN_ID_IJK = 1
    val PLAN_ID_EXO = 2

    companion object {
        var instance : Context ?=null
    }

    override fun onCreate() {
        super.onCreate()
        instance=applicationContext
//                    PlayerConfig.addDecoderPlan(DecoderPlan(PLAN_ID_IJK, IjkPlayer::class.java!!.getName(), "IjkPlayer"));
//                    PlayerConfig.setDefaultPlanId(PLAN_ID_IJK);
        PlayerConfig.addDecoderPlan(DecoderPlan(PLAN_ID_EXO, ExoMediaPlayer::class.java.getName(), "ExoPlayer"))
        PlayerConfig.setDefaultPlanId(PLAN_ID_EXO)
        PlayerConfig.setUseDefaultNetworkEventProducer(true)
        PlayerLibrary.init(this)
    }
}