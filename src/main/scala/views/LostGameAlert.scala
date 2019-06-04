package views

import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage
import utils.ButtonTypes


class LostGameAlert(stage: Stage) extends Alert(AlertType.None) {
  initOwner(stage)
  title = "Przegrałeś"
  headerText = ""
  contentText = "No sory, zostałeś wyjebany w powietrze"
  buttonTypes = Seq(ButtonTypes.NewGameButton, ButtonTypes.QuitButton)
}
