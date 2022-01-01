package com.icehong.gnugo

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
const val ACTION_START = "A_START"
const val ACTION_STOP = "A_STOP"

// TODO: Rename parameters
const val EXTRA_PARAM1 = "com.icehong.gnugo.extra.PARAM1"
const val EXTRA_PARAM2 = "com.icehong.gnugo.extra.PARAM2"


class IntentGoService : JobIntentService() {
    /**
     * Convenience method for enqueuing work in to this service.
     */
    companion object{
        const val JOB_ID = 1000
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, IntentGoService::class.java, JOB_ID, work)
        }
        // Used to load the 'gnugo' library on application startup.
        init {
            System.loadLibrary("gnugo")
            Log.d("loadLibrary#", "gnugo");
        }
    }

    /**
     * A native method that is implemented by the 'stub' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String
    external fun javaIntArray(len: Int): IntArray?


    override fun onHandleWork(intent: Intent) {

        Log.d("MyJobIntentService所在线程", Thread.currentThread().id.toString());
        Log.d("MyJobIntentService服务#", "开始工作了");
    }

}