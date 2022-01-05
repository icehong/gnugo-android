package com.icehong.gnugo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.icehong.gnugo.databinding.ActivityMainBinding
import android.R
import android.annotation.SuppressLint

import android.graphics.Bitmap
import android.os.*
import androidx.core.os.HandlerCompat


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
            binding.txtResult.text.append("onServiceConnected")

        }

        // Called when the connection with the service disconnects unexpectedly
        override fun onServiceDisconnected(className: ComponentName) {
            binding.txtResult.text.append("onServiceDisconnected")
            Log.e("MainActivity", "Service has unexpectedly disconnected")
            mService = null
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btStart.setOnClickListener {
            Intent(this, Gnugo2Service::class.java).also { intent ->
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
            }
        }

        binding.btStop.setOnClickListener {
            client.asynSendMessage("quit\n")
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
/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var workIntent = Intent().apply {
                putExtra("id", ACTION_START);
                putExtra("para", arrayOf("--mode", "gtp", "--gtp-listen", "1234"));
            }
            GnugoService.enqueueWork(this, workIntent);
        }


 */

    }

    // val mHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())
    private val mHandler: Handler = object : Handler() {
        @SuppressLint("HandlerLeak")
        override fun handleMessage(msg: Message) { //此方法在ui线程运行
            when (msg.what) {
                0 -> {
                    binding.txtResult.text.append(msg.obj as String)
                }
            }
        }
    }
}