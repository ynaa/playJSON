package ynaa.jsontest.domain

import db._
import play.api.Play.current
import com.mongodb.casbah.Imports._

import com.novus.salat._
import com.novus.salat.dao._
import mongoContext._

case class ExpenseType(_id : ObjectId = new ObjectId, typeName : String) {
}

object ExpenseType extends ModelCompanion[ExpenseType, ObjectId] {
  val collection = MongoDBSetup.mongoDB("expensetype")
  val dao = new SalatDAO[ExpenseType, ObjectId](collection = collection) {}

  def getExpenseType(expTypeId : ObjectId) = {
    ExpenseType.findOne(MongoDBObject("_id" -> expTypeId))
  }
  
  def getExpenseTypes() = {
    findAll().sort(MongoDBObject("typeName" -> 1)).toList
  }
  
  def deleteExpenseType(expTypeId : ObjectId) = {
    updateExpenseDetailByExpType(expTypeId, None)
    ExpenseType.remove(MongoDBObject("_id" -> expTypeId))
  }
  
  def updateExpenseDetailByExpType(expTypeId : ObjectId, expenseType : Option[ExpenseType]){
    val expDetList = ExpenseDetail.getExpenseDetailsByExpTypeId(expTypeId)
    expDetList.foreach(expDet => {
      val newExpDet = ExpenseDetail(expDet._id, expDet.description, expDet.searchTags, expenseType)
      ExpenseDetail.save(newExpDet.copy(_id = newExpDet._id))
    })
  }
  
  def addExpenseType(expType : ExpenseType) {
    ExpenseType.insert(expType)
  }

  def updateExpenseType(expType : ExpenseType) {
    updateExpenseDetailByExpType(expType._id, Some(expType))
    ExpenseType.save(expType.copy(_id = expType._id)) 
  }


}
