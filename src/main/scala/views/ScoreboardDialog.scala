package views

import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.text
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType, Dialog}
import scalafx.scene.text.Font
import scalafx.stage.Stage
import utils.{Scoreboard, ScoreboardItem}

class ScoreboardDialog(stage: Stage, scoreboard: Scoreboard) extends Dialog {
  initOwner(stage)
  title = "Scoreboard"
  headerText = s"Scoreboard dla ${scoreboard.settings.maxX}x${scoreboard.settings.maxY}  ${scoreboard.settings.bombsCount} bomb"
  dialogPane.value.setContent(
    new GridPane() {
      setHgap(80)
      setVgap(5)

      private val labelFont = Font.font("System", text.FontWeight.BOLD, 12.0)
      add(new Label("Name:"){setFont(labelFont)}, 0, 0)
      add(new Label("Time:"){setFont(labelFont)}, 1, 0)


      var line: Int = 1
      for (item: ScoreboardItem <- scoreboard.scoreboard){
        add(new Label(item.name), 0, line)
        add(new Label(item.time.toString), 1, line)
        line += 1
      }
    }
  )
  dialogPane.value.getButtonTypes.setAll(ButtonType.OK)
}
