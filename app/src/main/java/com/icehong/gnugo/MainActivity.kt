package com.icehong.gnugo

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.icehong.gnugo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btStart.setOnClickListener {
            Toast.makeText(this,"点击了",Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var workIntent  = Intent().apply {
                    putExtra(ACTION_START,"value555555");
                }
                IntentGoService.enqueueWork(this,workIntent);
            }
        }

        // Example of a call to a native method
        /*
        var s = ""
        javaIntArray(10)?.forEach{
            s += it.toString()
        }
        binding.sampleText.text = s

         */
    }
}