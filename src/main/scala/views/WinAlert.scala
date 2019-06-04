package views

import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage
import utils.ButtonTypes


class WinAlert(stage: Stage) extends Alert(AlertType.None) {
  initOwner(stage)
  title = "Wygrałeś"
  headerText = ""
  contentText = "Brawo, wygrałeś xD"
  buttonTypes = Seq(ButtonTypes.NewGameButton, ButtonTypes.QuitButton)
}
