package utils

case class Point2D(x: Double, y: Double)

object Vector2D {
  def makeV2d(from: Point2D, to: Point2D) = new Vector2D(to.x - from.x, to.y - from.y)
}

case class Vector2D(x: Double, y: Double) {
  
  def sum(v: Vector2D) = new Vector2D(x + v.x, y + v.y)

  def abs: Double = Math.sqrt(x * x + y * y)

  def getNormalized: Vector2D = {
    val module = Math.sqrt(x * x + y * y)
    new Vector2D(x / module, y / module)
  }

  def mul(fact: Double) = new Vector2D(x * fact, y * fact)

  override def toString: String = "V2d(" + x + "," + y + ")"
  
}


