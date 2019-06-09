package views

import javafx.scene.layout.GridPane
import scalafx.scene.control.{ButtonType, Dialog, Label, TextField}
import scalafx.stage.Stage
import utils.GameSettings

class SettingsDialog(stage: Stage, gameSettings: GameSettings) extends Dialog[GameSettings] {
  initOwner(stage)
  title = "Settings"
  headerText = ""

  private val mapXField = new NumericField(40){ text = gameSettings.maxX.toString }
  private val mapYField = new NumericField(20){ text = gameSettings.maxY.toString }
  private val bombsCountField = new NumericField(600){ text = gameSettings.bombsCount.toString }
  private val playerNameField = new TextField{ text = gameSettings.playerName }

  dialogPane.value.setContent(
    new GridPane() {
      setHgap(20)
      setVgap(10)

      add(new Label("Width:"), 0, 0)
      add(mapXField, 1, 0)
      add(new Label("Height:"), 0, 1)
      add(mapYField, 1, 1)
      add(new Label("Bombs count:"), 0, 2)
      add(bombsCountField, 1, 2)
      add(new Label("Player name:"), 0, 3)
      add(playerNameField, 1, 3)
    }
  )

  dialogPane.value.getButtonTypes.setAll(ButtonType.OK, ButtonType.Cancel)

  resultConverter = buttonType => {
    if (buttonType == ButtonType.OK)
      GameSettings( if (mapXField.getText != "") mapXField.getText.toInt else 0,
        if (mapYField.getText != "") mapYField.getText.toInt else 0,
        if (bombsCountField.getText != "") bombsCountField.getText.toInt else 0,
        playerNameField.getText)
    else
      null
  }
}
