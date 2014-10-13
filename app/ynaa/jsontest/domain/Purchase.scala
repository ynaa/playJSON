package ynaa.jsontest.domain

import db._
import java.util.Date

import play.api.Play.current
import com.mongodb.casbah.Imports._
import org.joda.time.Interval
import org.joda.time.DateTime


import com.novus.salat._
import com.novus.salat.dao._
import mongoContext._

case class Purchase(
  _id : ObjectId = new ObjectId,
  bookedDate : Date,
  interestDate : Date,
  textcode : String,
  description : String,
  amount : Double,
  archiveref : String,
  account : String,
  expenseDetail : Option[ExpenseDetail]) {

}

object Purchase extends ModelCompanion[Purchase, ObjectId] {
  val collection = MongoDBSetup.mongoDB("purchase")
  val dao = new SalatDAO[Purchase, ObjectId](collection = collection) {}

  val columns = List("dummy", "_id", "bookedDate", "description", "amount", "expenseDetail")

  def getPurchases(page : Int = 0, pageSize : Int = 10, orderBy : Int = 1,
    expTypeId : Option[ObjectId] = None,
    expDetId : String = "",
    start : DateTime = null,
    slutt : DateTime = null) : Page[Purchase] = {
   
    val where = createWhere(expTypeId, expDetId, start, slutt)
    val ascDesc = if (orderBy > 0) -1 else 1
    val order = MongoDBObject("bookedDate" -> ascDesc)

    val sum = find(where).toSeq.foldLeft(0.0)((tempval, p) => tempval + p.amount).toLong
    val totalRows = count(where);
    val offset = pageSize * page
    val purchases = find(where).sort(order).limit(pageSize).skip(offset).toSeq
    Page(purchases, page, offset, totalRows, sum)
  }
  
  def getPurhcaseByExpDetId(expDetId : Option[ObjectId]) = {
    val where = createWhere(None, expDetId match {
      case Some(id) => id.toString
      case None => ""
    }, null, null)

    find(where).toSeq
  }
  
  private def createWhere(expTypeId : Option[ObjectId], expDetId : String, start : DateTime, slutt : DateTime) = {
    val etQuery = expTypeId match {
      case Some(et) => MongoDBObject("expenseDetail.expenseType._id" -> expTypeId)
      case None => MongoDBObject.empty
    }
    val edQuery = expDetId match {
      case "" => MongoDBObject.empty
      case "-2" =>
        val q = "expenseDetail._id" $exists false
        q
      case _ => MongoDBObject("expenseDetail._id" -> new ObjectId(expDetId))
    }
    val startQuery = if(start == null) MongoDBObject.empty else ("bookedDate" $gt start.toDate)
    val sluttQuery = if(slutt == null) MongoDBObject.empty else ("bookedDate" $lt slutt.toDate)
    
    MongoDBObject("$and" -> MongoDBList(etQuery, edQuery, startQuery, sluttQuery))
  }
  
  def getSum(expTypeId : Option[ObjectId] = None,
    expDetId : String = "",
    start : DateTime = null,
    slutt : DateTime = null) = {
    
  }
  
  def getPurchase(purchaseId : ObjectId) : Option[Purchase] = {
    Purchase.findOne(MongoDBObject("_id" -> purchaseId))
  }

  def updatePurchase(newPurchase : Purchase) = {
    Purchase.save(newPurchase.copy(_id = newPurchase._id))
  }

  def deletePurchase(purchaseId : ObjectId) {
    Purchase.remove(MongoDBObject("_id" -> purchaseId))
  }

  def addPurchase(newPurchase : Purchase) {
    Purchase.insert(newPurchase)
  }
  
  def exists(newPurchase: Purchase) = {
     val query = MongoDBObject(
      "bookedDate" -> newPurchase.bookedDate,
      "interestDate" -> newPurchase.interestDate,
      "textcode" -> newPurchase.textcode,
      "description" -> newPurchase.description,
      "amount" -> newPurchase.amount,
      "archiveref" -> newPurchase.archiveref,
      "account" -> newPurchase.account,
      "expenseDetail" -> newPurchase.expenseDetail)
    val result = find(query).toList
    !result.isEmpty
  }

  def getPurchasesByExpenseTypeAndDate(expType : ExpenseType, interval : Interval) : List[Purchase] = {
    val where = createWhere(Some(expType._id), "", interval.getStart, interval.getEnd)
    find(where).toList
  }
  
  def getFirstDate = {
    findAll().toSeq.minBy(p => p.bookedDate).bookedDate
  }
  def getLastDate = {
    findAll().toSeq.maxBy(p => p.bookedDate).bookedDate
  }
}
/**
 * Helper for pagination.
 */
case class Page[A](items : Seq[A], page : Int, offset : Long, total : Long, totalSum : Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}
