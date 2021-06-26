package com.example.deltaapp2

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import androidx.annotation.ContentView
import com.example.deltaapp2.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        startRepeatingJob (1000L)
        update()
    }

    override fun onUserInteraction() {
        update()
        super.onUserInteraction()
    }

    fun update(){
        val sharedPref = getSharedPreferences("App_Info", Context.MODE_PRIVATE)
        val high = sharedPref.getInt("High", 0)
        var point = binding.pong.point
        if (high <= point){
            val editor = sharedPref.edit()
            editor.clear()
            editor.putInt("High", point)
            editor.apply()
        }
        binding.pong.high = high
    }

    private fun startRepeatingJob(timeInterval: Long): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                update()
                delay(timeInterval)
            }
        }
    }

}