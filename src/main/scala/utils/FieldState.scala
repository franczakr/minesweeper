package utils

sealed abstract class FieldState

case class UnVisited() extends FieldState
case class Visited() extends FieldState
case class Flagged() extends FieldState