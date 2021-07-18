package com.example.deltaapp2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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

    private val slowBallReady: Paint = Paint()
    private val slowBallNotReady: Paint = Paint()
    private val paddleLengthReady: Paint = Paint()
    private val paddleLengthNotReady: Paint = Paint()
    private val powerupReady: Paint = Paint()

    private var PLAYER_WIDTH: Int = 180
    private val PLAYER_HEIGHT: Int = 50
    private var COMPUTER_WIDTH: Int = 180
    private val COMPUTER_HEIGHT: Int = 50

    private var playerX: Float = 0f
    private var computerX: Float = 0f

    private var touchX: Float = 0f
    private var touchY: Float = 0f
    var impactSound = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    var GAME_AI = false
    var GAME_PRACTICE = false
    var GAME_OVER = false
    var MAIN_MENU = true

    private var ballX = (width/2).toFloat()
    private var ballY = (height/2).toFloat()
    private var ballSize = 40f

    private var dX = 5f
    private var dY = 5f
    private var compX = 4.25f

    var playerPoint = 0
    var computerPoint = 0
    var high = 0
    var diff = 0
    var mode = 0
    var slowBall = 0f
    var slowBallFlag = false
    var slowBallScore = 0
    var paddleLength = 0f
    var paddleLengthFlag = false
    var paddleLengthScore = 0

    private val barY = 150f
    private val barH = 10f

    init
    {
        paintBg.color = Color.BLACK
        paintBg.style = Paint.Style.FILL

        paintWhite.color =  ContextCompat.getColor(context, R.color.white)
        paintWhite.style = Paint.Style.FILL

        slowBallReady.color = ContextCompat.getColor(context, R.color.teal_200)
        slowBallReady.style = Paint.Style.FILL

        slowBallNotReady.color = ContextCompat.getColor(context, R.color.teal_200)
        slowBallNotReady.style = Paint.Style.STROKE
        slowBallNotReady.strokeWidth = 3f

        paddleLengthReady.color = ContextCompat.getColor(context, R.color.yellow)
        paddleLengthReady.style = Paint.Style.FILL

        paddleLengthNotReady.color = ContextCompat.getColor(context, R.color.yellow)
        paddleLengthNotReady.style = Paint.Style.STROKE
        paddleLengthNotReady.strokeWidth = 3f

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
    }

    override fun onSizeChanged(width: Int, height: Int, oldwidth: Int, oldheight: Int)
    {
        super.onSizeChanged(width, height, oldwidth, oldheight)

        playerX = (width / 2).toFloat()
        computerX = (width / 2).toFloat()
        ballX = (width/2).toFloat()
        ballY = barY + PLAYER_HEIGHT + ballSize

        whiteText.textSize = height / 20f
        blackText.textSize = height / 30f
        titleText.textSize = height / 5f
    }

    override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)

        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBg)

        if(MAIN_MENU == true)
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
            canvas?.drawText("$high", (width/2).toFloat(), 1175f - (whiteText.descent() + whiteText.ascent())/2, whiteText)
            canvas?.drawText("High Score:", (width/2).toFloat(), 950f - (whiteText.descent() + whiteText.ascent())/2, whiteText)


        }
        else
        {
            if (mode == 1)
                canvas?.drawText("SCORE: $playerPoint",(width/2).toFloat(), (barY - whiteText.descent() - whiteText.ascent())/2, whiteText)
            else if (mode == 2)
                canvas?.drawText("$playerPoint | $computerPoint",(width/2).toFloat(), (barY - whiteText.descent() - whiteText.ascent())/2, whiteText)
            canvas?.drawRect(0f, barY, width.toFloat(), barY - barH, paintWhite)

            canvas?.drawRect(playerX - PLAYER_WIDTH/2, (height - PLAYER_HEIGHT).toFloat(), playerX + PLAYER_WIDTH/2, height.toFloat(), if (paddleLengthFlag == false) paintWhite else paddleLengthReady)

            canvas?.drawRect((width - 100) - slowBall, ((barY - barH)/2) - slowBall, (width - 100) + slowBall, ((barY - barH)/2) + slowBall, slowBallReady)
            canvas?.drawRect((width - 130f), ((barY - barH)/2) - 30, (width - 70f), ((barY - barH)/2) + 30, if(slowBall <= 30) slowBallNotReady else powerupReady)
            canvas?.drawRect((width - 210) - paddleLength, ((barY - barH)/2) - paddleLength, (width - 210) + paddleLength, ((barY - barH)/2) + paddleLength, paddleLengthReady)
            canvas?.drawRect((width - 240f), ((barY - barH)/2) - 30, (width - 180f), ((barY - barH)/2) + 30, if(paddleLength <= 30) paddleLengthNotReady else powerupReady)
        }


        if(GAME_OVER == true)
        {

            canvas?.drawText("YOUR RECORD",(width/2).toFloat(), (height + barY - whiteText.descent() - whiteText.ascent())/2 - 400f, whiteText)
            canvas?.drawText("IS: $high", (width/2).toFloat(), (height + barY - whiteText.descent() - whiteText.ascent())/2 - 300f, whiteText)

            canvas?.drawRect(width/2 - 160f, (height + barY)/2 - 70f, width/2 + 160f, (height + barY)/2 +70f, paintWhite)
            canvas?.drawText("RESET",(width/2).toFloat(), (height + barY - whiteText.descent() - whiteText.ascent())/2, blackText)

            canvas?.drawRect(width/2 - 125f, (height + barY)/2 + 230f, width/2 + 125f, (height + barY)/2 + 370f, paintWhite)
            canvas?.drawText("MENU",(width/2).toFloat(), (height + barY - whiteText.descent() - whiteText.ascent())/2 +300f, blackText)
        }
        else
        {
            if (MAIN_MENU == false)
                canvas?.drawRect(ballX-(ballSize/2), ballY-(ballSize/2), ballX+(ballSize/2), ballY+(ballSize/2), if (slowBallFlag == false) paintWhite else slowBallReady)
        }
        if(GAME_AI == true)
        {
            canvas?.drawRect(computerX - 90, barY, computerX + 90, barY + PLAYER_HEIGHT, paintWhite)
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        when (event?.action)
        {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.x
                touchY = event.y
                if (GAME_OVER == true)
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
                else if (MAIN_MENU == true)
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
                        if (diff == 0)
                            diff = 1
                        else
                            diff = 0
                    }
                    if ((touchX in (width/2 - 125f)..(width/2 + 125f)) and
                        (touchY in ((height + barY)/2 + 580)..((height + barY)/2 + 720)))
                    {
                        GAME_AI = true
                        mode = 2
                        startComputerMode()

                    }


                    invalidate()
                }
                else
                {
                    if ((touchX in ((width - 100f) - slowBall)..((width - 100f) + slowBall)) and
                        (touchY in (((barY - barH)/2) - slowBall)..(((barY - barH)/2) + slowBall)))
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
                        (touchY in (((barY - barH)/2) - paddleLength)..(((barY - barH)/2) + paddleLength)))
                    {
                        if (paddleLength >= 30f)
                        {
                            paddleLength = 0f
                            paddleLengthFlag = true
                            PLAYER_WIDTH = 280
                            paddleLengthScore = playerPoint
                        }
                    }
                }

            }
            MotionEvent.ACTION_MOVE ->
            {
                if (GAME_OVER == false)
                    handleMove(event)
            }
        }

        return true
    }

    private fun handleMove(event: MotionEvent)
    {
        playerX = playerX - (touchX - event.x)
        touchX = event.x
        playerX = if (playerX - PLAYER_WIDTH/2 < 0) PLAYER_WIDTH/2.toFloat()
        else if (playerX + PLAYER_WIDTH/2 > width)
        {
            (width - PLAYER_WIDTH/2).toFloat()
        } else {
            playerX
        }
        invalidate()
    }


    fun startPracticeMode()
    {
        MAIN_MENU = false
        GAME_PRACTICE = true
        GAME_OVER = false
        playerPoint = 0
        playerX = (width / 2 - PLAYER_WIDTH / 2).toFloat()
        GameThread().start()
    }
    fun stopGame()
    {
        invalidate()
        GAME_PRACTICE = false
        GAME_AI = false
        GAME_OVER = true
        resetGame()
    }
    fun resetGame()
    {
//        ballX = (width/2).toFloat()
        ballY = barY + PLAYER_HEIGHT + ballSize
        paddleLength = 0f
        slowBall = 0f
        paddleLengthFlag = false
        slowBallFlag = false
        compX = 4.25f
        dX /= abs(dX)
        dY /= abs(dY)
        dX *= 5f
        dY *= 5f
        PLAYER_WIDTH = 180
    }
    fun returnMenu()
    {
        playerPoint = 0
        computerPoint = 0
        MAIN_MENU = true
        GAME_OVER = false
        invalidate()
    }
    fun startComputerMode()
    {
        MAIN_MENU = false
        GAME_AI = true
        GAME_OVER = false
        GAME_PRACTICE = false
        playerPoint = 0
        computerPoint = 0
        playerX = (width / 2 - PLAYER_WIDTH / 2).toFloat()
        GameThread().start()
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

        if (ballY > height - (ballSize/2) - PLAYER_HEIGHT)
        {
            if (ballY >= height + (ballSize/2))
            {
                stopGame()
            }
            else if (ballX in playerX - PLAYER_WIDTH/2 - ballSize/4 ..playerX + PLAYER_WIDTH/2 + ballSize/4)
            {
                ballY = height - (ballSize/2) - PLAYER_HEIGHT
                dY *= -1
                impactSound.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
            }


        }
        else if (ballY < barY + (ballSize/2))
        {
            ballY = barY + (ballSize/2)
            playerPoint++
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
        if (ballX in (COMPUTER_WIDTH/2).toFloat()..(width - COMPUTER_WIDTH/2).toFloat())
        {
            computerX = ballX
        }
        if (ballX > width - (ballSize/2))
        {
            ballX = width - (ballSize/2)
            dX *= -1
        }
        else if (ballX < (ballSize/2))
        {
            ballX = (ballSize/2)
            dX *= -1
        }

        if (ballY > height - (ballSize/2) - PLAYER_HEIGHT)
        {
            if (ballY >= height + (ballSize/2))
            {
                stopGame()
            }
            else if (ballX in playerX - PLAYER_WIDTH/2 - ballSize/4 ..playerX + PLAYER_WIDTH/2 + ballSize/4)
            {
                ballY = height - (ballSize/2) - PLAYER_HEIGHT
                dY *= -1
                playerPoint += 1
            }


        }
        else if (ballY < barY + PLAYER_HEIGHT + (ballSize/2))
        {
            if (ballX in (computerX - PLAYER_WIDTH/2 - ballSize/2)..(computerX + PLAYER_WIDTH/2 + ballSize/2)){
                ballY = barY + (ballSize/2) + PLAYER_HEIGHT
                dY *= -1
                computerPoint += 1
            }
            else
                stopGame()
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
        }
        else if (ballX < (ballSize/2))
        {
            ballX = (ballSize/2)
            dX *= -1
        }

        if (ballY > height - (ballSize/2) - PLAYER_HEIGHT)
        {
            if (ballY >= height + (ballSize/2))
            {
                stopGame()
            }
            else if (ballX in playerX - PLAYER_WIDTH/2 - ballSize/4 ..playerX + PLAYER_WIDTH/2 + ballSize/4)
            {
                ballY = height - (ballSize/2) - PLAYER_HEIGHT
                dY *= -1
                playerPoint += 1
            }


        }
        else if (ballY < barY + PLAYER_HEIGHT + (ballSize/2))
        {
            if (ballX in (computerX - PLAYER_WIDTH/2 - ballSize/2)..(computerX + PLAYER_WIDTH/2 + ballSize/2)){
                ballY = barY + (ballSize/2) + PLAYER_HEIGHT
                dY *= -1
                computerPoint += 1
            }
            else
                stopGame()
        }
        /*if (computerX < PLAYER_WIDTH/2)
        {
            computerX = (PLAYER_WIDTH/2).toFloat()
            compX *= -1
        }
        else if (computerX > width - PLAYER_WIDTH/2)
        {
            computerX > width - PLAYER_WIDTH/2
            compX *= -1
        }
         */
        if (ballX < computerX)
        {
            if (compX > 0)
                compX *= -1
            if (computerX < COMPUTER_WIDTH/2)
            {
                computerX = (COMPUTER_WIDTH/2).toFloat()
            }
        }
        else if (ballX > computerX)
        {
            if (compX < 0)
                compX *= -1
            if (computerX > width - COMPUTER_WIDTH/2)
            {
                computerX > width - COMPUTER_WIDTH/2
            }
        }
    }



    inner class GameThread : Thread()
    {
        override fun run() {
            while (GAME_PRACTICE)
            {
                gameModePractice()
                if ((paddleLength < 30) and (paddleLengthFlag == false))
                    paddleLength += 0.03f
                else if ((paddleLengthFlag == true) and (playerPoint == paddleLengthScore + 3))
                {
                    PLAYER_WIDTH = 180
                    paddleLengthFlag = false
                }
                if ((slowBall < 30) and (slowBallFlag == false))
                    slowBall += 0.03f
                else if ((slowBallFlag == true) and (playerPoint == slowBallScore + 3))
                {
                    dX *= 2
                    dY *= 2
                    slowBallFlag = false
                }
                postInvalidate()
                sleep(10)
            }
            while (GAME_AI)
            {
                if (diff == 1)
                    gameModeAIHard()
                else
                    gameModeAIEasy()
                if ((paddleLength < 30) and (paddleLengthFlag == false))
                    paddleLength += 0.03f
                else if ((paddleLengthFlag == true) and (playerPoint == paddleLengthScore + 3))
                {
                    PLAYER_WIDTH = 180
                    paddleLengthFlag = false
                }
                if ((slowBall < 30) and (slowBallFlag == false))
                    slowBall += 0.03f
                else if ((slowBallFlag == true) and (playerPoint == slowBallScore + 3))
                {
                    dX *= 2
                    dY *= 2
                    slowBallFlag = false
                }
                postInvalidate()
                sleep(10)
            }
        }
    }
}