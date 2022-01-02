package com.icehong.gnugo

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.icehong.gnugo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val client = GoClient("127.0.0.1", 1234)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btStart.setOnClickListener {
            Toast.makeText(this, "点击了", Toast.LENGTH_SHORT).show();

            val mRunnable = Runnable {
                run {
                    client.initConnect()
                    client.sendThread()
                    client.receiveThread()
                }
            }
            Thread(mRunnable).start()
        }

        binding.btStop.setOnClickListener {
            client.asynSendMessage("8 quit\n")
        }

        binding.btGo.setOnClickListener {
            client.asynSendMessage("1 boardsize 7\n")
            client.asynSendMessage("2 clear_board\n")
            client.asynSendMessage("3 play black D5\n")
            client.asynSendMessage("4 genmove white\n")
            client.asynSendMessage("5 play black C3\n")
            client.asynSendMessage("6 play black E3\n")
            client.asynSendMessage("7 showboard\n")
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var workIntent = Intent().apply {
                putExtra("id", ACTION_START);
                putExtra("para", arrayOf("--mode", "gtp", "--gtp-listen", "1234"));
            }
            GnugoService.enqueueWork(this, workIntent);
        }

    }
}