package db

import play.Play
import com.mongodb.casbah.{MongoClient, MongoClientURI}

object MongoDBSetup {

  val dbApi : MyEconomyDbApi = new MongoDb
  
  val dbName = Play.application().configuration().getString("mongodb.default.db")
  val mongoURI = Play.application().configuration().getString("mongodb.uri")
  
  val mDb = MongoClient(MongoClientURI(mongoURI))
  val mongoDB = mDb(dbName)
}