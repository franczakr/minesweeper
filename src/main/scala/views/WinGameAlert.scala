package views

import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage
import utils.ButtonTypes


class WinGameAlert(stage: Stage, time: Int) extends Alert(AlertType.None) {
  initOwner(stage)
  title = "Wygrałeś"
  headerText = ""
  contentText = s"Brawo, odkryłeś wszyskie bezpieczne pola i przeżyłeś!\n Twój czas to $time s"
  buttonTypes = Seq(ButtonTypes.NewGameButton, ButtonTypes.QuitButton)
}
