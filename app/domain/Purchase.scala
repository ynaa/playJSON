package domain

import db._
import java.util.Date

import play.Play
import play.api.Play.current
import com.mongodb.casbah.Imports._
import org.joda.time.Interval
import org.joda.time.DateTime

case class Purchase(
  _id: ObjectId = new ObjectId,
  bookedDate: Date,
  interestDate: Date,
  textcode: String,
  description: String,
  amount: Double,
  archiveref: String,
  account: String,
  expenseDetail: Option[ExpenseDetail]) {

}

object Purchase {
  
  val numPerPages: Int = Play.application().configuration().getInt("purchasesPerPage")
  
  val collection = MongoDBSetup.mongoDB("purchase")
  
  val ID = "_id"
  val BOOKEDDATE = "bookedDate"
  val INTERESTDATE = "interestDate"
  val TEXTCODE = "textcode"
  val DESCRIPTION = "description"
  val AMOUNT = "amount"
  val ARCHIVEREF = "archiveref"
  val ACCOUNT = "account"
  val EXPDET = "expenseDetail"


  val columns = List("dummy", "_id", "bookedDate", "description", "amount", "expenseDetail")

  def getPurchases(page: Int = 0, orderBy: Int = 1,
    expTypeId: Option[ObjectId] = None,
    expDetId: String = "",
    start: DateTime = null,
    slutt: DateTime = null): Page[Purchase] = {

    val where = createWhere(expTypeId, expDetId, start, slutt)
    val ascDesc = if (orderBy > 0) -1 else 1
    val order = MongoDBObject("bookedDate" -> ascDesc)

    val ps = createPurchases(collection.find(where).toList)
    val sum = ps.foldLeft(0.0)((tempval, p) => tempval + p.amount).toLong
    val totalRows = collection.count(where);
    val offset = numPerPages * page
    
    val purchases = createPurchases(collection.find(where).sort(order).limit(numPerPages).skip(offset).toList)
    Page(purchases, page, offset, totalRows, sum, numPerPages )
  }

  def getPurhcaseByExpDetId(expDetId: Option[ObjectId]) = {
    val where = createWhere(None, expDetId match {
      case Some(id) => id.toString
      case None => "-2"
    }, null, null)

    createPurchases(collection.find(where).toList)
  }

  private def createWhere(expTypeId: Option[ObjectId], expDetId: String, start: DateTime, slutt: DateTime) = {
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
    val startQuery = if (start == null) MongoDBObject.empty else ("bookedDate" $gt start.toDate)
    val sluttQuery = if (slutt == null) MongoDBObject.empty else ("bookedDate" $lt slutt.toDate)

    MongoDBObject("$and" -> MongoDBList(etQuery, edQuery, startQuery, sluttQuery))
  }

  def getSum(expTypeId: Option[ObjectId] = None,
    expDetId: String = "",
    start: DateTime = null,
    slutt: DateTime = null) = {

  }

  def getPurchase(purchaseId: ObjectId): Option[Purchase] = {
    collection.findOne(MongoDBObject("_id" -> purchaseId)) match {
    	case Some(p) => Some(convertFromMongoObject(p))
    	case None => None
    }
  }

  def updatePurchase(newPurchase: Purchase) = {
    collection.save(convertToMongoObject(newPurchase.copy(_id = newPurchase._id)))
  }

  def deletePurchase(purchaseId: ObjectId) {
    collection.remove(MongoDBObject("_id" -> purchaseId))
  }

  def addPurchase(newPurchase: Purchase) {
    collection.insert(convertToMongoObject(newPurchase))
  }

  def exists(newPurchase: Purchase) = {
//    val ed = newPurchase.expenseDetail match {
//      case Some(ed) => ed._id
//      case None => ""
//    }
    val query = MongoDBObject(
      "bookedDate" -> newPurchase.bookedDate,
      "interestDate" -> newPurchase.interestDate,
      "textcode" -> newPurchase.textcode,
      "description" -> newPurchase.description,
      "amount" -> newPurchase.amount,
      "archiveref" -> newPurchase.archiveref,
      "account" -> newPurchase.account)
    val result = collection.find(query).toList
    !result.isEmpty
  }

  def getPurchasesByExpenseTypeAndDate(expType: ExpenseType, interval: Interval): List[Purchase] = {
    val where = createWhere(Some(expType._id), "", interval.getStart, interval.getEnd)
    createPurchases(collection.find(where).toList)
  }

  def getFirstDate =
    createPurchases(collection.find.toList).minBy(p => p.bookedDate).bookedDate

  def getLastDate = 
    createPurchases(collection.find.toList).maxBy(p => p.bookedDate).bookedDate
  
  private def createPurchases(dbObjects: List[DBObject] ) = {
    dbObjects.map( dbO => convertFromMongoObject(dbO))
  }
  
  private def convertFromMongoObject(dbObject: DBObject) : Purchase = {
    val expDet = dbObject.getAs[DBObject](EXPDET) match {
	  case Some(ed) => Some(ExpenseDetail.convertFromMongoObject(ed))
	  case None => None
	}
    Purchase(
      dbObject.getAsOrElse[ObjectId](ID, mongoFail), 
      dbObject.getAsOrElse[Date](BOOKEDDATE, mongoFail), 
      dbObject.getAsOrElse[Date](INTERESTDATE, mongoFail), 
      dbObject.getAsOrElse[String](TEXTCODE, mongoFail), 
      dbObject.getAsOrElse[String](DESCRIPTION, mongoFail), 
      dbObject.getAsOrElse[Double](AMOUNT, mongoFail), 
      dbObject.getAsOrElse[String](ARCHIVEREF, mongoFail), 
      dbObject.getAsOrElse[String](ACCOUNT, mongoFail), 
      expDet
    )
  }

  private def convertToMongoObject(purchase: Purchase): DBObject = {
    val builder = MongoDBObject.newBuilder
    builder += ID -> purchase._id 
    builder += BOOKEDDATE -> purchase.bookedDate  
    builder += INTERESTDATE -> purchase.interestDate  
    builder += TEXTCODE -> purchase.textcode  
    builder += DESCRIPTION -> purchase.description 
    builder += AMOUNT -> purchase.amount  
    builder += ARCHIVEREF -> purchase.archiveref  
    builder += ACCOUNT -> purchase.account
    purchase.expenseDetail match {
	  case Some(ed) => builder += EXPDET -> ExpenseDetail.convertToMongoObject(ed)
	  case None =>
	}
    builder.result()
  }
}
/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long, totalSum: Long, numPerPages: Int ) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}
