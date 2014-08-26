package db

import play.Play
import com.mongodb.casbah._

object MongoDBSetup {

  val dbApi : MyEconomyDbApi = DummyDb
  val dbName = Play.application().configuration().getString("mongodb.default.db")
  val mongoDB = MongoClient()(dbName)

}