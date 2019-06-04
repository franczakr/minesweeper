import javafx.event.ActionEvent
import javafx.scene.input.MouseEvent
import javafx.scene.text
import javafx.scene.text.FontPosture
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.beans.property.IntegerProperty
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout._
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color._
import scalafx.scene.text.{Font, FontWeight}
import utils._
import views._

import scala.util.Random

object Minesweeper extends JFXApp {

  private var settings: GameSettings = GameSettings(10, 10, 10)
  private val flagCount = new IntegerProperty()
  private var fieldsToUncover = 0
  private var fields: Array[Array[Field]] = _

  newGame()

  def newGame() {
    flagCount.value = 0
    fieldsToUncover = settings.maxX * settings.maxY - settings.bombsCount
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
          center = new VBox {
            children = Seq(
              new BorderPane {
                private val labelFont = Font.font("System", text.FontWeight.SEMI_BOLD, 12.0)
                padding = Insets(5, 20, 5, 20)
                left = new Label("Bombs: " + settings.bombsCount) {font.value = labelFont}
                right = new Label() {
                  alignment = Pos.BaselineRight
                  text.bind(flagCount.asString("Flags: %s"))
                  font.value = labelFont
                }
              },
              new GridPane {
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
              },
            )
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
          flagCount.value -= 1
        }
        else {
          field.graphic = new ImageView("res/flag.png")
          field.state = Flagged()
          flagCount.value += 1
        }
      }
      else if (field.state == UnVisited()) {
        fieldsToUncover -= 1
        field.state = Visited()
        showField(field)
        if (field.content == Bomb()) {
          showAll()
          field.background = getBackground(255, 0, 0, 0.9)
          val result = new LostGameAlert(stage).showAndWait()
          result match {
            case Some(ButtonTypes.NewGameButton) => newGame(); return
            case _ => Platform.exit(); System.exit(0)
          }
        }
        else if (field.content == EmptyField(0)) {
          showEmptyFields(field)
        }
        if (fieldsToUncover == 0) {
          for (y <- 0 until settings.maxY) {
            for (x <- 0 until settings.maxX) {
              if (fields(x)(y).state != Flagged() && fields(x)(y).content == Bomb())
                fields(x)(y).graphic = new ImageView("res/green_flag.png")
            }
          }
          val result = new WinAlert(stage).showAndWait()
          result match {
            case Some(ButtonTypes.NewGameButton) => newGame(); return
            case _ => Platform.exit(); System.exit(0)
          }
        }
      }
    }
  }

  def showEmptyFields(field: Field): Unit = {
    if (field.state == UnVisited()) {
      fieldsToUncover -= 1
      field.state = Visited()
      showField(field)
    }
    if (field.content == EmptyField(0)) {
      val x = field.x
      val y = field.y
      for (dx <- -1 to 1) {
        for (dy <- -1 to 1) {
          if (x + dx >= 0 && x + dx < settings.maxX && y + dy >= 0 && y + dy < settings.maxY &&
            fields(x + dx)(y + dy).state == UnVisited() && fields(x + dx)(y + dy).content != Bomb())
            showEmptyFields(fields(x + dx)(y + dy))
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
        if (fields(x)(y).state == Flagged()) {
          if (fields(x)(y).content != Bomb())
            fields(x)(y).graphic = new ImageView("res/bad_flag.png")
        }
        else if (fields(x)(y).state == UnVisited()) {
          showField(fields(x)(y))
        }
      }
    }
  }

}