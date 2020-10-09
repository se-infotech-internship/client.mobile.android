package com.example.finedriver

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper

class BeepHelper()
{
    val toneG = ToneGenerator(AudioManager.STREAM_ALARM, 100)

    fun beep(duration: Int)
    {
        toneG.startTone(ToneGenerator.TONE_DTMF_S, duration)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            toneG.release()
        }, (duration + 50).toLong())
    }
}