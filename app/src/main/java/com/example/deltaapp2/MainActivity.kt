package com.example.deltaapp2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.deltaapp2.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.pong.GameThread().start()
        startRepeatingJob (1L)
    }

    fun update(){
        var point = binding.pong.playerPoint

        if (binding.pong.diff == 0) {
            val easyHighScore = getSharedPreferences("App_EasyMode", Context.MODE_PRIVATE)
            val easyHigh = easyHighScore.getInt("easyHigh", 0)
            if (easyHigh <= point) {
                val editor = easyHighScore.edit()
                editor.clear()
                editor.putInt("easyHigh", point)
                editor.apply()
            }
            binding.pong.high = easyHigh
        }

        else if(binding.pong.diff == 1)
        {
            val hardHighScore = getSharedPreferences("App_HardMode", Context.MODE_PRIVATE)
            val hardHigh = hardHighScore.getInt("hardHigh", 0)
            if (hardHigh <= point) {
                val editor = hardHighScore.edit()
                editor.clear()
                editor.putInt("hardHigh", point)
                editor.apply()
            }
            binding.pong.high = hardHigh
        }
    }

    private fun startRepeatingJob(timeInterval: Long): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                update()
                delay(timeInterval)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        binding.pong.stopGame()
    }


}