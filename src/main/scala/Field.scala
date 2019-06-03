import javafx.scene.input.MouseEvent
import scalafx.geometry.Insets
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import scalafx.scene.paint.Color

class Field () extends Button {
  private val size = 40
  var isBomb: Boolean = false
  var nearBombsCount = 0
  minWidth = size
  maxWidth = size
  minHeight = size
  maxHeight = size

}
