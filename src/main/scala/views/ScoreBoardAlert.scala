package views

import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.Stage
import utils.ButtonTypes
import utils.Scoreboard

class ScoreBoardAlert(stage: Stage, scoreboard: Scoreboard) extends Alert(AlertType.None) {
  initOwner(stage)
  title = "Scoreboard dla " ++ scoreboard.settings.maxX.toString ++ scoreboard.settings.maxY.toString
  headerText = ""
  contentText = scoreboard.toString
  buttonTypes = Seq(ButtonTypes.NewGameButton, ButtonTypes.QuitButton)
}
