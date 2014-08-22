package ynaa.jsontest.domain

import db._
import play.api.Play.current
import com.mongodb.casbah.Imports._


import com.novus.salat._
import com.novus.salat.dao._
import mongoContext._

case class ExpenseDetail ( 
    _id: ObjectId = new ObjectId, 
    description : String,
    searchTags : List[String],
    expenseType : Option[ExpenseType] ){
}
object ExpenseDetail extends ModelCompanion[ExpenseDetail, ObjectId] {
  val collection = MongoDBSetup.mongoDB("expensedetail")
  val dao = new SalatDAO[ExpenseDetail, ObjectId](collection = collection) {}
  
  
  def getExpenseDetails = {
    ExpenseDetail.findAll.sort(MongoDBObject("description" -> 1)).toList
  }

  def getExpenseDetailsByExpTypeId(expTypeId : ObjectId) = {
    val where = MongoDBObject("expenseType._id" -> expTypeId)
    val order = MongoDBObject("description" -> 1)
        
    ExpenseDetail.find(where).sort(order).toList
  }

  def deleteExpenseDetail(expDetId : ObjectId) = {
    updatePurchaseByExpDetId(expDetId, None)
    ExpenseDetail.remove(MongoDBObject("_id" -> expDetId))
  }
  
  def updatePurchaseByExpDetId(expDetId : ObjectId, expDet : Option[ExpenseDetail]){
    val purchases = Purchase.getPurhcaseByExpDetId(Some(expDetId))
    purchases.foreach(purchase => {
      val newPurchase = Purchase(purchase._id, purchase.bookedDate, purchase.interestDate, 
                                 purchase.textcode, purchase.description, purchase.amount, 
                                 purchase.archiveref, purchase.account, expDet)
      Purchase.save(newPurchase.copy(_id = newPurchase._id))
    })
  }

  def getExpenseDetail(expDetId : ObjectId) = {
    ExpenseDetail.findOne(MongoDBObject("_id" -> expDetId))
  }

  def addExpenseDetail(expDet : ExpenseDetail) {
    ExpenseDetail.insert(expDet)
  }

  def updateExpenseDetail(expDet : ExpenseDetail) {
    updatePurchaseByExpDetId(expDet._id, Some(expDet))
    ExpenseDetail.save(expDet.copy(_id = expDet._id)) 
  }
}
