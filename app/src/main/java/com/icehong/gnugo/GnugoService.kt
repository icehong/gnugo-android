package com.icehong.gnugo

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast

class GnugoService : Service() {

    external fun main(para: Array<String>): Int
    external fun gnugoclose(): Int
    external fun processGTP(cmd: String): String

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
                Log.d("GnugoService", "main return.")
            }
        }
        Thread(mRunnable).start()
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        gnugoclose()
        Toast.makeText(this, "GnuGo Service onUnbind...", Toast.LENGTH_LONG).show()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Toast.makeText(this, "GnuGo Service destroyed...", Toast.LENGTH_LONG).show()
        super.onDestroy()
    }

    private val binder = object : IGnugoS.Stub() {
        override fun processGTP(cmd: String): String {
            //return this@GnugoService.processGTP(cmd)
            return "processGTP"
        }
    }
}