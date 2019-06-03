import javafx.scene.input.MouseEvent
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii, GridPane}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color._
import views.Field

import scala.util.Random

object Minesweeper extends JFXApp {
  def countNearBombs(fields: Array[Array[Field]], x: Int, y: Int): Int = {
    var nearBombs = 0
    for (dx <- -1 to 1) {
      for (dy <- -1 to 1) {
        if (x + dx >= 0 && x + dx < mapDims && y + dy >= 0 && y + dy < mapDims && fields(x + dx)(y + dy).isBomb)
          nearBombs += 1
      }
    }
    nearBombs
  }

  private val mapDims = 10
  private val fields = Array.ofDim[Field](mapDims, mapDims)
  stage = new PrimaryStage {
    title = "Minesweeper"
    scene = new Scene {
      fill = White
      content = new GridPane {
        for (y <- 0 until mapDims) {
          for (x <- 0 until mapDims) {
            fields(x)(y) = new Field(x, y) {
              onMouseClicked = (mouseEvent: MouseEvent) => onFieldClick(fields(x)(y), mouseEvent)
            }
            fields(x)(y).isBomb = if (new Random().nextInt(10) > 8) true else false
            add(fields(x)(y), x, y, 1, 1)
          }
        }
        for (y <- 0 until mapDims) {
          for (x <- 0 until mapDims) {
            fields(x)(y).nearBombsCount = countNearBombs(fields, x, y)
          }
        }
      }
    }
  }

  private def getBackground(red: Int, green: Int, blue: Int, alpha: Double = 1.0): Background = {
    new Background(Array(new BackgroundFill(
      new Color(Color.rgb(red, green, blue, alpha)),
      new CornerRadii(javafx.scene.layout.CornerRadii.EMPTY),
      Insets.Empty
    )))
  }

  private def onFieldClick(field: Field, mouseEvent: MouseEvent = null): Unit = {
    if (!field.visited) {
      field.visited = true
      field.background = getBackground(200, 200, 200)
      if (field.isBomb) {
        field.graphic = new ImageView("bomb.png")
        new Alert(AlertType.Error) {
          initOwner(this.owner)
          title = "PRZEGRAŁEŚ"
          headerText = ""
          contentText = "No sory, zostałeś wyjebany w powietrze"
        }.showAndWait()
      }
      else {
        if (field.nearBombsCount == 0) {
          val x = field.x
          val y = field.y
          for (dx <- -1 to 1) {
            for (dy <- -1 to 1) {
              if (x + dx >= 0 && x + dx < mapDims && y + dy >= 0 && y + dy < mapDims &&
                !fields(x + dx)(y + dy).isBomb && !fields(x + dx)(y + dy).visited)
                onFieldClick(fields(x + dx)(y + dy))
            }
          }
        }
        else if (field.nearBombsCount > 0) {
          field.text = field.nearBombsCount.toString
          if (field.nearBombsCount == 1)
            field.background = getBackground(217, 255, 179)
          else if (field.nearBombsCount == 2)
            field.background = getBackground(179, 255, 255)
          else if (field.nearBombsCount == 3)
            field.background = getBackground(255, 255, 179)
          else if (field.nearBombsCount == 4)
            field.background = getBackground(255, 217, 179)
          else if (field.nearBombsCount == 5)
            field.background = getBackground(255, 179, 179)
          else if (field.nearBombsCount >= 6)
            field.background = getBackground(255, 153, 153)
        }
      }

    }
  }

}