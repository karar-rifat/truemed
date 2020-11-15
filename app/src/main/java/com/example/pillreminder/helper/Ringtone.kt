package com.example.pillreminder.helper

import android.app.AlarmManager
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService

class Ringtone {
    companion object {
         var r: Ringtone? = null
         lateinit var ringtoneManager:RingtoneManager
        fun runRingtone(context:Context){
            Log.e("ringtone","entered")

            if (r==null) {
                var alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                r = RingtoneManager.getRingtone(context, alert)
                if (r == null) {
                    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    r = RingtoneManager.getRingtone(context, alert)
                    Log.e("ringtone", "entered 1")
                    if (r == null) {
                        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                        r = RingtoneManager.getRingtone(context, alert)
                        Log.e("ringtone", "entered 2")
                    }
                }
//            Log.e("ringtone","entered 3")
            }
        }

        fun play(){
            if (!r!!.isPlaying) {
                r?.play()
                Log.e("ringtone","play")
            }
        }
        fun stopRingtone(){
            r?.stop()
            Log.e("ringtone","stop")

        }
        fun isPlaying():Boolean{
            Log.e("ringtone","playing")
            return r!!.isPlaying
        }
    }

}