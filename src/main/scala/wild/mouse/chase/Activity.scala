package wild.mouse.chase

import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.widget.TextView

object MainActivity {
	var canvasView:CanvasView = _
}

class MainActivity extends Activity with TypedActivity {
	
    override def onCreate(savedInstanceState: Bundle) {
    	super.onCreate(savedInstanceState)
    	setContentView(R.layout.main)
    
    	MainActivity.canvasView = findView(TR.canvas_view)
    }

}
