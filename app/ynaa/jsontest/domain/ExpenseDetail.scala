package ynaa.jsontest.domain

import db._
import play.api.Play.current
import com.mongodb.casbah.Imports._

case class ExpenseDetail ( 
    _id: ObjectId = new ObjectId, 
    description : String,
    searchTags : List[String],
    expenseType : Option[ExpenseType] ){
}
object ExpenseDetail {
  
  val ID = "_id"
  val DESCRIPTION = "description"
  val TAGS = "searchTags"
  val EXPTYPE = "expenseType"
  
  val collection = MongoDBSetup.mongoDB("expensedetail")
  
  def getExpenseDetails = {
    val details = collection.find.sort(MongoDBObject("description" -> 1)).toList
    createExpenseDetails(details)
  }

  def getExpenseDetailsByExpTypeId(expTypeId : ObjectId) = {
    val where = MongoDBObject("expenseType._id" -> expTypeId)
    val order = MongoDBObject("description" -> 1)
    val details = collection.find(where).sort(order).toList
    createExpenseDetails(details)
  }

  def deleteExpenseDetail(expDetId : ObjectId) = {
    updatePurchaseByExpDetId(expDetId, None)
    collection.remove(MongoDBObject("_id" -> expDetId))
  }
  
  def updatePurchaseByExpDetId(expDetId : ObjectId, expDet : Option[ExpenseDetail]){
    val purchases = Purchase.getPurhcaseByExpDetId(Some(expDetId))
    purchases.foreach(purchase => {
      val newPurchase = Purchase(purchase._id, purchase.bookedDate, purchase.interestDate, 
                                 purchase.textcode, purchase.description, purchase.amount, 
                                 purchase.archiveref, purchase.account, expDet)
      Purchase.updatePurchase(newPurchase.copy(_id = newPurchase._id))
    })
  }

  def getExpenseDetail(expDetId : ObjectId) = {
    collection.findOne(MongoDBObject("_id" -> expDetId)) match {
    	case Some(ed) => Some(convertFromMongoObject(ed))
    	case None => None
    }
  }

  def addExpenseDetail(expDet : ExpenseDetail) {
    collection.insert(convertToMongoObject(expDet))
  }

  def updateExpenseDetail(expDet : ExpenseDetail) {
    updatePurchaseByExpDetId(expDet._id, Some(expDet))
    collection.save(convertToMongoObject(expDet.copy(_id = expDet._id))) 
  }
  
  private def createExpenseDetails(dbObjects: List[DBObject] ) = {
    dbObjects.map( dbO => convertFromMongoObject(dbO))
  }
  
  def convertFromMongoObject(dbObject: DBObject) : ExpenseDetail = {
    val expType = dbObject.getAs[DBObject](EXPTYPE)  match {
      case Some(et) => Some(ExpenseType.convertFromMongoObject(et))
      case None => None
    }
    ExpenseDetail(
      dbObject.getAsOrElse[ObjectId](ID, mongoFail), 
      dbObject.getAsOrElse[String](DESCRIPTION, mongoFail), 
      dbObject.getAsOrElse[List[String]](TAGS, mongoFail), 
      expType
    )
  }

  def convertToMongoObject(expDetail: ExpenseDetail): DBObject = {
	val builder = MongoDBObject.newBuilder
    builder += ID -> expDetail._id
    builder += DESCRIPTION  -> expDetail.description 
    builder += TAGS   -> expDetail.searchTags
    expDetail.expenseType match {
      case Some(et) => builder += EXPTYPE  -> ExpenseType.convertToMongoObject(et)
      case None =>
    }
    builder.result()
  }
}
