package views

import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType}
import scalafx.stage.Stage
import utils.Scoreboard

class ScoreboardAlert(stage: Stage, scoreboard: Scoreboard) extends Alert(AlertType.None) {
  initOwner(stage)
  title = s"Scoreboard dla ${scoreboard.settings.maxX}x${scoreboard.settings.maxY}  ${scoreboard.settings.bombsCount} bomb"
  headerText = ""
  contentText = scoreboard.getScoreboardAsString
  buttonTypes = Seq(ButtonType.OK)
}
