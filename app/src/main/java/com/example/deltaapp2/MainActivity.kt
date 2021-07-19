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

    private fun update(){
        val practicePoint = binding.pong.practicePoint
        val playerPoint = binding.pong.playerPoint
        val computerPoint = binding.pong.computerPoint

        if (binding.pong.diff == 0) {
            val easyHighScore = getSharedPreferences("Practice_EasyMode", Context.MODE_PRIVATE)
            val easyHigh = easyHighScore.getInt("easyHigh", 0)
            if (easyHigh <= practicePoint) {
                val editor = easyHighScore.edit()
                editor.clear()
                editor.putInt("easyHigh", practicePoint)
                editor.apply()
            }
            binding.pong.practiceHigh = easyHigh
        }
        else if(binding.pong.diff == 1) {
            val hardHighScore = getSharedPreferences("Practice_HardMode", Context.MODE_PRIVATE)
            val hardHigh = hardHighScore.getInt("hardHigh", 0)
            if (hardHigh <= practicePoint) {
                val editor = hardHighScore.edit()
                editor.clear()
                editor.putInt("hardHigh", practicePoint)
                editor.apply()
            }
            binding.pong.practiceHigh = hardHigh
        }

        if ((binding.pong.diff == 0) and (binding.pong.mode == 2)) {
            val computerEasyHighScore = getSharedPreferences("Computer_EasyMode", Context.MODE_PRIVATE)
            val playerEasyHighScore = getSharedPreferences("Player_EasyMode", Context.MODE_PRIVATE)
            val computerEasy = computerEasyHighScore.getInt("computerHigh", 0)
            val playerEasy = playerEasyHighScore.getInt("playerHigh", 0)
            if (playerEasy <= playerPoint) {
                val editor = playerEasyHighScore.edit()
                editor.clear()
                editor.putInt("playerHigh", playerPoint)
                editor.apply()
            }
            if (computerEasy <= computerPoint) {
                val editor = computerEasyHighScore.edit()
                editor.clear()
                editor.putInt("computerHigh", computerPoint)
                editor.apply()
            }
            binding.pong.playerHigh = playerEasy
            binding.pong.computerHigh = computerEasy
        }
        else if ((binding.pong.diff == 1) and (binding.pong.mode == 2)) {
            val computerHardHighScore = getSharedPreferences("Computer_HardMode", Context.MODE_PRIVATE)
            val playerHardHighScore = getSharedPreferences("Player_HardMode", Context.MODE_PRIVATE)
            val computerHard = computerHardHighScore.getInt("computerHigh", 0)
            val playerHard = playerHardHighScore.getInt("playerHigh", 0)
            if (playerHard <= playerPoint) {
                val editor = playerHardHighScore.edit()
                editor.clear()
                editor.putInt("playerHigh", playerPoint)
                editor.apply()
            }
            if (computerHard <= computerPoint) {
                val editor = computerHardHighScore.edit()
                editor.clear()
                editor.putInt("computerHigh", computerPoint)
                editor.apply()
            }
            binding.pong.playerHigh = playerHard
            binding.pong.computerHigh = computerHard
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

    override fun onPause() {
        super.onPause()
        if ((binding.pong.gameAI) or (binding.pong.gamePRACTICE))
            if (!binding.pong.gamePAUSED)
                binding.pong.pauseGame()
            binding.pong.resumeGame()
            binding.pong.pauseGame()
    }


}