import db._
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.mvc._
import play.api.test._
import scala.concurrent.Future
import play.api.test.Helpers._
import controllers._
import org.joda.time.DateTime

object OverviewSpec  extends Specification with Results {

  "Overview" should {
    
    "return intervall" in {
      running(FakeApplication()) {

        val show = route(FakeRequest(GET, "/overview")).get
        
        status(show) must equalTo(OK)
        contentType(show) must beSome.which(_ == "application/json")
        
      }
    }
  }
}