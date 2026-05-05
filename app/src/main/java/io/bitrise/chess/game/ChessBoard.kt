package io.bitrise.chess.game

import io.bitrise.chess.model.*

class ChessBoard {
    val squares: Array<Array<Piece?>> = Array(8) { arrayOfNulls(8) }

    init {
        setupInitialPosition()
    }

    private fun setupInitialPosition() {
        val backRank = listOf(
            PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
            PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        )
        for (col in 0..7) {
            squares[0][col] = Piece(backRank[col], PieceColor.BLACK)
            squares[1][col] = Piece(PieceType.PAWN, PieceColor.BLACK)
            squares[6][col] = Piece(PieceType.PAWN, PieceColor.WHITE)
            squares[7][col] = Piece(backRank[col], PieceColor.WHITE)
        }
    }

    fun getPiece(pos: Position): Piece? = squares[pos.row][pos.col]

    fun setPiece(pos: Position, piece: Piece?) {
        squares[pos.row][pos.col] = piece
    }

    fun applyMove(move: Move) {
        val piece = getPiece(move.from) ?: return
        val movedPiece = if (move.promotion != null) {
            Piece(move.promotion, piece.color)
        } else {
            piece
        }
        setPiece(move.to, movedPiece)
        setPiece(move.from, null)
    }

    fun copy(): ChessBoard {
        val copy = ChessBoard()
        for (row in 0..7) {
            for (col in 0..7) {
                copy.squares[row][col] = squares[row][col]
            }
        }
        return copy
    }
}
