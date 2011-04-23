package wild.mouse.chase

import java.lang.Math

import android.os._
import android.view._
import android.graphics._
import android.content._
import android.util._
import scala.collection.mutable.ListBuffer

class CanvasView(context: Context, attrs: AttributeSet) extends View(context, attrs) {

  val BALL_SIZE = 20
  val BALL_HALF_SIZE = BALL_SIZE / 2
  val BLUE = 0xff0000ff
  val WHITE = 0xffffffff
  val penWidth = 3
  val width = 480
  val height = 860
  var onStroke = false
  var imageBuffer: Option[Bitmap] = None
  var last: (Int, Int) = _
  var xys: ListBuffer[(Int, Int)] = _
  
  clearCanvas

  private def clearCanvas {
    imageBuffer match {
      case Some(n) => n.recycle
      case None => imageBuffer = Some(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))
    }
    imageBuffer.get.eraseColor(WHITE)
    invalidate
  }

  override def onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (imageBuffer != None) canvas.drawBitmap(imageBuffer.get, 0, 0, null);
  }

  override def onTouchEvent(event: MotionEvent): Boolean = {
    if (imageBuffer == None) return false;
    val x = event.getX.asInstanceOf[Int]
    val y: Int = event.getY.asInstanceOf[Int]

    event.getAction match {
      case MotionEvent.ACTION_DOWN => touchPressed(x, y)
      case MotionEvent.ACTION_MOVE => touchDragged(x, y)
      case MotionEvent.ACTION_UP => touchReleased(x, y)
    }
    true
  }

  private def touchPressed(x: Int, y: Int) {
    last = (x, y)
    xys = new ListBuffer[(Int, Int)]
    // こうしないと、eclipseではエラーになってしまう
    val now = (x, y); xys += now
  }

  private def touchDragged(x: Int, y: Int) {
    drawLine(last._1, last._2, x, y)
    val penWidthHalf: Int = penWidth / 2 + 1
    val minX = Math.min(x, last._1) - penWidthHalf
    val maxX = Math.max(x, last._1) + penWidthHalf
    val minY = Math.min(y, last._2) - penWidthHalf
    val maxY = Math.max(y, last._2) + penWidthHalf

    invalidate(new Rect(minX, minY, maxX, maxY))
    last = (x, y)
    val now = (x, y); xys += now
  }

  private def touchReleased(x: Int, y: Int) {
    onStroke = false
    
    i = 0
    update
  }
  
  private val handler = new RefreshHandler
  var i = 0
  def update {
    if (xys.size <= i - 20) return
    imageBuffer.get.eraseColor(WHITE)
    drawBall(i)
    i += 1
    CanvasView.this.invalidate
    handler.sleep(150)
  }
  
  private def drawBall(i:Int) {
    for (offset <- List(0, 5, 10, 15, 20)) {
      val color = offset match {
        case 0 => Color.RED
        case 5 => Color.GREEN
        case 10 => Color.YELLOW
        case 15 => Color.MAGENTA
        case 20 => Color.BLUE
      }
      if (i < xys.size && offset <= i) drawBall(xys(i - offset)._1, xys(i - offset)._2, color)
      if (xys.size <= i) drawBall(xys.last._1, xys.last._2, color)  
    }
  }

  def drawBall(x: Int, y: Int, color:Int) {
    Log.d(this.getClass.getSimpleName, "drawBall x=" + x + " y=" + y + " color=" + color)
    val canvas: Canvas = new Canvas(imageBuffer.get)
    val paint = new Paint(Paint.ANTI_ALIAS_FLAG)
    paint.setColor(color)
    paint.setAntiAlias(true)
    canvas.drawCircle(x + BALL_HALF_SIZE, y + BALL_HALF_SIZE, BALL_HALF_SIZE, paint)
    invalidate
  }

  private def drawLine(lastX: Int, lastY: Int, x: Int, y: Int) {
    val canvas: Canvas = new Canvas(imageBuffer.get)
    val paint = new Paint(Paint.ANTI_ALIAS_FLAG)
    paint.setStrokeCap(Paint.Cap.ROUND)
    paint.setStrokeWidth(penWidth)
    paint.setColor(BLUE)
    canvas.drawLine(lastX, lastY, x, y, paint)
  }

  private class RefreshHandler extends Handler {
    def sleep(delayMillis: Long) {
      removeMessages(0)
      sendMessageDelayed(obtainMessage(0), delayMillis)
    }

    override def handleMessage(msg: Message) {
      CanvasView.this.update
    }
  }

}