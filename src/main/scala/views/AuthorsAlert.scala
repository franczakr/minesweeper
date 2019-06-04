package views

import scalafx.scene.control.{Alert, ButtonType}
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage

class AuthorsAlert(stage: Stage) extends Alert(AlertType.None) {
  initOwner(stage)
  title = "Autorzy"
  headerText = ""
  contentText = "Rafał Franczak\nPiotr Kotara"
  buttonTypes = Seq(ButtonType.OK)
}
