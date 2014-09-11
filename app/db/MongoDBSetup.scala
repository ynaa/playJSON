package db

import play.Play
import com.mongodb.casbah._

object MongoDBSetup {

  val dbApi : MyEconomyDbApi = new MongoDb
  val dbName = Play.application().configuration().getString("mongodb.default.db")
  val mongoURI = Play.application().configuration().getString("mongodb.uri")
  val uri = MongoClientURI(mongoURI)
  val mDb = MongoClient(uri)
  val mongoDB = mDb(dbName)

  val mongoDB1 = MongoClient()(dbName)
}