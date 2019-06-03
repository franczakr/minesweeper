package views

import scalafx.scene.control.Button

class Field (xx: Int, yy: Int) extends Button {
  private val size = 40
  var isBomb: Boolean = false
  var nearBombsCount = 0
  var x: Int = xx
  var y: Int = yy
  var visited: Boolean = false
  minWidth = size
  maxWidth = size
  minHeight = size
  maxHeight = size

}
