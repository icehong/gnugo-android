package com.icehong.gnugo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.icehong.gnugo.databinding.ActivityMainBinding

import android.os.*
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val client = GoClient("127.0.0.1", 1234)
    var mService: IGnugoS? = null

    val mConnection = object : ServiceConnection {
        // Called when the connection with the service is established
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service
            mService = IGnugoS.Stub.asInterface(service)
            binding.textRst.append("onServiceConnected\n")

        }
        // Called when the connection with the service disconnects unexpectedly
        override fun onServiceDisconnected(className: ComponentName) {
            binding.textRst.append("onServiceDisconnected\n")
            Log.e("MainActivity", "Service has unexpectedly disconnected")
            mService = null
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btStart.setOnClickListener {
            Intent(this, GnugoService::class.java).also { intent ->
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            }
        }
        binding.btStop.setOnClickListener {
            client.asynSendMessage("quit\n")
            try {
                mService = null
                unbindService(mConnection)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.btConn.setOnClickListener {
            val mRunnable = Runnable {
                run {
                    if(client.initConnect()){
                        client.sendThread()
                        client.receiveThread(mHandler)
                    }
                }
            }
            Thread(mRunnable).start()

            /*
            client.asynSendMessage("1 boardsize 7\n")
            client.asynSendMessage("2 clear_board\n")
            client.asynSendMessage("3 play black D5\n")
            client.asynSendMessage("4 genmove white\n")
            client.asynSendMessage("5 play black C3\n")
            client.asynSendMessage("6 play black E3\n")
            client.asynSendMessage("7 showboard\n")

             */
        }

        binding.bt1.setOnClickListener{
            client.asynSendMessage("1 boardsize 7\n")
        }
        binding.bt2.setOnClickListener{
            client.asynSendMessage("2 clear_board\n")
        }
        binding.bt3.setOnClickListener{
            client.asynSendMessage("3 play black D5\n")
        }

    }

    override fun onDestroy() {
        client.close()
        super.onDestroy()
    }

    fun AppendResult(value: CharSequence?){
        binding.textRst.append(value)
    }

    class MyHandler(activity: MainActivity?) : Handler() {
        var wractivity: WeakReference< MainActivity> = WeakReference<MainActivity>(activity)
        override fun handleMessage(msg: Message) {
            val mactivity = wractivity.get() ?: return
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {
                    mactivity.AppendResult(msg.obj as String)
                }
            }
        }
    }

    // val mHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())
    private val mHandler: Handler = MyHandler(this)
}