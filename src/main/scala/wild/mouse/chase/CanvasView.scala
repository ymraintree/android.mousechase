package wild.mouse.chase

import java.lang.Math
import java.util.Vector
import java.util.Date

import android.os._
import android.view._
import android.graphics._
import android.content._
import android.util._
import scala.collection.immutable.TreeMap
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
  var lastX, lastY: Int = _
  var stroke:Stroke = new Stroke
  
  clearCanvas

  var xArray, yArray: ListBuffer[Int] = _

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
    lastX = x
    lastY = y
    onStroke = true

    xArray = new ListBuffer[Int]
    yArray = new ListBuffer[Int]
    xArray += x
    yArray += y
  }

  private def touchDragged(x: Int, y: Int) {
    drawLine(lastX, lastY, x, y)
    val penWidthHalf: Int = penWidth / 2 + 1
    val minX = Math.min(x, lastX) - penWidthHalf
    val maxX = Math.max(x, lastX) + penWidthHalf
    val minY = Math.min(y, lastY) - penWidthHalf
    val maxY = Math.max(y, lastY) + penWidthHalf

    invalidate(new Rect(minX, minY, maxX, maxY))
    lastX = x
    lastY = y

    xArray += x
    yArray += y
  }

  private def touchReleased(x: Int, y: Int) {
    stroke.xArray = xArray
    stroke.yArray = yArray
//    xArray = null
//    yArray = null
    onStroke = false
    Log.d(this.getClass.getSimpleName, stroke.toString)
//    for (i <- 0 to xArray.size - 1) drawBall(xArray(i), yArray(i))
    
    i = 0
    update
  }
  
  private val handler = new RefreshHandler
  var i = 0
  def update {
    if (xArray.size <= i) return
    imageBuffer.get.eraseColor(WHITE)
    drawBall(xArray(i), yArray(i), Color.RED)
    if (5 <= i) drawBall(xArray(i - 5), yArray(i - 5), Color.GREEN)
    if (10 <= i) drawBall(xArray(i - 10), yArray(i - 10), Color.BLUE)
    i += 1
    CanvasView.this.invalidate
    handler.sleep(150)
  }

  def drawBall(x: Int, y: Int, color:Int) {
    Log.d(this.getClass.getSimpleName, "x=" + x + " y=" + y)
    val canvas: Canvas = new Canvas(imageBuffer.get)
    val paint = new Paint(Paint.ANTI_ALIAS_FLAG)
    paint.setColor(color)
    paint.setAntiAlias(true)
    canvas.drawCircle(x + BALL_HALF_SIZE, y + BALL_HALF_SIZE, BALL_HALF_SIZE, paint)
    invalidate
  }

  private def drawLine(lastX2: Int, lastY2: Int, x: Int, y: Int) {
    val canvas: Canvas = new Canvas(imageBuffer.get)
    val paint = new Paint(Paint.ANTI_ALIAS_FLAG)
    paint.setStrokeCap(Paint.Cap.ROUND)
    paint.setStrokeWidth(penWidth)
    paint.setColor(BLUE)
    canvas.drawLine(lastX2, lastY2, x, y, paint)
  }

  //	private def drawStroke(bitmap:Bitmap, stroke:Stroke) {
  //		if (stroke.xArray.size == 0) return
  //		val canvas:Canvas = new Canvas(bitmap)
  //		
  //		val paint = new Paint(Paint.ANTI_ALIAS_FLAG)
  //		paint.setStyle(Paint.Style.STROKE)
  //		paint.setStrokeCap(Paint.Cap.ROUND)
  //		paint.setStrokeJoin(Paint.Join.ROUND)
  //		paint.setStrokeWidth(penWidth)
  //		paint.setColor(BLACK)
  //
  //		val path:Path = new Path
  ////		if (stroke.xArray.get(0) == null || stroke.yArray.get(0) == null) return
  //		path.moveTo(stroke.xArray(0), stroke.yArray(0))
  //		for (i <- 1 to stroke.xArray.size - 1)
  //			path.lineTo(stroke.xArray(i), stroke.yArray(i))
  //		canvas.drawPath(path, paint)
  //	}

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