package com.example.deltaapp2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.abs

class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paintBg: Paint = Paint()
    private val paintWhite: Paint = Paint()
    private val whiteText: Paint = Paint()
    private val blackText: Paint = Paint()
    private val titleText: Paint = Paint()
    private val path = Path()

    private val slowBallReady: Paint = Paint()
    private val slowBallNotReady: Paint = Paint()
    private val paddleLengthReady: Paint = Paint()
    private val paddleLengthNotReady: Paint = Paint()
    private val computerPaddleReady: Paint = Paint()
    private val computerPaddleNotReady: Paint = Paint()
    private val powerupReady: Paint = Paint()

    private var playerWIDTH: Int = 180
    private val playerHEIGHT: Int = 50
    private var computerWIDTH: Int = 180
    private val computerHEIGHT: Int = 50

    private var playerX: Float = 0f
    private var computerX: Float = 0f

    private var touchX: Float = 0f
    private var touchY: Float = 0f

    private var impactSound = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    var gameAI = false
    var gamePRACTICE = false
    private var gameOVER = false
    private var gameMAINMENU = true
    var gamePAUSED = false

    private var ballX = (width/2).toFloat()
    private var ballY = (height/2).toFloat()
    private var ballSize = 40f

    private var dX = 5f
    private var pauseSpeedX = 0f
    private var dY = 5f
    private var pauseSpeedY = 0f
    private var compX = 4.25f

    var playerPoint = 0
    var computerPoint = 0
    var practicePoint = 0
    var playerHigh = 0
    var computerHigh = 0
    var practiceHigh = 0
    var diff = 0
    var mode = 0

    private var slowBall = 0f
    private var slowBallFlag = false
    private var slowBallScore = 0

    private var paddleLength = 0f
    private var paddleLengthFlag = false
    private var paddleLengthScore = 0

    private var computerPaddle = 0f
    private var computerPaddleFlag = false
    private var computerPaddleScore = 0

    private val barY = 150f
    private val barH = 10f

    init
    {
        paintBg.color = Color.BLACK
        paintBg.style = Paint.Style.FILL

        paintWhite.color =  Color.WHITE
        paintWhite.style = Paint.Style.FILL

        slowBallReady.color = ContextCompat.getColor(context, R.color.blue)
        slowBallReady.style = Paint.Style.FILL

        slowBallNotReady.color = ContextCompat.getColor(context, R.color.blue)
        slowBallNotReady.style = Paint.Style.STROKE
        slowBallNotReady.strokeWidth = 3f

        paddleLengthReady.color = ContextCompat.getColor(context, R.color.yellow)
        paddleLengthReady.style = Paint.Style.FILL

        paddleLengthNotReady.color = ContextCompat.getColor(context, R.color.yellow)
        paddleLengthNotReady.style = Paint.Style.STROKE
        paddleLengthNotReady.strokeWidth = 3f

        computerPaddleReady.color = ContextCompat.getColor(context, R.color.purple)
        computerPaddleReady.style = Paint.Style.FILL

        computerPaddleNotReady.color = ContextCompat.getColor(context, R.color.purple)
        computerPaddleNotReady.style = Paint.Style.STROKE
        computerPaddleNotReady.strokeWidth = 3f

        powerupReady.color = ContextCompat.getColor(context, R.color.green)
        powerupReady.style = Paint.Style.STROKE
        powerupReady.strokeWidth = 5f

        whiteText.color = Color.WHITE
        whiteText.style = Paint.Style.FILL
        whiteText.textSize = 100f
        whiteText.textAlign = Paint.Align.CENTER
        whiteText.typeface = resources.getFont(R.font.rajdhani_medium)

        blackText.color = Color.BLACK
        blackText.style = Paint.Style.FILL
        blackText.textSize = 100f
        blackText.textAlign = Paint.Align.CENTER
        blackText.typeface = resources.getFont(R.font.rajdhani_medium)

        titleText.color = Color.WHITE
        titleText.style = Paint.Style.STROKE
        titleText.textSize = 500f
        titleText.textAlign = Paint.Align.CENTER
        titleText.typeface = resources.getFont(R.font.rajdhani_medium)

        path.moveTo(100f, (barY - barH)/2 + 30f)
        path.lineTo(100f, (barY - barH)/2 - 30f)
        path.lineTo(145f, (barY - barH)/2)
        path.lineTo(100f, (barY - barH)/2 + 30f)
        path.close()
    }

    override fun onSizeChanged(width: Int, height: Int, oldwidth: Int, oldheight: Int)
    {
        super.onSizeChanged(width, height, oldwidth, oldheight)

        playerX = (width / 2).toFloat()
        computerX = (width / 2).toFloat()
        ballX = (width/2).toFloat()
        ballY = barY + playerHEIGHT + ballSize

        whiteText.textSize = height / 20f
        blackText.textSize = height / 30f
        titleText.textSize = height / 5f
    }

    override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)

        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBg)

        if(gameMAINMENU)
        {
            canvas?.drawRect(width/2 - 160f, (height + barY)/2 + 350f, width/2 + 160f, (height + barY)/2 + 490f, paintWhite)
            canvas?.drawText("PRACTICE",(width/2).toFloat(), (height + barY - whiteText.descent() - whiteText.ascent())/2 + 420, blackText)

            canvas?.drawRect(width/2 - 125f, (height + barY)/2 + 580, width/2 + 125f, (height + barY)/2 + 720, paintWhite)
            canvas?.drawText("AI",(width/2).toFloat(), (height + barY - whiteText.descent() - whiteText.ascent())/2 + 650, blackText)

            canvas?.drawText("PONG", (width/2).toFloat(), 250 - (titleText.descent() + titleText.ascent())/2, titleText)

            canvas?.drawRect(width/2 - 125f, 600f, width/2 + 125f, 740f, paintWhite)
            if(diff == 0)
                canvas?.drawText("Easy", (width/2).toFloat(), 670 - (blackText.descent() + blackText.ascent())/2, blackText)
            else
                canvas?.drawText("Hard", (width/2).toFloat(), 670 - (blackText.descent() + blackText.ascent())/2, blackText)

            canvas?.drawRect(width/2 - 125f, 1050f, width/2 + 125f, 1300f, titleText)
            canvas?.drawText("$practiceHigh", (width/2).toFloat(), 1175f - (whiteText.descent() + whiteText.ascent())/2, whiteText)
            canvas?.drawText("High Score:", (width/2).toFloat(), 950f - (whiteText.descent() + whiteText.ascent())/2, whiteText)


        }
        else
        {
            if (mode == 1)
                canvas?.drawText("SCORE: $practicePoint",(width/2).toFloat(), (barY - whiteText.descent() - whiteText.ascent())/2, whiteText)
            else if (mode == 2) {
                canvas?.drawText("$playerPoint | $computerPoint", (width / 2).toFloat(), (barY - whiteText.descent() - whiteText.ascent()) / 2, whiteText)
                canvas?.drawRect((width - 320) - computerPaddle, ((barY - barH)/2) - computerPaddle, (width - 320) + computerPaddle, ((barY - barH)/2) + computerPaddle, computerPaddleReady)
                canvas?.drawRect((width - 350f), ((barY - barH)/2) - 30, (width - 290f), ((barY - barH)/2) + 30, if(computerPaddle <= 30) computerPaddleNotReady else powerupReady)
            }
            canvas?.drawRect(0f, barY, width.toFloat(), barY - barH, paintWhite)

            canvas?.drawRect(playerX - playerWIDTH/2, (height - playerHEIGHT).toFloat(), playerX + playerWIDTH/2, height.toFloat(), if (!paddleLengthFlag) paintWhite else paddleLengthReady)

            canvas?.drawRect((width - 100) - slowBall, ((barY - barH)/2) - slowBall, (width - 100) + slowBall, ((barY - barH)/2) + slowBall, slowBallReady)
            canvas?.drawRect((width - 130f), ((barY - barH)/2) - 30, (width - 70f), ((barY - barH)/2) + 30, if(slowBall <= 30) slowBallNotReady else powerupReady)
            canvas?.drawRect((width - 210) - paddleLength, ((barY - barH)/2) - paddleLength, (width - 210) + paddleLength, ((barY - barH)/2) + paddleLength, paddleLengthReady)
            canvas?.drawRect((width - 240f), ((barY - barH)/2) - 30, (width - 180f), ((barY - barH)/2) + 30, if(paddleLength <= 30) paddleLengthNotReady else powerupReady)
            if (!gamePAUSED) {
                canvas?.drawRect(100f, (barY - barH) / 2 - 30f, 115f, (barY - barH) / 2 + 30f, paintWhite)
                canvas?.drawRect(130f, (barY - barH) / 2 - 30f, 145f, (barY - barH) / 2 + 30f, paintWhite)
            }
            else
            {
                canvas?.drawPath(path, paintWhite)
            }
        }

        if(gameOVER)
        {

            canvas?.drawText("YOUR RECORD",(width/2).toFloat(), (height + barY - whiteText.descent() - whiteText.ascent())/2 - 400f, whiteText)
            canvas?.drawText(if(mode == 1) "IS: $practiceHigh" else "IS: $playerHigh | $computerHigh", (width/2).toFloat(), (height + barY - whiteText.descent() - whiteText.ascent())/2 - 300f, whiteText)

            canvas?.drawRect(width/2 - 160f, (height + barY)/2 - 70f, width/2 + 160f, (height + barY)/2 +70f, paintWhite)
            canvas?.drawText("RESET",(width/2).toFloat(), (height + barY - whiteText.descent() - whiteText.ascent())/2, blackText)

            canvas?.drawRect(width/2 - 125f, (height + barY)/2 + 230f, width/2 + 125f, (height + barY)/2 + 370f, paintWhite)
            canvas?.drawText("MENU",(width/2).toFloat(), (height + barY - whiteText.descent() - whiteText.ascent())/2 +300f, blackText)
        }
        else
        {
            if (!gameMAINMENU)
                canvas?.drawRect(ballX-(ballSize/2), ballY-(ballSize/2), ballX+(ballSize/2), ballY+(ballSize/2), if (!slowBallFlag) paintWhite else slowBallReady)
        }

        if(gameAI)
        {
            canvas?.drawRect(computerX - computerWIDTH/2, barY, computerX + computerWIDTH/2, barY + playerHEIGHT, if(!computerPaddleFlag) paintWhite else computerPaddleReady)
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        when (event?.action)
        {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.x
                touchY = event.y
                if (gameOVER)
                {
                    if ((touchX in (width/2 - 200f)..(width/2 +200f)) and
                        (touchY in ((height + barY)/2 - 70f)..(height + barY)/2 + 70f))
                    {
                        if (mode == 1)
                            startPracticeMode()
                        else
                            startComputerMode()
                    }
                    if ((touchX in (width/2 - 125f)..(width/2 +125f)) and
                        (touchY in ((height + barY)/2 + 230f)..(height + barY)/2 +370f))
                            returnMenu()
                }
                else if (gameMAINMENU)
                {
                    if ((touchX in (width/2 - 160f)..(width/2 + 160f)) and
                        (touchY in ((height + barY)/2 + 350f)..((height + barY)/2 + 490f)))
                    {
                        mode = 1
                        startPracticeMode()

                    }
                    if ((touchX in (width/2 - 125f)..(width/2 + 125f)) and
                        (touchY in 600f..740f))
                    {
                        diff = if (diff == 0)
                            1
                        else
                            0
                    }
                    if ((touchX in (width/2 - 125f)..(width/2 + 125f)) and
                        (touchY in ((height + barY)/2 + 580)..((height + barY)/2 + 720)))
                    {
                        gameAI = true
                        mode = 2
                        startComputerMode()

                    }


                    invalidate()
                }
                else
                {
                    if ((touchX in ((width - 100f) - slowBall)..((width - 100f) + slowBall)) and
                        (touchY in (((barY - barH)/2) - slowBall)..(((barY - barH)/2) + slowBall)) and
                        (!gamePAUSED))
                    {
                        if (slowBall >= 30f)
                        {
                            slowBall = 0f
                            slowBallFlag = true
                            dX /= 1.5f
                            dY /= 1.5f
                            slowBallScore = playerPoint
                        }
                    }
                    if ((touchX in ((width - 210f) - paddleLength)..((width - 210f) + paddleLength)) and
                        (touchY in (((barY - barH)/2) - paddleLength)..(((barY - barH)/2) + paddleLength)) and
                        (!gamePAUSED))
                    {
                        if (paddleLength >= 30f)
                        {
                            paddleLength = 0f
                            paddleLengthFlag = true
                            playerWIDTH = 280
                            paddleLengthScore = playerPoint
                        }
                    }

                    if ((touchX in ((width - 320f) - computerPaddle)..((width - 320f) + computerPaddle)) and
                        (touchY in (((barY - barH)/2) - computerPaddle)..(((barY - barH)/2) + computerPaddle)) and
                        (!gamePAUSED))
                    {
                        if (computerPaddle >= 30f)
                        {
                            computerPaddle = 0f
                            computerPaddleFlag = true
                            computerWIDTH = 80
                            computerPaddleScore = playerPoint
                        }
                    }

                    if ((touchX in (90f)..(155f)) and
                        (touchY in ((barY - barH)/2 - 30f)..((barY - barH)/2 + 30f)))
                    {
                        if (gamePAUSED)
                            resumeGame()
                        else
                            pauseGame()
                    }
                }

            }
            MotionEvent.ACTION_MOVE -> {
                if ((!gameOVER) and
                    (!gamePAUSED))
                    handleMove(event)
            }
        }
        return true
    }

    private fun handleMove(event: MotionEvent)
    {
        playerX -= (touchX - event.x)
        touchX = event.x
        playerX = when {
            playerX - playerWIDTH/2 < 0 -> playerWIDTH/2.toFloat()
            playerX + playerWIDTH/2 > width -> {
                (width - playerWIDTH/2).toFloat()
            }
            else -> {
                playerX
            }
        }
        invalidate()
    }

    private fun startPracticeMode()
    {
        gameMAINMENU = false
        gamePRACTICE = true
        gameOVER = false
        playerPoint = 0
        playerX = (width / 2 - playerWIDTH / 2).toFloat()
        GameThread().start()
    }

    fun stopGame()
    {
        invalidate()
        gamePRACTICE = false
        gameAI = false
        gameOVER = true
        resetGame()
    }

    private fun resetGame()
    {
        ballY = barY + playerHEIGHT + ballSize
        paddleLength = 0f
        slowBall = 0f
        computerPaddle = 0f
        paddleLengthFlag = false
        slowBallFlag = false
        computerPaddleFlag = false
        compX = 4.25f
        dX /= abs(dX)
        dX *= 5f
        dY = 5f
        playerWIDTH = 180
    }

    private fun returnMenu()
    {
        playerPoint = 0
        computerPoint = 0
        gameMAINMENU = true
        gameOVER = false
        invalidate()
    }

    private fun startComputerMode()
    {
        gameMAINMENU = false
        gameAI = true
        gameOVER = false
        gamePRACTICE = false
        playerPoint = 0
        computerPoint = 0
        playerX = (width / 2 - playerWIDTH / 2).toFloat()
        GameThread().start()
    }

    fun pauseGame()
    {
        gamePAUSED = true
        pauseSpeedX = dX
        pauseSpeedY = dY
        dX = 0f
        dY = 0f
        compX = 0f
    }

    fun resumeGame()
    {
        gamePAUSED = false
        dX = pauseSpeedX
        dY = pauseSpeedY
        compX = 4.25f
    }

    fun gameModePractice()
    {
        ballX += dX
        ballY += dY
        if (ballX > width - (ballSize/2))
        {
            ballX = width - (ballSize/2)
            dX *= -1
            impactSound.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
        }
        else if (ballX < (ballSize/2))
        {
            ballX = (ballSize/2)
            dX *= -1
            impactSound.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
        }

        if (ballY > height - (ballSize/2) - playerHEIGHT)
        {
            if (ballY >= height + (ballSize/2))
            {
                stopGame()
                impactSound.startTone(ToneGenerator.TONE_SUP_RINGTONE, 200)
            }
            else if (ballX in playerX - playerWIDTH/2 - ballSize/4 ..playerX + playerWIDTH/2 + ballSize/4)
            {
                ballY = height - (ballSize/2) - playerHEIGHT
                dY *= -1
                impactSound.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
            }


        }
        else if (ballY < barY + (ballSize/2))
        {
            ballY = barY + (ballSize/2)
            practicePoint++
            dY *= -1
            if (dX < 0)
                dX -= diff
            else
                dX += diff
            dY += diff
            impactSound.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
        }

    }

    fun gameModeAIHard()
    {
        ballX += dX
        ballY += dY
        if (ballX in (computerWIDTH/2).toFloat()..(width - computerWIDTH/2).toFloat())
        {
            computerX = ballX
        }
        if (ballX > width - (ballSize/2))
        {
            ballX = width - (ballSize/2)
            dX *= -1
            impactSound.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
        }
        else if (ballX < (ballSize/2))
        {
            ballX = (ballSize/2)
            dX *= -1
            impactSound.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
        }

        if (ballY > height - (ballSize/2) - playerHEIGHT)
        {
            if (ballY >= height + (ballSize/2))
            {
                stopGame()
                impactSound.startTone(ToneGenerator.TONE_SUP_RINGTONE, 200)
            }
            else if (ballX in playerX - playerWIDTH/2 - ballSize/4 ..playerX + playerWIDTH/2 + ballSize/4)
            {
                ballY = height - (ballSize/2) - playerHEIGHT
                dY *= -1
                playerPoint += 1
                impactSound.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
            }


        }
        else if (ballY < barY + playerHEIGHT + (ballSize/2))
        {
            if (ballX in (computerX - computerWIDTH/2 - ballSize/2)..(computerX + computerWIDTH/2 + ballSize/2)){
                ballY = barY + (ballSize/2) + computerHEIGHT
                dY *= -1
                computerPoint += 1
                impactSound.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
            }
            else {
                stopGame()
                impactSound.startTone(ToneGenerator.TONE_SUP_RINGTONE, 200)
            }
        }
    }

    fun gameModeAIEasy()
    {
        ballX += dX
        ballY += dY
        computerX += compX

        if (ballX > width - (ballSize/2))
        {
            ballX = width - (ballSize/2)
            dX *= -1
            impactSound.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
        }
        else if (ballX < (ballSize/2))
        {
            ballX = (ballSize/2)
            dX *= -1
            impactSound.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
        }

        if (ballY > height - (ballSize/2) - playerHEIGHT)
        {
            if (ballY >= height + (ballSize/2))
            {
                stopGame()
                impactSound.startTone(ToneGenerator.TONE_SUP_RINGTONE, 200)
            }
            else if (ballX in playerX - playerWIDTH/2 - ballSize/4 ..playerX + playerWIDTH/2 + ballSize/4)
            {
                ballY = height - (ballSize/2) - playerHEIGHT
                dY *= -1
                playerPoint += 1
                impactSound.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
            }


        }
        else if (ballY < barY + computerHEIGHT + (ballSize/2))
        {
            if (ballX in (computerX - computerWIDTH/2 - ballSize/2)..(computerX + computerWIDTH/2 + ballSize/2)){
                ballY = barY + (ballSize/2) + computerHEIGHT
                dY *= -1
                computerPoint += 1
                impactSound.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
            }
            else {
                stopGame()
                impactSound.startTone(ToneGenerator.TONE_SUP_RINGTONE, 200)
            }
        }
        if (ballX < computerX)
        {
            if (compX > 0)
                compX *= -1
            if (computerX < computerWIDTH/2)
            {
                computerX = (computerWIDTH/2).toFloat()
            }
        }
        else if (ballX > computerX)
        {
            if (compX < 0)
                compX *= -1
            if (computerX > width - computerWIDTH/2)
            {
                computerX > width - computerWIDTH/2
            }
        }
    }

    inner class GameThread : Thread()
    {
        override fun run() {
            while (gamePRACTICE)
            {
                gameModePractice()
                if (!gamePAUSED) {
                    if ((paddleLength < 30) and (!paddleLengthFlag))
                        paddleLength += 0.03f
                    else if ((paddleLengthFlag) and (playerPoint == paddleLengthScore + 3)) {
                        playerWIDTH = 180
                        paddleLengthFlag = false
                    }
                    if ((slowBall < 30) and (!slowBallFlag))
                        slowBall += 0.03f
                    else if ((slowBallFlag) and (playerPoint == slowBallScore + 3)) {
                        dX *= 2
                        dY *= 2
                        slowBallFlag = false
                    }
                    if ((computerPaddle < 30) and (!computerPaddleFlag))
                        computerPaddle += 0.03f
                    else if ((computerPaddleFlag) and (playerPoint == computerPaddleScore + 3)) {
                        computerWIDTH = 180
                        computerPaddleFlag = false
                    }
                }
                postInvalidate()
                sleep(10)
            }

            while (gameAI)
            {
                if (diff == 1)
                    gameModeAIHard()
                else
                    gameModeAIEasy()
                if (!gamePAUSED) {
                    if ((paddleLength < 30) and (!paddleLengthFlag))
                        paddleLength += 0.03f
                    else if ((paddleLengthFlag) and (playerPoint == paddleLengthScore + 3)) {
                        playerWIDTH = 180
                        paddleLengthFlag = false
                    }
                    if ((slowBall < 30) and (!slowBallFlag))
                        slowBall += 0.03f
                    else if ((slowBallFlag) and (playerPoint == slowBallScore + 3)) {
                        dX *= 2
                        dY *= 2
                        slowBallFlag = false
                    }
                    if ((computerPaddle < 30) and (!computerPaddleFlag))
                        computerPaddle += 0.03f
                    else if ((computerPaddleFlag) and (playerPoint == computerPaddleScore + 3)) {
                        computerWIDTH = 180
                        computerPaddleFlag = false
                    }
                }
                postInvalidate()
                sleep(10)
            }
        }
    }
}