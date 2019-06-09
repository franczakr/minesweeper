package utils

import java.io._
import java.nio.file.{Files, Path, Paths}

class Scoreboard(val settings: GameSettings) {
  val scoreboardPath: Path = Paths.get(new File(".").getCanonicalPath, "scoreboard")
  val filename: String = s"score${settings.maxX}x${settings.maxY}x${settings.bombsCount}.txt"
  val filePath: Path = Paths.get(scoreboardPath.toString, filename)

  var scoreboard :Array[ScoreboardItem] = Array.empty[ScoreboardItem]
  def initScoreboard(): Unit = {
    try {
      if(!Files.exists(scoreboardPath))
        Files.createDirectory(scoreboardPath)
      for (line <- Files.readAllLines(filePath).toArray(new Array[String](0))) {
        val splitted = line.split(",")
        scoreboard = Array.concat(scoreboard, Array(ScoreboardItem(splitted(0), splitted(1).toInt)))
      }
    } catch {
      case _: FileNotFoundException =>
        scoreboard = Array.empty[ScoreboardItem]
        Files.createFile(filePath)
    }
    scoreboard = scoreboard.sorted
  }

  def add(name: String, time: Int): Unit = {
    val scoreboardItem = ScoreboardItem(name, time)
    scoreboard = Array.concat(scoreboard, Array(scoreboardItem))
    scoreboard = scoreboard.sorted.take(10)
    Files.write(filePath, getScoreboardAsString.getBytes)
  }

  def getScoreboardAsString: String = {
    var ret: String = ""
    for (x: ScoreboardItem <- scoreboard) {
      ret = ret ++ x.name.toString ++ "," ++ x.time.toString ++ "\n"
    }
    ret
  }
}
