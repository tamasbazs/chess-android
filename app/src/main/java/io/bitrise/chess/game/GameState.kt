package io.bitrise.chess.game

import io.bitrise.chess.model.*

enum class GameStatus { ONGOING, CHECK, CHECKMATE, STALEMATE, DRAW }

class GameState {
    val board = ChessBoard()
    var currentTurn: PieceColor = PieceColor.WHITE
    var enPassantTarget: Position? = null
    var status: GameStatus = GameStatus.ONGOING
    val moveHistory = mutableListOf<Move>()
    private val moveGenerator = MoveGenerator()

    fun getLegalMoves(pos: Position): List<Move> {
        val piece = board.getPiece(pos) ?: return emptyList()
        if (piece.color != currentTurn) return emptyList()
        return moveGenerator.generateMoves(board, pos, enPassantTarget).filter { move ->
            val simBoard = board.copy()
            simBoard.applyMove(move)
            !isKingInCheck(simBoard, piece.color)
        }
    }

    fun makeMove(move: Move): Boolean {
        val legalMoves = getLegalMoves(move.from)
        val legalMove = legalMoves.find { it.to == move.to && it.promotion == move.promotion }
            ?: legalMoves.find { it.to == move.to && move.promotion == null }
            ?: return false

        val piece = board.getPiece(move.from)!!
        enPassantTarget = if (piece.type == PieceType.PAWN &&
            Math.abs(legalMove.to.row - legalMove.from.row) == 2
        ) {
            Position((legalMove.from.row + legalMove.to.row) / 2, legalMove.from.col)
        } else null

        if (piece.type == PieceType.PAWN && move.to == enPassantTarget) {
            val capturedPawnRow = if (currentTurn == PieceColor.WHITE) move.to.row + 1 else move.to.row - 1
            board.setPiece(Position(capturedPawnRow, move.to.col), null)
        }

        board.applyMove(legalMove)
        moveHistory.add(legalMove)
        currentTurn = if (currentTurn == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
        updateStatus()
        return true
    }

    private fun updateStatus() {
        val allMoves = getAllLegalMoves(currentTurn)
        val inCheck = isKingInCheck(board, currentTurn)
        status = when {
            allMoves.isEmpty() && inCheck -> GameStatus.CHECKMATE
            allMoves.isEmpty() -> GameStatus.STALEMATE
            inCheck -> GameStatus.CHECK
            else -> GameStatus.ONGOING
        }
    }

    private fun getAllLegalMoves(color: PieceColor): List<Move> {
        val moves = mutableListOf<Move>()
        for (row in 0..7) {
            for (col in 0..7) {
                val pos = Position(row, col)
                val piece = board.getPiece(pos) ?: continue
                if (piece.color == color) moves.addAll(getLegalMoves(pos))
            }
        }
        return moves
    }

    fun isKingInCheck(chessBoard: ChessBoard, color: PieceColor): Boolean {
        val kingPos = findKing(chessBoard, color) ?: return false
        val opponent = if (color == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
        for (row in 0..7) {
            for (col in 0..7) {
                val pos = Position(row, col)
                val piece = chessBoard.getPiece(pos) ?: continue
                if (piece.color == opponent) {
                    val attacks = moveGenerator.generateMoves(chessBoard, pos, null)
                    if (attacks.any { it.to == kingPos }) return true
                }
            }
        }
        return false
    }

    private fun findKing(chessBoard: ChessBoard, color: PieceColor): Position? {
        for (row in 0..7) {
            for (col in 0..7) {
                val piece = chessBoard.getPiece(Position(row, col))
                if (piece?.type == PieceType.KING && piece.color == color) {
                    return Position(row, col)
                }
            }
        }
        return null
    }
}
