import play.api._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

object Global extends WithFilters(new CorsFilter) with GlobalSettings {

    override def onStart(app: play.api.Application) {
        Logger.info("Application has started")
    }    
}