package com.icehong.gnugo

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import androidx.core.app.JobIntentService
import android.widget.Toast


// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
const val ACTION_NONE = 0
const val ACTION_START = 1
const val ACTION_STOP = 2

// TODO: Rename parameters
const val EXTRA_PARAM1 = "com.icehong.gnugo.extra.PARAM1"
const val EXTRA_PARAM2 = "com.icehong.gnugo.extra.PARAM2"


class GnugoService : JobIntentService() {
    /**
     * Convenience method for enqueuing work in to this service.
     */
    companion object{
        const val JOB_ID = 1000
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, GnugoService::class.java, JOB_ID, work)
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
    external fun main(para: Array<String>): Int
    external fun stringFromJNI(): String
    external fun javaIntArray(len: Int): IntArray?


    override fun onHandleWork(intent: Intent) {
        when(intent.getIntExtra("id",ACTION_NONE)){
            ACTION_START-> {
                var para = intent.getStringArrayExtra("para")
                if (para != null) {
                    val mRunnable = Runnable {
                        run {
                            main(para)
                            Log.d("GnugoS", "main return.")
                        }
                    }
                    Thread(mRunnable).start()
                    para.forEach { Log.d("GnugoS", "main start:$it") }

                }
            }
        }
        Log.d("MyJobIntentService所在线程", Thread.currentThread().id.toString());
    }

    override fun onDestroy() {
        super.onDestroy()
        toast("All work complete")
    }

    private val mHandler: Handler = Handler()
    // Helper for showing tests
    private fun toast(text: CharSequence?) {
        mHandler.post(Runnable {
            Toast.makeText(
                this@GnugoService,
                text,
                Toast.LENGTH_SHORT
            ).show()
        })
    }
}