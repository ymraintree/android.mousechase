package wild.mouse.chase

import scala.collection.mutable.ListBuffer
import java.util.Date

class Stroke {
	var clientTime:Long = (new Date).getTime
	var xArray:ListBuffer[Int] = new ListBuffer[Int]
	var yArray:ListBuffer[Int] = new ListBuffer[Int]
	
	private def setupVariables {
		clientTime = 0
	}
	
	override def toString = {
		var result = 
			" clientTime:" + clientTime +
			" points:"
		for (i <- 0 to xArray.size - 1) result += xArray(i) + "-" + yArray(i) + " "
		result
	}
}