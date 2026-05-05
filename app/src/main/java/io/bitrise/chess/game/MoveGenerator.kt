package io.bitrise.chess.game

import io.bitrise.chess.model.*

class MoveGenerator {

    fun generateMoves(board: ChessBoard, pos: Position, enPassantTarget: Position?): List<Move> {
        val piece = board.getPiece(pos) ?: return emptyList()
        return when (piece.type) {
            PieceType.PAWN -> pawnMoves(board, pos, piece.color, enPassantTarget)
            PieceType.KNIGHT -> knightMoves(board, pos, piece.color)
            PieceType.BISHOP -> slidingMoves(board, pos, piece.color, bishopDirections)
            PieceType.ROOK -> slidingMoves(board, pos, piece.color, rookDirections)
            PieceType.QUEEN -> slidingMoves(board, pos, piece.color, queenDirections)
            PieceType.KING -> kingMoves(board, pos, piece.color)
        }
    }

    private val bishopDirections = listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1)
    private val rookDirections = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
    private val queenDirections = bishopDirections + rookDirections

    private fun slidingMoves(
        board: ChessBoard, pos: Position, color: PieceColor,
        directions: List<Pair<Int, Int>>
    ): List<Move> {
        val moves = mutableListOf<Move>()
        for ((dr, dc) in directions) {
            var r = pos.row + dr
            var c = pos.col + dc
            while (r in 0..7 && c in 0..7) {
                val target = board.getPiece(Position(r, c))
                if (target == null) {
                    moves.add(Move(pos, Position(r, c)))
                } else {
                    if (target.color != color) moves.add(Move(pos, Position(r, c)))
                    break
                }
                r += dr
                c += dc
            }
        }
        return moves
    }

    private fun knightMoves(board: ChessBoard, pos: Position, color: PieceColor): List<Move> {
        val offsets = listOf(-2 to -1, -2 to 1, -1 to -2, -1 to 2, 1 to -2, 1 to 2, 2 to -1, 2 to 1)
        return offsets.mapNotNull { (dr, dc) ->
            val to = Position(pos.row + dr, pos.col + dc)
            if (to.isValid()) {
                val target = board.getPiece(to)
                if (target == null || target.color != color) Move(pos, to) else null
            } else null
        }
    }

    private fun kingMoves(board: ChessBoard, pos: Position, color: PieceColor): List<Move> {
        val offsets = listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)
        return offsets.mapNotNull { (dr, dc) ->
            val to = Position(pos.row + dr, pos.col + dc)
            if (to.isValid()) {
                val target = board.getPiece(to)
                if (target == null || target.color != color) Move(pos, to) else null
            } else null
        }
    }

    private fun pawnMoves(
        board: ChessBoard, pos: Position, color: PieceColor, enPassantTarget: Position?
    ): List<Move> {
        val moves = mutableListOf<Move>()
        val direction = if (color == PieceColor.WHITE) -1 else 1
        val startRow = if (color == PieceColor.WHITE) 6 else 1
        val promotionRow = if (color == PieceColor.WHITE) 0 else 7

        val oneStep = Position(pos.row + direction, pos.col)
        if (oneStep.isValid() && board.getPiece(oneStep) == null) {
            if (oneStep.row == promotionRow) {
                listOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT).forEach {
                    moves.add(Move(pos, oneStep, it))
                }
            } else {
                moves.add(Move(pos, oneStep))
                val twoStep = Position(pos.row + 2 * direction, pos.col)
                if (pos.row == startRow && board.getPiece(twoStep) == null) {
                    moves.add(Move(pos, twoStep))
                }
            }
        }

        for (dc in listOf(-1, 1)) {
            val capture = Position(pos.row + direction, pos.col + dc)
            if (capture.isValid()) {
                val target = board.getPiece(capture)
                if (target != null && target.color != color) {
                    if (capture.row == promotionRow) {
                        listOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT).forEach {
                            moves.add(Move(pos, capture, it))
                        }
                    } else {
                        moves.add(Move(pos, capture))
                    }
                }
                if (capture == enPassantTarget) {
                    moves.add(Move(pos, capture))
                }
            }
        }
        return moves
    }
}
