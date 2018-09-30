package db

import java.util.Date
import org.joda.time.Interval
import com.mongodb.casbah.Imports._
import org.joda.time.DateTime
import domain._
//import ynaa.myEconomy.domain.Page

trait MyEconomyDbApi {
  
  //Expense Types
  def getExpenseType(expTypeId : ObjectId) : Option[ExpenseType];

  def getExpenseTypes : List[ExpenseType];
  
  def deleteExpenseType(expTypeId : ObjectId);
  
  def addExpenseType(expType : ExpenseType);
  
  def updateExpenseType(expType : ExpenseType);

  //Expense Details
  def getExpenseDetails : List[ExpenseDetail];
  
  def getExpenseDetailsByExpTypeId(expTypeId : ObjectId) : List[ExpenseDetail];
  
  def deleteExpenseDetail(expDetId : ObjectId);
  
  def getExpenseDetail(expDetId : ObjectId) : Option[ExpenseDetail];
  
  def addExpenseDetail(expDet : ExpenseDetail);
  
  def updateExpenseDetail(expDet : ExpenseDetail);
  
  //Purchases
  def getPurchase(pId : ObjectId) : Option[Purchase];
  
  def getPurchases(page : Int = 0, orderBy : Int = 1,
    expTypeId : Option[ObjectId] = None,
    expDetId : String = "",
    start : DateTime = null,
    slutt : DateTime = null) : Page[Purchase];
  
  def getAllPurchases(start: DateTime = null, slutt: DateTime = null): List[Purchase] ;

  def updatePurchase(newPurchase : Purchase);
  
  def deletePurchase(purchaseId : ObjectId);
  
  def addPurchase(newPurchase : Purchase);
  
  def getPurchasesByExpenseTypeAndDate(expType : ExpenseType, ObjectIderval : Interval) : List[Purchase];
  
  def getPurhcaseByExpDetId(expDetId : Option[ObjectId]) : Seq[Purchase];
  
  def getFirstDate: DateTime;
  
  def getLastDate: DateTime;
}