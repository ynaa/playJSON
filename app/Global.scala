import db._
import modules._
import com.google.inject._
import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.http.HeaderNames._

import scala.concurrent.ExecutionContext.Implicits.global



object Global extends WithFilters(new CorsFilter) with GlobalSettings {

    private lazy val injector = {
        Play.isTest match {
            case true =>
                Guice.createInjector(new TestModule)
            case false => {
                Play.isProd match {
                    case true =>
                        Guice.createInjector(new ProdModule)
                    case false =>
                        Guice.createInjector(new DevModule)
                }
            }
        }
    }

    override def getControllerInstance[A](clazz: Class[A]) = {
        injector.getInstance(clazz)
    }

    override def onStart(app: play.api.Application) {
        Logger.info("Application has started")
    }    
}
