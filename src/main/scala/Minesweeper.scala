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

import scala.util.Random

object Minesweeper extends JFXApp {
  def countNearBombs(fields: Array[Array[Field]], x: Int, y: Int): Int =
  {
    var nearBombs = 0
    for(dx <- -1 to 1) {
      for (dy <- -1 to 1) {
          if((dx != 0 || dy != 0) && x+dx >= 0 && x+dx < mapDims && y+dy >= 0 && y+dy < mapDims && fields(x+dx)(y+dy).isBomb)
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
        for( y <- 0 until mapDims) {
          for(x <- 0 until mapDims) {
            fields(x)(y) = new Field(){
              onMouseClicked = (_: MouseEvent) => {
                background = getBackground(200,200,200)
                if(isBomb) {
                  graphic = new ImageView("bomb.png")
                  new Alert(AlertType.Error) {
                    initOwner(this.owner)
                    title = "PRZEGRAŁEŚ"
                    headerText = ""
                    contentText = "No sory, zostałeś wyjebany w powietrze"
                  }.showAndWait()
                }
                else if(nearBombsCount > 0) {
                  text = nearBombsCount.toString
                  if (nearBombsCount == 1)
                    background = getBackground(217, 255, 179)
                  else if (nearBombsCount == 2)
                    background = getBackground(179, 255, 255)
                  else if (nearBombsCount == 3)
                    background = getBackground(255, 255, 179)
                  else if (nearBombsCount == 4)
                    background = getBackground(255, 217, 179)
                  else if (nearBombsCount == 5)
                    background = getBackground(255, 179, 179)
                  else if (nearBombsCount >= 6)
                    background = getBackground(255, 153, 153)
                }
              }
            }
            fields(x)(y).isBomb = if (new Random().nextInt(10) > 8) true else false
            add(fields(x)(y), x, y,1, 1)
          }
        }
        for( y <- 0 until mapDims) {
          for(x <- 0 until mapDims) {
            fields(x)(y).nearBombsCount = countNearBombs(fields, x, y)
          }
        }
      }
    }
  }

  private def getBackground(red: Int, green: Int, blue: Int, alpha: Double=1.0): Background =
  {
    new Background(Array(new BackgroundFill(
      new Color(Color.rgb(red,green,blue, alpha)),
      new CornerRadii(javafx.scene.layout.CornerRadii.EMPTY),
      Insets.Empty
    )))
  }

}