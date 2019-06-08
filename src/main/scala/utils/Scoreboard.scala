package utils

import java.io.{File, FileNotFoundException, FileWriter, PrintWriter}

import scala.io.Source

class Scoreboard(val settings: GameSettings) {
//  val filename = s"res/score${settings.maxX}X${settings.maxY}X${settings.bombsCount}.txt"
  val filename = "C:\\Users\\piotr\\Desktop\\scala\\minesweeper\\target\\scala-2.12\\classes\\res\\score10X10X10.txt"

  var scoreboard :Array[ScoreboardItem] = Array.empty[ScoreboardItem]
  def initScoreBoard(): Unit = {
    try {
      for (line <- Source.fromFile(filename).getLines) {
        val splitted = line.split(",")
        println(splitted(0))
        scoreboard = Array.concat(scoreboard, Array(ScoreboardItem(splitted(0), splitted(1).toInt)))

      }
    }catch {
      case e: FileNotFoundException => {
        scoreboard = Array.empty[ScoreboardItem]
        val fileWriter = new FileWriter(new File(filename))
        fileWriter.write("HUEHUEHEUHEUHEHUEHUEH")
        fileWriter.close()
        throw new FileNotFoundException
    }
    }
    scoreboard = Array.concat(scoreboard, Array(ScoreboardItem("DUpa", 69)))

    scoreboard = scoreboard.sorted

  }

  def add(name: String, time: Int): Unit = {
    val scoreboardItem = ScoreboardItem(name, time)
    scoreboard = Array.concat(scoreboard, Array(scoreboardItem))
    scoreboard = scoreboard.sorted
    val writer = new PrintWriter(new File(filename))
    writer.println()
  }

  override def toString: String = {
    var ret: String = ""
    for (x: ScoreboardItem <- scoreboard) {
      ret = ret ++ x.name.toString ++ "," ++ x.time.toString ++ "\n"
    }
    ret
  }
}
