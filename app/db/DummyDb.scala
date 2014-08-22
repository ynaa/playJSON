package db
import ynaa.jsontest.domain._
import com.mongodb.casbah.Imports._
import java.util.Date
import java.util.Calendar
import org.joda.time.Interval
import org.joda.time.DateTime

object DummyDb extends MyEconomyDbApi {
  var expenseTypes : List[ExpenseType] = Nil

  var expenseDetails : List[ExpenseDetail] = Nil

  var purchases : List[Purchase] = Nil

  def initData {
    expenseTypes = createExpenseType("Type 1") ::
      createExpenseType("Type 2") ::
      createExpenseType("Type 3") ::
      createExpenseType("Type 4") ::
      Nil
      
    expenseDetails = createExpenseDetail("Detail 1", List("tag11", "tag12"), Some(expenseTypes(0))) ::
      createExpenseDetail("Detail 2", List("tag21", "tag22"), Some(expenseTypes(1))) ::
      createExpenseDetail("Detail 3", List("tag31", "tag32"), Some(expenseTypes(2))) ::
      createExpenseDetail("Detail 4", List("tag41", "tag42"), Some(expenseTypes(3))) ::
      Nil
      
      val date = new DateTime().monthOfYear.get - 1
      
      purchases = createPurchase(1, createDate(2012, date - 1, 1), "Purchase 1", 123.5, Some(expenseDetails(0))) ::
      createPurchase(2, createDate(2012, date - 1, 1), "Purchase 2", 223.5, Some(expenseDetails(1))) ::
      createPurchase(3, createDate(2013, date - 1, 1), "Purchase 3", 323.5, Some(expenseDetails(2))) ::
      createPurchase(4, createDate(2013, date - 1, 1), "Purchase 4", 423.5, Some(expenseDetails(3))) ::
      createPurchase(5, createDate(2013, date - 2, 1), "Purchase 5", 123.5, Some(expenseDetails(0))) ::
      createPurchase(6, createDate(2013, date - 2, 1), "Purchase 6", 223.5, Some(expenseDetails(1))) ::
      createPurchase(7, createDate(2013, date - 2, 1), "Purchase 7", 323.5, Some(expenseDetails(2))) ::
      createPurchase(8, createDate(2013, date - 3, 1), "Purchase 8", 423.5, Some(expenseDetails(3))) ::
      createPurchase(9, createDate(2014, date - 3, 1), "Purchase 9", 123.5, Some(expenseDetails(0))) ::
      createPurchase(10, createDate(2014, date - 3, 1), "Purchase 10", 223.5, Some(expenseDetails(1))) ::
      createPurchase(11, createDate(2014, date - 3, 1), "Purchase 11", 323.5, Some(expenseDetails(2))) ::
      createPurchase(12, createDate(2014, date - 4, 1), "Purchase 12", 423.5, Some(expenseDetails(3))) ::
      createPurchase(13, createDate(2014, date - 4, 1), "Purchase 13", 123.5, Some(expenseDetails(0))) ::
      createPurchase(14, createDate(2014, date - 4, 1), "Purchase 14", 223.5, Some(expenseDetails(1))) ::
      createPurchase(15, createDate(2014, date - 4, 1), "Purchase 15", 323.5, Some(expenseDetails(2))) ::
      createPurchase(16, createDate(2014, date - 4, 1), "Purchase 16", 123.5, Some(expenseDetails(3))) ::
      Nil
      
      /*
      val date = new DateTime().monthOfYear.get - 1
      purchases = createPurchase(1, createDate(2012, date - 1, 1), "Purchase 1", 123.5, Some(expenseDetails(0))) ::
      createPurchase(2, createDate(2012, date - 1, 1), "Purchase 2", 223.5, Some(expenseDetails(1))) ::
      createPurchase(3, createDate(2012, date - 1, 1), "Purchase 3", 323.5, Some(expenseDetails(2))) ::
      createPurchase(4, createDate(2012, date - 1, 1), "Purchase 4", 423.5, Some(expenseDetails(3))) ::
      createPurchase(5, createDate(2012, date - 2, 1), "Purchase 5", 123.5, Some(expenseDetails(0))) ::
      createPurchase(6, createDate(2012, date - 2, 1), "Purchase 6", 223.5, Some(expenseDetails(1))) ::
      createPurchase(7, createDate(2012, date - 2, 1), "Purchase 7", 323.5, Some(expenseDetails(2))) ::
      createPurchase(8, createDate(2012, date - 3, 1), "Purchase 8", 423.5, Some(expenseDetails(3))) ::
      createPurchase(9, createDate(2012, date - 3, 1), "Purchase 9", 123.5, Some(expenseDetails(0))) ::
      createPurchase(10, createDate(2012, date - 3, 1), "Purchase 10", 223.5, Some(expenseDetails(1))) ::
      createPurchase(11, createDate(2012, date - 3, 1), "Purchase 11", 323.5, Some(expenseDetails(2))) ::
      createPurchase(12, createDate(2012, date - 4, 1), "Purchase 12", 423.5, Some(expenseDetails(3))) ::
      createPurchase(13, createDate(2012, date - 4, 1), "Purchase 13", 123.5, Some(expenseDetails(0))) ::
      createPurchase(14, createDate(2012, date - 4, 1), "Purchase 14", 223.5, Some(expenseDetails(1))) ::
      createPurchase(15, createDate(2012, date - 4, 1), "Purchase 15", 323.5, Some(expenseDetails(2))) ::
      createPurchase(16, createDate(2012, date - 4, 1), "Purchase 16", 123.5, Some(expenseDetails(3))) ::
      Nil
      */
  }

  override def getExpenseType(expTypeId : ObjectId) = {
    expenseTypes.find(et => et._id == expTypeId)
  }

  override def getExpenseTypes() = {
    expenseTypes
  }

  override def deleteExpenseType(expTypeId : ObjectId) = {
    val expType = getExpenseType(expTypeId)
    expenseTypes = expenseTypes.filterNot(e => e._id == expTypeId)
  }

  override def addExpenseType(expType : ExpenseType) {
    expenseTypes = expType :: expenseTypes
  }

  override def updateExpenseType(expType : ExpenseType) {
    deleteExpenseType(expType._id)
    addExpenseType(expType)
  }

  override def getExpenseDetails = {
    expenseDetails
  }

  override def getExpenseDetailsByExpTypeId(expTypeId : ObjectId) = {
    expenseDetails.filter(
      ed => ed.expenseType match {
        case Some(et) => et._id == expTypeId
        case None => false
      })
  }

  override def deleteExpenseDetail(expDetId : ObjectId) = {
    expenseDetails = expenseDetails.filterNot(e => e._id == expDetId)
  }
  
  override def getExpenseDetail(expDetId : ObjectId) = {
    expenseDetails.find(ed => ed._id == expDetId)
  }

  override def addExpenseDetail(expDet : ExpenseDetail) {
    expenseDetails = expDet :: expenseDetails
  }

  override def updateExpenseDetail(expDet : ExpenseDetail) {
    deleteExpenseDetail(expDet._id)
    addExpenseDetail(expDet)
  }

  private def createExpenseType(typeName : String = "Test navn") = {
    ExpenseType(new ObjectId(), typeName)
  }

  private def createExpenseDetail(description : String,
    searchTags : List[String], expenseType : Option[ExpenseType]) = {
    ExpenseDetail(new ObjectId(), description, searchTags, expenseType)
  }
  
 private def isInInterval(interval : Interval, date : Date) = {

    val jodaDate = new DateTime(date).withTimeAtStartOfDay
    val result = interval.contains(jodaDate)
    result
  } 
//  override def getPurchases : List[Purchase] = {
//    purchases
//  }

  override def getPurchase(pId : ObjectId) : Option[Purchase] = {
    purchases.find(p => p._id == pId)
  }

  override def updatePurchase(newPurchase : Purchase) = {
    deletePurchase(newPurchase._id)
    addPurchase(newPurchase)
  }

  override def deletePurchase(purchaseId : ObjectId) {
    val purchase = getPurchase(purchaseId)
    purchases = purchases.filterNot(p => p._id == purchaseId)
  }

  override def addPurchase(newPurchase : Purchase) {
    purchases = newPurchase :: purchases
  }
  
  override def getPurchases(page : Int = 0, pageSize : Int = 10, orderBy : Int = 1, 
                            expTypeId : Option[ObjectId] = None,expDetId : String = "",
                            start : DateTime = null,slutt : DateTime = null) : List[Purchase] = {
    val result = expTypeId match {
      case Some(et) => purchases.filter(purchase => purchase.expenseDetail.get.expenseType.get._id == expTypeId.get )
      case None => purchases
    }
    result
  }
  
  override def getFirstDate = new DateTime(purchases.minBy(_.bookedDate).bookedDate)
  
  override def getLastDate = new DateTime(purchases.maxBy(_.bookedDate).bookedDate)
  
  override def getPurchasesByExpenseTypeAndDate(expType : ExpenseType, interval : Interval) : List[Purchase] = {
    val p = purchases.filter(p =>
      p.expenseDetail match {
        case Some(ed) => ed.expenseType match {
          case Some(et) =>
            et._id == expType._id && isInInterval(interval, p.bookedDate)
          case None =>
            false
        }
        case None => false
      })
      p
  }
  
  override def getPurhcaseByExpDetId(expDetId : Option[ObjectId]) = {
    expDetId match {
      case Some(id) => {
        purchases.filter(p => 
          p.expenseDetail match {
            case Some(ed) => ed._id == id
            case None => false
          })
      }
      case None => purchases
    }
  }
  
  private def createPurchase(num: Int, bookedDate : Date, description : String,
    amount : Double, expenseDetail : Option[ExpenseDetail]) = {
    Purchase(new ObjectId(), bookedDate, bookedDate, null, description, amount, null, null, expenseDetail)
  }

  def createDate(year : Int, month : Int, day : Int) = {
    val cal : Calendar = Calendar.getInstance()
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.getTime
  }
}