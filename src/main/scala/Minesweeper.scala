import javafx.event.ActionEvent
import javafx.scene.input.MouseEvent
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.geometry.Insets
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color._
import utils.{Flagged, UnVisited, Visited}
import views.Field

import scala.util.Random

object Minesweeper extends JFXApp {

  private val mapDims = 10
  private val fields = Array.ofDim[Field](mapDims, mapDims)

  newGame()

  def newGame() {
    stage = new PrimaryStage {
      title = "Minesweeper"
      icons.add(new Image("res/bomb.png"))
      scene = new Scene {
        fill = White
        root = new BorderPane {
          top = new MenuBar {
            useSystemMenuBar = true
            menus = List(
              new Menu("Game") {
                items = List(
                  new MenuItem("New Game") {
                    onAction = (_: ActionEvent) => {
                      newGame()
                    }
                  },
                  new MenuItem("Exit") {
                    onAction = (_: ActionEvent) => {
                      Platform.exit()
                      System.exit(0)
                    }
                  },
                )
              },
              new Menu("Help") {
                items = List(
                  new MenuItem("Authors") {
                    onAction = (_: ActionEvent) => {
                      new Alert(AlertType.None) {
                        initOwner(stage)
                        title = "Autorzy"
                        headerText = ""
                        contentText = "Rafał Franczak\nPiotr Kotara"
                        buttonTypes = Seq(ButtonType.OK)
                      }.showAndWait()
                    }
                  },
                )
              },
            )
          }
          center = new GridPane {
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
    }
  }

  private def getBackground(red: Int, green: Int, blue: Int, alpha: Double = 1.0): Background = {
    new Background(Array(new BackgroundFill(
      new Color(Color.rgb(red, green, blue, alpha)),
      new CornerRadii(javafx.scene.layout.CornerRadii.EMPTY),
      Insets.Empty
    )))
  }

  private def onFieldClick(field: Field, mouseEvent: MouseEvent = null) {
    if (field.state != Visited() && mouseEvent != null) {
      if (field.state == UnVisited() && mouseEvent.getButton == javafx.scene.input.MouseButton.PRIMARY) {
        field.state = Visited()
        field.background = getBackground(200, 200, 200)
        if (field.isBomb) {
          field.graphic = new ImageView("res/bomb.png")
          val NewGameButton = new ButtonType("New Game")
          val QuitButton = new ButtonType("Quit")
          val result = new Alert(AlertType.None) {
            initOwner(stage)
            title = "Przegrałeś"
            headerText = ""
            contentText = "No sory, zostałeś wyjebany w powietrze"
            buttonTypes = Seq(NewGameButton, QuitButton)
          }.showAndWait()
          result match {
            case Some(NewGameButton) => newGame()
            case _ => Platform.exit(); System.exit(0)
          }
        }
        else {
          if (field.nearBombsCount == 0) {
            val x = field.x
            val y = field.y
            for (dx <- -1 to 1) {
              for (dy <- -1 to 1) {
                if (x + dx >= 0 && x + dx < mapDims && y + dy >= 0 && y + dy < mapDims &&
                  !fields(x + dx)(y + dy).isBomb && fields(x + dx)(y + dy).state == UnVisited())
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
      else if (mouseEvent.getButton == javafx.scene.input.MouseButton.SECONDARY) {
        if(field.state == Flagged()) {
          field.setGraphic(null)
          field.state = UnVisited()
        }
        else {
          field.graphic = new ImageView("res/flag.png")
          field.state = Flagged()
        }
      }
    }
  }

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

}