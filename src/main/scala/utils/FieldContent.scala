package utils

sealed abstract class FieldContent

case class Bomb() extends FieldContent
case class EmptyField(nearBombs: Int) extends FieldContent
