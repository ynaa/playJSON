import db._
import modules._
import com.google.inject._
import play.api._
import play.api.Play
import play.api.Play.current

object Global extends GlobalSettings {

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