package domain

import db._
import play.api.Play.current
import com.mongodb.casbah.Imports._

case class ExpenseType(_id : ObjectId = new ObjectId, typeName : String) {
}

object ExpenseType  {
  
  val ID = "_id"
  val NAME = "typeName"
     
  val collection = MongoDBSetup.mongoDB("expensetype")

  def getExpenseType(expTypeId : ObjectId) = {
    val res = collection.findOne(MongoDBObject(ID -> expTypeId))
    res match {
    	case Some(et) => Some(convertFromMongoObject(et))
    	case None => None
    }
  }
  
  def getExpenseTypes() = {
    val res = collection.find().sort(MongoDBObject("typeName" -> 1)).toList
    createExpenseTypes(res)
  }
  
  def deleteExpenseType(expTypeId : ObjectId) = {
    updateExpenseDetailByExpType(expTypeId, None)
    collection.remove(MongoDBObject("_id" -> expTypeId))
  }
  
  def updateExpenseDetailByExpType(expTypeId : ObjectId, expenseType : Option[ExpenseType]){
    val expDetList = ExpenseDetail.getExpenseDetailsByExpTypeId(expTypeId)
    expDetList.foreach(expDet => {
      val newExpDet = ExpenseDetail(expDet._id, expDet.description, expDet.searchTags, expenseType)
      ExpenseDetail.updateExpenseDetail(newExpDet.copy(_id = newExpDet._id))
    })
  }

  def addExpenseType(expType : ExpenseType) {
    collection.insert(convertToMongoObject(expType))
  }

  def updateExpenseType(expType : ExpenseType) {
    updateExpenseDetailByExpType(expType._id, Some(expType))
    collection.save(convertToMongoObject(expType.copy(_id = expType._id))) 
  }

  private def createExpenseTypes(dbObjects: List[DBObject] ) = {
    dbObjects.map( dbO => convertFromMongoObject(dbO))
  }
  
  def convertFromMongoObject(dbObject: DBObject) : ExpenseType = {
    ExpenseType(
      dbObject.getAsOrElse[ObjectId](ID, mongoFail), 
      dbObject.getAsOrElse[String](NAME, mongoFail)
    )
  }

  def convertToMongoObject(expType: ExpenseType): DBObject = {
    val builder = MongoDBObject.newBuilder
    builder += ID -> expType._id
    builder += NAME -> expType.typeName 
    builder.result()
  }
}
