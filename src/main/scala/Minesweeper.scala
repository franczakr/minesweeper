import javafx.event.ActionEvent
import javafx.scene.input.MouseEvent
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color._
import utils._
import views._

import scala.util.Random

object Minesweeper extends JFXApp {

  private var settings: GameSettings = GameSettings(10, 10, 10)
  private var fields: Array[Array[Field]] = _

  newGame()

  def newGame() {
    fields = Array.ofDim[Field](settings.maxX, settings.maxY)
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
                    onAction = (_: ActionEvent) => {
                      val result = new SettingsDialog(stage, settings).showAndWait()
                      result match {
                        case Some(GameSettings(w, h, b)) =>
                          if (GameSettings(w, h, b) != settings) {
                            settings = GameSettings(w, h, b)
                            newGame()
                          }
                        case _ =>
                      }
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
                    onAction = (_: ActionEvent) => new AuthorsAlert(stage).showAndWait()
                  },
                )
              },
            )
          }
          center = new GridPane {
            private var fieldsLeft = settings.maxX * settings.maxY
            private var bombsLeft = settings.bombsCount
            private val random = new Random()
            for (y <- 0 until settings.maxY) {
              for (x <- 0 until settings.maxX) {
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
            for (y <- 0 until settings.maxY) {
              for (x <- 0 until settings.maxX) {
                if (fields(x)(y).content != Bomb())
                  fields(x)(y).content = EmptyField(countNearBombs(fields, x, y))
              }
            }
          }
        }
      }
      centerOnScreen()
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
          val result = new LostGameAlert(stage).showAndWait()
          result match {
            case Some(ButtonTypes.NewGameButton) => newGame()
            case _ => Platform.exit(); System.exit(0)
          }
        }
        else if (field.content == EmptyField(0)) {
          val x = field.x
          val y = field.y
          for (dx <- -1 to 1) {
            for (dy <- -1 to 1) {
              if (x + dx >= 0 && x + dx < settings.maxX && y + dy >= 0 && y + dy < settings.maxY && fields(x + dx)(y + dy).state == UnVisited())
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
        if (x + dx >= 0 && x + dx < settings.maxX && y + dy >= 0 && y + dy < settings.maxY && fields(x + dx)(y + dy).content == Bomb())
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
    for (y <- 0 until settings.maxY) {
      for (x <- 0 until settings.maxX) {
        showField(fields(x)(y))
      }
    }
  }

}