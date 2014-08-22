package controllers

import db._
import play.api.libs.json._
import play.api._
import play.api.mvc._
import play.api.libs.json.JsString
import ynaa.jsontest.domain._
import com.mongodb.casbah.Imports._
import helpers._
import helpers.Writes._
import java.text.SimpleDateFormat
import java.util.Date
import java.text.DateFormat
import org.bson.types.ObjectId

object FileUploadController extends Controller {

  val db : MyEconomyDbApi = MongoDBSetup.dbApi

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
      val fileContent = scala.io.Source.fromFile(file.ref.file, "ISO-8859-1")
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

  private def readRequestMap(request : Request[AnyContent]) = {
    val requestMap : collection.mutable.Map[String, Seq[String]] = collection.mutable.Map()
    requestMap ++= request.body.asFormUrlEncoded.getOrElse[Map[String, Seq[String]]] { Map.empty }
    requestMap ++= request.queryString
  }

  private def convertToString(string : Option[Seq[String]]) = {
    string match {
      case Some(s) => s.foldLeft("")((tempString, str) => tempString + str).trim
      case None => ""
    }
  }

  private def filterLine(line : String) = {
    val linje = line.replaceAll("\"", "")
    if (linje.contains("INNGÅENDE SALDO")) {
      Nil
    } else if (!(linje.length == 0 || linje.startsWith("BOKFØRINGSDATO") ||
      linje.startsWith("Bok") || linje.startsWith("Bokført") ||
      linje.contains("UTGÅENDE SALDO"))) {
      linje :: Nil
    } else {
      Nil
    }
  }

  def createPurchases(fileAsList : List[String], bank : String) = {
    val expenseDetails = db.getExpenseDetails
    val details = expenseDetails.map(exp => exp.description)
    val purchases = fileAsList.map(aLine => {
      val lineItems = stringSplit(aLine)
      val purchase = createPurchase(lineItems, bank)
      if (purchase != null) {
        val tt = matchExpDetailText(purchase.description, expenseDetails)
        createPurchase(purchase, tt)
      } else {
        purchase
      }
    })
    purchases
  }

  def createPurchase(purchase : Purchase, expDet : Option[ExpenseDetail]) = {
    Purchase(purchase._id, purchase.bookedDate, purchase.interestDate, purchase.textcode, purchase.description,
      purchase.amount, purchase.archiveref, purchase.account, expDet)
  }
  def stringSplit(str : String) : List[String] = {
    val li : List[String] = str.split("\t").toList
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

    for (expDet <- expenseDetails) {
      val searchTags = expDet.searchTags
      for (tag <- searchTags) {
        val besk = tag.toUpperCase
        val result = checkIfStringContainsWholeWord(purchaseText, besk)
        if (result) {
          return Some(expDet)
        }
      }
    }
    None
  }

  def checkIfStringContainsWholeWord(string : String, word : String) : Boolean = {
    val str = string.toUpperCase
    val wrd = word.trim.toUpperCase
    if (str.equals(wrd)) {
      true
    } else if (str.contains(wrd)) {
      val indexBefore = str.indexOf(wrd)
      val indexAfter = indexBefore + wrd.length
      val lenght = str.length
      val correctBefore = (indexBefore == 0) || (indexBefore >= 0 && correctEndChar(str.charAt(indexBefore - 1)))
      val correctAfter = correctBefore && ((indexAfter == str.length) || (indexAfter < str.length && correctEndChar(str.charAt(indexAfter))))
      correctBefore && correctAfter
    } else {
      false
    }
  }

  def correctEndChar(char : Char) = {
    char == ' ' || char == '.' || char == ','
  }

  def createSPV(list : List[String], formatter : DateFormat) : Purchase = {
    val booked : Date = formatter.parse(list(0))
    val interest : Date = formatter.parse(list(1))
    val ammount = list(4)
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
    var ammount = list(5)
    if (ammount == null || ammount == "") {
      ammount = list(6)
    } else {
      ammount = "-" + list(5)
    }
    val amount = createDecimal(ammount)
    val desc = list(4)
    val text = list(3)
    Purchase(ObjectId.get, booked, interest, text, desc, amount, "", "", None)
  }

  def createVisakreditt(list : List[String], formatter : DateFormat) : Purchase = {
    val booked : Date = formatter.parse(list(0))

    val intDateString = list(1)
    var interest : Date = null
    if (intDateString == null || intDateString == "") {
      interest = booked
    } else {
      interest = formatter.parse(intDateString)
    }

    var ammount = list(3)
    if (ammount == null || ammount == "") {
      ammount = list(4)
    } else {
      ammount = "-" + list(3)
    }
    val amount = createDecimal(ammount)
    val desc = list(2)
    val text = ""
    Purchase(ObjectId.get, booked, interest, text, desc, amount, "", "", None)
  }

  def createDecimal(amount : String) : Int = {
    val amm = amount.replace(" ", "")
    val index = amm.indexOf(",")
    if (index > 0)
      amm.substring(0, index).toInt
    else
      amm.toInt
  }
}
