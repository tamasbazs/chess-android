package io.bitrise.chess

import io.bitrise.chess.game.GameState
import io.bitrise.chess.game.GameStatus
import io.bitrise.chess.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameStateTest {

    private lateinit var gameState: GameState

    @Before
    fun setup() {
        gameState = GameState()
    }

    @Test
    fun initialTurnIsWhite() {
        assertEquals(PieceColor.WHITE, gameState.currentTurn)
    }

    @Test
    fun initialStatusIsOngoing() {
        assertEquals(GameStatus.ONGOING, gameState.status)
    }

    @Test
    fun whitePawnCanMoveOneSquare() {
        val moves = gameState.getLegalMoves(Position(6, 0))
        assertTrue(moves.any { it.to == Position(5, 0) })
    }

    @Test
    fun whitePawnCanMoveTwoSquaresFromStart() {
        val moves = gameState.getLegalMoves(Position(6, 0))
        assertTrue(moves.any { it.to == Position(4, 0) })
    }

    @Test
    fun blackPieceCannotMoveOnWhiteTurn() {
        val moves = gameState.getLegalMoves(Position(1, 0))
        assertTrue(moves.isEmpty())
    }

    @Test
    fun validMoveChangesToBlackTurn() {
        val moved = gameState.makeMove(Move(Position(6, 4), Position(4, 4)))
        assertTrue(moved)
        assertEquals(PieceColor.BLACK, gameState.currentTurn)
    }

    @Test
    fun invalidMoveReturnsFalse() {
        val moved = gameState.makeMove(Move(Position(0, 0), Position(3, 3)))
        assertFalse(moved)
    }

    @Test
    fun initialBoardHasCorrectPieces() {
        val board = gameState.board
        assertEquals(PieceType.KING, board.getPiece(Position(7, 4))?.type)
        assertEquals(PieceColor.WHITE, board.getPiece(Position(7, 4))?.color)
        assertEquals(PieceType.KING, board.getPiece(Position(0, 4))?.type)
        assertEquals(PieceColor.BLACK, board.getPiece(Position(0, 4))?.color)
    }

    @Test
    fun centerSquaresAreEmptyAtStart() {
        for (row in 2..5) {
            for (col in 0..7) {
                assertNull(gameState.board.getPiece(Position(row, col)))
            }
        }
    }

    @Test
    fun knightHasTwoMovesAtStart() {
        val moves = gameState.getLegalMoves(Position(7, 1))
        assertEquals(2, moves.size)
    }
}
