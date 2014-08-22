import db._
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import controllers.OverviewController
import org.joda.time.DateTime

@RunWith(classOf[JUnitRunner])
class OverviewSpec  extends Specification {

  "Overview" should {
      val test = OverviewController
    "create intervall" in new WithApplication{
        val ints = test.createIntervals(new DateTime(DummyDb.createDate(2013, 0, 1)), new DateTime)
        println(ints)
        println(ints.size)
        
        val names = ints.map(int => int.getStart.year.getAsText + " - " + int.getStart.monthOfYear.getAsText)
        names.foreach(println(_))
        ints.size must be equalTo(20)
    }
  }
}