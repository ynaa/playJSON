package db

import org.joda.time.Interval
import java.util.Date
import play.Play
import com.mongodb.casbah.Imports._
import org.joda.time.DateTime
import ynaa.jsontest.domain._
import com.google.inject._
import com.mongodb.casbah.commons.MongoDBObject

@Singleton
class MongoDb extends MyEconomyDbApi {

  val dbName = Play.application().configuration().getString("mongodb.default.db")
  val mongoURI = Play.application().configuration().getString("mongodb.uri")
  val uri = MongoClientURI(mongoURI)
  val mDb = MongoClient(uri)
  val mongoDB = mDb(dbName)

  val mongoDB1 = MongoClient()(dbName)

  override def getExpenseType(expTypeId: ObjectId) = {
    ExpenseType.getExpenseType(expTypeId)
  }

  override def getExpenseTypes() = {
    ExpenseType.getExpenseTypes
  }

  override def deleteExpenseType(expTypeId: ObjectId) {
    ExpenseType.deleteExpenseType(expTypeId)
  }

  override def addExpenseType(expType: ExpenseType) {
    ExpenseType.addExpenseType(expType)
  }

  override def updateExpenseType(expType: ExpenseType) {
    ExpenseType.updateExpenseType(expType)
  }

  override def getExpenseDetails = {
    ExpenseDetail.getExpenseDetails
  }

  override def getExpenseDetailsByExpTypeId(expTypeId: ObjectId) = {
    ExpenseDetail.getExpenseDetailsByExpTypeId(expTypeId)
  }

  override def deleteExpenseDetail(expDetId: ObjectId) {
    ExpenseDetail.deleteExpenseDetail(expDetId)
  }

  override def getExpenseDetail(expDetId: ObjectId) = {
    ExpenseDetail.getExpenseDetail(expDetId)
  }

  override def addExpenseDetail(expDet: ExpenseDetail) {
    ExpenseDetail.addExpenseDetail(expDet)
  }

  override def updateExpenseDetail(expDet: ExpenseDetail) {
    ExpenseDetail.updateExpenseDetail(expDet)
  }

  override def getPurchase(pId: ObjectId): Option[Purchase] = {
    Purchase.getPurchase(pId)
  }

  override def updatePurchase(newPurchase: Purchase) = {
    Purchase.updatePurchase(newPurchase)
  }

  override def deletePurchase(purchaseId: ObjectId) {
    Purchase.deletePurchase(purchaseId)
  }

  override def addPurchase(newPurchase: Purchase) {
    if (!Purchase.exists(newPurchase)) {
      Purchase.addPurchase(newPurchase)
    }
  }

  override def getPurchases(page: Int = 0, orderBy: Int = 1,
    expTypeId: Option[ObjectId] = None,
    expDetId: String = "",
    start: DateTime = null,
    slutt: DateTime = null): Page[Purchase] = {
    Purchase.getPurchases(page, orderBy, expTypeId, expDetId, start, slutt)
  }

  override def getPurchasesByExpenseTypeAndDate(expType: ExpenseType, ObjectIderval: Interval): List[Purchase] = {
    Purchase.getPurchasesByExpenseTypeAndDate(expType: ExpenseType, ObjectIderval)
  }

  override def getPurhcaseByExpDetId(expDetId: Option[ObjectId]) = Purchase.getPurhcaseByExpDetId(expDetId)

  override def getFirstDate = new DateTime(Purchase.getFirstDate)
  override def getLastDate = new DateTime(Purchase.getLastDate)
}