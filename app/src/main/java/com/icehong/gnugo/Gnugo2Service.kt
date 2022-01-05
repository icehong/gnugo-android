package com.icehong.gnugo

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast

class Gnugo2Service : Service() {

    external fun main(para: Array<String>): Int

    override fun onCreate() {
        super.onCreate()
        System.loadLibrary("gnugo")
        Toast.makeText(this, "GnuGo Service created...", Toast.LENGTH_LONG).show()
    }

    override fun onBind(intent: Intent): IBinder {
        Toast.makeText(this, "GnuGo Service onBind...", Toast.LENGTH_LONG).show()
        val mRunnable = Runnable {
            run {
                main(arrayOf("--mode", "gtp", "--gtp-listen", "1234"))
                Log.d("GnugoS", "main return.")
            }
        }
        Thread(mRunnable).start()
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "GnuGo Service destroyed...", Toast.LENGTH_LONG).show()
    }

    private val binder = object : IGnugoS.Stub() {
        override fun GtpCommand(cmd: String?): String {
            return "GtpCommand"
        }
    }
}