package io.bitrise.chess.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import io.bitrise.chess.game.GameState
import io.bitrise.chess.model.Position

class ChessBoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var gameState = GameState()
    var onMoveListener: (() -> Unit)? = null

    private var selectedPosition: Position? = null
    private var legalMoveTargets: List<Position> = emptyList()

    private val lightSquarePaint = Paint().apply { color = Color.parseColor("#F0D9B5") }
    private val darkSquarePaint = Paint().apply { color = Color.parseColor("#B58863") }
    private val selectedPaint = Paint().apply { color = Color.parseColor("#80F6F669") }
    private val legalMovePaint = Paint().apply { color = Color.parseColor("#6064DD17") }
    private val checkPaint = Paint().apply { color = Color.parseColor("#C0FF0000") }

    private val piecePaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    private var squareSize = 0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = minOf(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        squareSize = w / 8f
        piecePaint.textSize = squareSize * 0.75f
    }

    override fun onDraw(canvas: Canvas) {
        drawSquares(canvas)
        drawHighlights(canvas)
        drawPieces(canvas)
    }

    private fun drawSquares(canvas: Canvas) {
        for (row in 0..7) {
            for (col in 0..7) {
                val paint = if ((row + col) % 2 == 0) lightSquarePaint else darkSquarePaint
                canvas.drawRect(
                    col * squareSize, row * squareSize,
                    (col + 1) * squareSize, (row + 1) * squareSize,
                    paint
                )
            }
        }
    }

    private fun drawHighlights(canvas: Canvas) {
        selectedPosition?.let { pos ->
            canvas.drawRect(
                pos.col * squareSize, pos.row * squareSize,
                (pos.col + 1) * squareSize, (pos.row + 1) * squareSize,
                selectedPaint
            )
        }
        for (pos in legalMoveTargets) {
            canvas.drawRect(
                pos.col * squareSize, pos.row * squareSize,
                (pos.col + 1) * squareSize, (pos.row + 1) * squareSize,
                legalMovePaint
            )
        }
    }

    private fun drawPieces(canvas: Canvas) {
        for (row in 0..7) {
            for (col in 0..7) {
                val piece = gameState.board.getPiece(Position(row, col)) ?: continue
                val x = col * squareSize + squareSize / 2
                val y = row * squareSize + squareSize / 2 - (piecePaint.descent() + piecePaint.ascent()) / 2
                canvas.drawText(piece.symbol(), x, y, piecePaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP) return true
        val col = (event.x / squareSize).toInt().coerceIn(0, 7)
        val row = (event.y / squareSize).toInt().coerceIn(0, 7)
        val tappedPos = Position(row, col)
        handleTap(tappedPos)
        return true
    }

    private fun handleTap(pos: Position) {
        val selected = selectedPosition
        if (selected != null && pos in legalMoveTargets) {
            val moved = gameState.makeMove(
                io.bitrise.chess.model.Move(selected, pos)
            )
            selectedPosition = null
            legalMoveTargets = emptyList()
            if (moved) onMoveListener?.invoke()
        } else {
            val piece = gameState.board.getPiece(pos)
            if (piece != null && piece.color == gameState.currentTurn) {
                selectedPosition = pos
                legalMoveTargets = gameState.getLegalMoves(pos).map { it.to }
            } else {
                selectedPosition = null
                legalMoveTargets = emptyList()
            }
        }
        invalidate()
    }

    fun resetGame() {
        gameState = GameState()
        selectedPosition = null
        legalMoveTargets = emptyList()
        invalidate()
    }
}
