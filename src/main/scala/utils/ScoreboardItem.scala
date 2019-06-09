package utils

case class ScoreboardItem(name :String, time: Int) extends Ordered[ScoreboardItem ]{
  def compare(that: ScoreboardItem): Int = this.time compare that.time
}
