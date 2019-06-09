package utils

import java.io._
import java.nio.file.{Files, Paths}

import scala.io.Source

class Scoreboard(val settings: GameSettings) {
  val scoreboardPath: String = new File(".").getCanonicalPath + "/scoreboard"
  val filename: String = s"score${settings.maxX}x${settings.maxY}x${settings.bombsCount}.txt"
  val filePath: String = s"${scoreboardPath.toString}/$filename"

  var scoreboard :Array[ScoreboardItem] = Array.empty[ScoreboardItem]
  def initScoreboard(): Unit = {
    try {
      if(!Files.exists(Paths.get(scoreboardPath)))
        Files.createDirectory(Paths.get(scoreboardPath))
      for (line <- Source.fromFile(filePath.toString).getLines) {
        val splitted = line.split(",")
        scoreboard = Array.concat(scoreboard, Array(ScoreboardItem(splitted(0), splitted(1).toInt)))
      }
    } catch {
      case _: FileNotFoundException =>
        scoreboard = Array.empty[ScoreboardItem]
        val fileWriter = new FileWriter(new File(filePath))
        fileWriter.write("")
        fileWriter.close()
    }
    scoreboard = scoreboard.sorted
  }

  def add(name: String, time: Int): Unit = {
    val scoreboardItem = ScoreboardItem(name, time)
    scoreboard = Array.concat(scoreboard, Array(scoreboardItem))
    scoreboard = scoreboard.sorted.take(10)
    val writer = new PrintWriter(new File(filePath))
    writer.print(getScoreboardAsString)
    writer.close()
  }

  def getScoreboardAsString: String = {
    var ret: String = ""
    for (x: ScoreboardItem <- scoreboard) {
      ret = ret ++ x.name.toString ++ "," ++ x.time.toString ++ "\n"
    }
    ret
  }
}
