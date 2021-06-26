package com.example.deltaapp2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat

class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paintBg: Paint = Paint()
    private val paintBlueFill: Paint = Paint()
    private val paintText: Paint = Paint()
    private val resetText: Paint = Paint()

    private val PLAYER_WIDTH: Int = 180
    private val PLAYER_HEIGHT: Int = 60
    private var playerX: Float = 0f

    private var touchX: Float = 0f
    private var touchY: Float = 0f

    var GAME_ENABLED = false
    var GAME_OVER = false

    private var ballX = (width/2).toFloat()
    private var ballY = (height/2).toFloat()
    private var ballSize = 40f

    private var dX = 5
    private var dY = 5

    var point = 0
    var high = 0

    private var barY = 150f
    private var barH = 10f

    init
    {
        paintBg.color = Color.BLACK
        paintBg.style = Paint.Style.FILL

        paintBlueFill.color =  ContextCompat.getColor(context, R.color.white)
        paintBlueFill.style = Paint.Style.FILL

        paintText.color = Color.WHITE
        paintText.style = Paint.Style.FILL
        paintText.textSize = 100f
        paintText.textAlign = Paint.Align.CENTER
        paintText.typeface = resources.getFont(R.font.rajdhani_medium)

        resetText.color = Color.BLACK
        resetText.style = Paint.Style.FILL
        resetText.textSize = 100f
        resetText.textAlign = Paint.Align.CENTER
        resetText.typeface = resources.getFont(R.font.rajdhani_medium)
    }

    override fun onSizeChanged(width: Int, height: Int, oldwidth: Int, oldheight: Int)
    {
        super.onSizeChanged(width, height, oldwidth, oldheight)

        playerX = (width / 2 - PLAYER_WIDTH / 2).toFloat()
        ballX = (width/2).toFloat()
        ballY = (height/2).toFloat()

        paintText.textSize = height / 20f
        resetText.textSize = height / 30f
    }

    override fun onDraw(canvas: Canvas?)
    {
        super.onDraw(canvas)

        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBg)
        canvas?.drawRect(0f, barY, width.toFloat(), barY - barH, paintBlueFill)
        canvas?.drawRect(
            playerX,
            (height - PLAYER_HEIGHT).toFloat(),
            playerX + PLAYER_WIDTH,
            height.toFloat(), paintBlueFill)


        canvas?.drawText("SCORE: " + point.toString(),(width/2).toFloat(), (barY - paintText.descent() - paintText.ascent())/2, paintText)

        if(GAME_OVER == true)
        {
            canvas?.drawText("YOUR RECORD",(width/2).toFloat(), (height + barY - paintText.descent() - paintText.ascent())/2 - 400f, paintText)
            canvas?.drawText("IS: " + high.toString(),(width/2).toFloat(), (height + barY - paintText.descent() - paintText.ascent())/2 - 300f, paintText)

            canvas?.drawRect(width/2 - 160f, (height + barY)/2 - 70f, width/2 + 160f, (height + barY)/2 +70f, paintBlueFill)
            canvas?.drawText("RESET",(width/2).toFloat(), (height + barY - paintText.descent() - paintText.ascent())/2, resetText)

            canvas?.drawRect(width/2 - 125f, (height + barY)/2 + 230f, width/2 + 125f, (height + barY)/2 + 370f, paintBlueFill)
            canvas?.drawText("MENU",(width/2).toFloat(), (height + barY - paintText.descent() - paintText.ascent())/2 +300f, resetText)
        }
        else
        {
            canvas?.drawRect(ballX-(ballSize/2), ballY-(ballSize/2), ballX+(ballSize/2), ballY+(ballSize/2), paintBlueFill)

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
                        (touchY in ((height + barY)/2 - 100f)..(height + barY)/2 +100f))
                            startGame()
                }
                else
                {
                    if (GAME_ENABLED == false)
                        startGame()
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
        playerX = if (playerX < 0) 0f
        else if (playerX > width - PLAYER_WIDTH)
        {
            (width - PLAYER_WIDTH).toFloat()
        } else {
            playerX
        }
        invalidate()
    }

    fun startGame()
    {
        GAME_ENABLED = true
        GAME_OVER = false
        point = 0
        playerX = (width / 2 - PLAYER_WIDTH / 2).toFloat()
        GameThread().start()

    }
    fun stopGame()
    {

        GAME_ENABLED = false
        GAME_OVER = true
        resetGame()
        invalidate()
    }
    private fun resetGame()
    {
//        ballX = (width/2).toFloat()
        ballY = (height/2).toFloat()
        dX = 5
        dY = 5
    }


    inner class GameThread : Thread()
    {
        override fun run()
        {
            while (GAME_ENABLED)
            {
                ballX += dX
                ballY += dY

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
                    else if (ballX in playerX..playerX + PLAYER_WIDTH)
                    {
                        ballY = height - (ballSize/2) - PLAYER_HEIGHT
                        dY *= -1
                    }


                }
                else if (ballY < barY + (ballSize/2))
                {
                    ballY = barY + (ballSize/2)
                    point++
                    dY *= -1
                }

                postInvalidate()

                sleep(10)
            }
        }
    }
}