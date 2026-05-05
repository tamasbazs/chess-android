package io.bitrise.chess

import io.bitrise.chess.game.ChessBoard
import io.bitrise.chess.model.*
import org.junit.Assert.*
import org.junit.Test

class ChessBoardTest {

    @Test
    fun initialBoardHasBlackRooksInCorners() {
        val board = ChessBoard()
        assertEquals(PieceType.ROOK, board.getPiece(Position(0, 0))?.type)
        assertEquals(PieceColor.BLACK, board.getPiece(Position(0, 0))?.color)
        assertEquals(PieceType.ROOK, board.getPiece(Position(0, 7))?.type)
    }

    @Test
    fun applyMoveUpdatesPiece() {
        val board = ChessBoard()
        val from = Position(6, 0)
        val to = Position(5, 0)
        val piece = board.getPiece(from)
        board.applyMove(Move(from, to))
        assertNull(board.getPiece(from))
        assertEquals(piece, board.getPiece(to))
    }

    @Test
    fun copyProducesIndependentBoard() {
        val board = ChessBoard()
        val copy = board.copy()
        board.applyMove(Move(Position(6, 0), Position(5, 0)))
        assertNotNull(copy.getPiece(Position(6, 0)))
    }

    @Test
    fun promotionChangesType() {
        val board = ChessBoard()
        board.setPiece(Position(1, 0), Piece(PieceType.PAWN, PieceColor.WHITE))
        board.applyMove(Move(Position(1, 0), Position(0, 0), PieceType.QUEEN))
        assertEquals(PieceType.QUEEN, board.getPiece(Position(0, 0))?.type)
        assertEquals(PieceColor.WHITE, board.getPiece(Position(0, 0))?.color)
    }
}
