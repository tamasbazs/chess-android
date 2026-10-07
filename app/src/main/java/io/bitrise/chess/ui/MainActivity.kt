package io.bitrise.chess.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.bitrise.chess.R
import io.bitrise.chess.game.GameStatus

class MainActivity : AppCompatActivity() {

    private lateinit var boardView: ChessBoardView
    private lateinit var statusText: TextView
    private lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardView = findViewById(R.id.chessBoardView)
        statusText = findViewById(R.id.statusText)
        resetButton = findViewById(R.id.resetButton)

        boardView.onMoveListener = { updateStatus() }
        resetButton.setOnClickListener {
            boardView.resetGame()
            updateStatus()
        }
        updateStatus()
    }

    private fun updateStatus() {
        val game = boardView.gameState
        val turnName = game.currentTurn.name.lowercase().replaceFirstChar { it.uppercase() }
        // Full-move number, as in chess notation: it increases after Black moves.
        val moveNumber = game.moveHistory.size / 2 + 1
        statusText.text = when (game.status) {
            GameStatus.CHECKMATE -> {
                val winner = if (game.currentTurn == io.bitrise.chess.model.PieceColor.WHITE) "Black" else "White"
                "Checkmate! $winner wins!"
            }
            GameStatus.STALEMATE -> "Stalemate! It's a draw."
            GameStatus.DRAW -> "Draw!"
            GameStatus.CHECK -> "$turnName is in check! · Move $moveNumber"
            GameStatus.ONGOING -> "$turnName's turn · Move $moveNumber"
        }
    }
}
