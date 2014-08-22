import db._
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: play.api.Application) {

    Logger.info("Application has started")
    println("er vi her, GlobalSettings")
    DummyDb.initData
  }

}