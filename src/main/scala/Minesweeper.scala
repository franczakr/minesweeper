import javafx.event.ActionEvent
import javafx.scene.input.MouseEvent
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color._
import utils._
import views.Field

import scala.util.Random

object Minesweeper extends JFXApp {

  private val maxX = 7
  private val maxY = 10
  private val bombsCount = 10

  private val fields = Array.ofDim[Field](maxX, maxY)

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
                    onAction = (_: ActionEvent) => newGame()
                  },
                  new MenuItem("Options") {
                    onAction = (_: ActionEvent) => {} //TODO
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
            private var fieldsLeft = maxX * maxY
            private var bombsLeft = bombsCount
            private val random = new Random()
            for (y <- 0 until maxY) {
              for (x <- 0 until maxX) {
                fields(x)(y) = new Field(x, y) {
                  onMouseClicked = (mouseEvent: MouseEvent) => onFieldClick(fields(x)(y), mouseEvent)
                }
                if (bombsLeft > 0 && random.nextDouble() * fieldsLeft <= bombsLeft.toDouble) {
                  fields(x)(y).content = Bomb()
                  bombsLeft -= 1
                }
                add(fields(x)(y), x, y, 1, 1)
                fieldsLeft -= 1
              }
            }
            for (y <- 0 until maxY) {
              for (x <- 0 until maxX) {
                if (fields(x)(y).content != Bomb())
                  fields(x)(y).content = EmptyField(countNearBombs(fields, x, y))
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
    if (field.state != Visited()) {
      if (mouseEvent != null && mouseEvent.getButton == javafx.scene.input.MouseButton.SECONDARY) {
        if (field.state == Flagged()) {
          field.setGraphic(null)
          field.state = UnVisited()
        }
        else {
          field.graphic = new ImageView("res/flag.png")
          field.state = Flagged()
        }
      }
      else if (field.state == UnVisited()) {
        field.state = Visited()
        showField(field)
        if (field.content == Bomb()) {
          showAll()
          field.background = getBackground(255, 0, 0, 0.9)
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
        else if (field.content == EmptyField(0)) {
          val x = field.x
          val y = field.y
          for (dx <- -1 to 1) {
            for (dy <- -1 to 1) {
              if (x + dx >= 0 && x + dx < maxX && y + dy >= 0 && y + dy < maxY && fields(x + dx)(y + dy).state == UnVisited())
                onFieldClick(fields(x + dx)(y + dy))
            }
          }
        }
      }
    }
  }

  def countNearBombs(fields: Array[Array[Field]], x: Int, y: Int): Int = {
    var nearBombs = 0
    for (dx <- -1 to 1) {
      for (dy <- -1 to 1) {
        if (x + dx >= 0 && x + dx < maxX && y + dy >= 0 && y + dy < maxY && fields(x + dx)(y + dy).content == Bomb())
          nearBombs += 1
      }
    }
    nearBombs
  }

  def showField(field: Field): Unit = {
    field.background = getBackground(200, 200, 200)
    field.content match {
      case Bomb() => field.graphic = new ImageView("res/bomb.png")
      case EmptyField(c) if c > 0 =>
        field.text = c.toString
        c match {
          case 1 => field.background = getBackground(217, 255, 179)
          case 2 => field.background = getBackground(179, 255, 255)
          case 3 => field.background = getBackground(255, 255, 179)
          case 4 => field.background = getBackground(255, 217, 179)
          case 5 => field.background = getBackground(255, 179, 179)
          case _ => field.background = getBackground(255, 153, 153)
        }
      case _ =>
    }
  }

  def showAll(): Unit = {
    for (y <- 0 until maxY) {
      for (x <- 0 until maxX) {
        showField(fields(x)(y))
      }
    }
  }

}