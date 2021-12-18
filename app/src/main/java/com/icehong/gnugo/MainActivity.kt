package com.icehong.gnugo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.icehong.gnugo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        var s = ""
        javaIntArray(10)?.forEach{
            s += it.toString()
        }
        binding.sampleText.text = s
    }

    /**
     * A native method that is implemented by the 'stub' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String
    external fun javaIntArray(len: Int): IntArray?

    companion object {
        // Used to load the 'gnugo' library on application startup.
        init {
            System.loadLibrary("gnugo")
        }
    }
}