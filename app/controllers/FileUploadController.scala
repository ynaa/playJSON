package controllers

import db._
import play.api.libs.json._
import play.api._
import play.api.mvc._
import play.api.libs.json.JsString
import domain._
import com.mongodb.casbah.Imports._
import helpers._
import helpers.Writes._
import java.text.SimpleDateFormat
import java.util.Date
import java.text.DateFormat
import org.bson.types.ObjectId
import com.google.inject._
import scala.util.matching.Regex

@Singleton
class FileUploadController @Inject()(db: MyEconomyDbApi)  extends Controller {

  def update = Action {
    val expenseDetails = db.getExpenseDetails
    val purchaseList = db.getPurhcaseByExpDetId(None)
    val purchasesToUpdate = findAndUpdatePurchase(purchaseList.toList, expenseDetails.toList)
    purchasesToUpdate.foreach(p => db.updatePurchase(p))
    Ok("Ok")
  }

  def findAndUpdatePurchase(purchaseList : List[Purchase], expenseDetails : List[ExpenseDetail]) = {
    val purchases = purchaseList.map(purchase => {
      val tt = matchExpDetailText(purchase.description, expenseDetails)
      tt match {
        case Some(ed) =>
          createPurchase(purchase, tt)
        case None => null
      }
    })
    purchases.filter(p => p != null)
  }

  def upload = Action(parse.multipartFormData) { request =>
    val requestMap = request.body.asFormUrlEncoded
    val bank = convertToString(requestMap.get("bank"))
    request.body.file("uploadedFile").map { file =>
      import java.io.File
      val fileContent = scala.io.Source.fromFile(file.ref.file, "UTF-8")
      val lines = fileContent.getLines()
      val list = lines.foldLeft(List.empty[String])((tempList, l) => tempList ::: filterLine(l))
      val purchases = createPurchases(list, bank)
      purchases.foreach(p => db.addPurchase(p))
      Ok("Ok")
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
        "error" -> "Missing file")
    }
  }

  private def convertToString(string : Option[Seq[String]]) = {
    string match {
      case Some(s) => s.foldLeft("")((tempString, str) => tempString + str).trim
      case None => ""
    }
  }

  def lineContainsWordInList(line: String, wordList: List[String]) = {
    !wordList.filter(word => line.toUpperCase.contains(word.toUpperCase)).isEmpty
  }

  def filterLine(line : String) = {
    val linje = line.replaceAll("\"", "")
    if (lineContainsWordInList(linje, readAsList("exluded-lines"))) {
      Nil
    } else if(isFromSavingsAccount(linje)){
      Nil
    } else if (!(linje.length == 0 || linje.startsWith("BOKFØRINGSDATO") ||
      linje.startsWith("Bok") || linje.startsWith("Bokført") ||
      linje.contains("UTGÅENDE SALDO"))) {
      linje :: Nil
    } 
    else {
      Nil
    }
  }
  
  def isFromSavingsAccount(linje: String) = {
    val savingsAccounts = readAsList("sparekontoer")
    val splittedLine = linje.split(";")
    val fromAccount = splittedLine.length == 8 match {
      case true => splittedLine(6)
      case false => splittedLine(7)
    }
    savingsAccounts.contains(fromAccount) 
  }

  def createPurchases(fileAsList : List[String], bank : String) = {
    val expenseDetails = db.getExpenseDetails
    val purchases = fileAsList.map(aLine => {
      val lineItems = stringSplit(aLine)
      val purchase = createPurchase(lineItems, bank)
      if (purchase != null) {
        val savings = checkForSavings(aLine, expenseDetails)
        val tt = savings match {
          case optEd: Some[ExpenseDetail] => optEd
          case None => matchExpDetailText(purchase.description, expenseDetails) 
        }
        createPurchase(purchase, tt)
      } else {
        purchase
      }
    })
    purchases
  }
  
  def checkForSavings(line: String, expenseDetails : List[ExpenseDetail]) : Option[ExpenseDetail] = {
    val savingsAccounts = readAsList("sparekontoer")
    val splittedLine = line.split(";")
    val toAccount = splittedLine.length == 8 match {
      case true => splittedLine(7)
      case false => splittedLine(8)
    }
    savingsAccounts.contains(toAccount) match {
      case true => {
        expenseDetails.find(ed => ed.description.equals("Fast sparing"))
      }
      case false => Option.empty 
    }
  }

  def createPurchase(purchase : Purchase, expDet : Option[ExpenseDetail]) = {
    Purchase(purchase._id, purchase.bookedDate, purchase.interestDate, purchase.textcode, purchase.description,
      purchase.amount, purchase.archiveref, purchase.account, expDet)
  }

  def stringSplit(str : String) : List[String] = {
    val li : List[String] = str.split(";").toList
    li.map { x => x.trim }
  }

  def createPurchase(list : List[String], bank : String) : Purchase = {
    bank match {
      case "Sparebanken" =>
        createSPV(list, new SimpleDateFormat("dd.MM.yyyy"))
      case "Visakreditt" =>
        createVisakreditt(list, new SimpleDateFormat("dd.MM.yyyy"))
      case "Skandiabanken" =>
        createSkandia(list, new SimpleDateFormat("yyyy-MM-dd"))
      case "Mastercard" =>
        createMastercard(list, new SimpleDateFormat("dd.MM.yyyy"))
      case _ => null
    }
  }

  def matchExpDetailText(purchaseText : String, expenseDetails : List[ExpenseDetail]) : Option[ExpenseDetail] = {
    val result =
      for(expDet <- expenseDetails;
        tag <- expDet.searchTags;
        if(checkIfStringContainsWholeWord(purchaseText, tag.toUpperCase))
      ) yield Some(expDet)

    result match {
      case head :: tail => head
      case _ => None
    }
  }
  
  def checkIfStringContainsWholeWord(string : String, word : String) : Boolean = {
    string.toUpperCase.contains(word)
  }

  def createSPV(list : List[String], formatter : DateFormat) : Purchase = {
    println("Linje: " + list)
    val dd = list(0).length() == 10 match {
      case true => list(0)
      case false => list(0).substring(1)
    }
    val booked : Date = formatter.parse(dd)
    val interest : Date = formatter.parse(list(1))
    val ammount = list(4).isEmpty() match {
      case true => list(5)
      case false => list(4)
    }
    val amount = createDecimal(ammount)
    val desc = list(3)
    val text = list(2)
    Purchase(ObjectId.get, booked, interest, text, desc, amount, "", "", None)
  }

  def createMastercard(list : List[String], formatter : DateFormat) : Purchase = {
    val booked : Date = formatter.parse(list(0))
    val interest : Date = formatter.parse(list(0))
    val ammount = list(5)
    val amount = createDecimal(ammount)
    val desc = list(1)
    val text = ""
    Purchase(ObjectId.get, booked, interest, text, desc, amount, "", "", None)
  }

  def createSkandia(list : List[String], formatter : DateFormat) : Purchase = {
    val booked : Date = formatter.parse(list(0))
    val interest : Date = formatter.parse(list(1))
    val ammount = list(5) match {
      case amnt if amnt != null && amnt != "" => "-" + amnt
      case _ => list(6)
    }
    val desc = list(4)
    val text = list(3)
    Purchase(ObjectId.get, booked, interest, text, desc, createDecimal(ammount), "", "", None)
  }

  def createVisakreditt(list : List[String], formatter : DateFormat) : Purchase = {
    val booked : Date = formatter.parse(list(0))
    val interest = list(1) match {
      case intDate if intDate != null && intDate != "" => formatter.parse(intDate)
      case _ => booked
    }
    val ammount = list(3) match {
      case amnt if amnt != null && amnt != "" => "-" + amnt
      case _ => list(4)
    }
    val desc = list(2)
    val text = ""
    Purchase(ObjectId.get, booked, interest, text, desc, createDecimal(ammount), "", "", None)
  }

  def createDecimal(amount : String) : Int = {
    println(amount)
    val amm = amount.replace(" ", "")
    val index = amm.indexOf(",")
    if (index > 0)
      amm.substring(0, index).toInt
    else
      amm.toInt
  }

}
