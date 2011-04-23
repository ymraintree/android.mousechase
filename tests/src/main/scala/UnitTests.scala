package wild.mouse.chase.tests

import junit.framework.Assert._
import _root_.android.test.AndroidTestCase

class UnitTests extends AndroidTestCase {
  def testPackageIsCorrect {
    assertEquals("wild.mouse.chase", getContext.getPackageName)
  }
}