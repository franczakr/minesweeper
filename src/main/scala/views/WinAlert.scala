package views

import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage
import utils.ButtonTypes


class WinAlert(stage: Stage) extends Alert(AlertType.None) {
  initOwner(stage)
  title = "Wygrałeś"
  headerText = ""
  contentText = "Brawo, odkryłeś wszyskie bezpieczne pola i przeżyłeś!"
  buttonTypes = Seq(ButtonTypes.NewGameButton, ButtonTypes.QuitButton)
}
