package io.bitrise.chess.model

data class Move(val from: Position, val to: Position, val promotion: PieceType? = null)
