package views

import scalafx.scene.control.Button
import utils.{EmptyField, FieldContent, FieldState, UnVisited}

class Field (xx: Int, yy: Int) extends Button {
  private val size = 40
  var x: Int = xx
  var y: Int = yy
  var state: FieldState = UnVisited()
  var content: FieldContent = EmptyField(0)
  minWidth = size
  maxWidth = size
  minHeight = size
  maxHeight = size
}
