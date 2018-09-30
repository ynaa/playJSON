package controllers

import helpers.Writes._
import db._
import play.api.libs.json._
import play.api._
import play.api.mvc._
import play.api.libs.json.JsString
import domain._
import com.mongodb.casbah.Imports._

import com.google.inject._

@Singleton
class  ExpenseDetailsController @Inject()(db: MyEconomyDbApi) extends Controller {

  //val db: MyEconomyDbApi = MongoDBSetup.dbApi

  def list(expTypeId : String = null, page : Int = 0) = Action {
    val expenseDetails = expTypeId match {
      case null => db.getExpenseDetails
      case _ => db.getExpenseDetailsByExpTypeId(new ObjectId(expTypeId))
    }
    val expenseDetailsAsJson = expenseDetails.map{ ed => Json.toJson(ed) }
    val expTypes = db.getExpenseTypes.map{ et => Json.toJson(et) }
    val result = Map("expDetList" -> expenseDetailsAsJson, "expTypesList" -> expTypes)
    Ok(Json.toJson(Json.obj("result" -> result)))
  }
  
  def add = Action { request =>
    val name = getRequestParameter(request, "detName")
    val tags = getRequestParameter(request, "detTags").split(",").toList.map(s => s.trim)
    val expTypeId = getRequestParameter(request, "expType")
    val expType = db.getExpenseType(new ObjectId(expTypeId))
    val newExpDet = ExpenseDetail(new ObjectId(), name, tags, expType)
    db.addExpenseDetail(newExpDet)
    Ok("OK")
  }
  
  def addHappening = Action { request =>
    val h = parseRequest(request, "happening")
    handle(h)
    Ok("OK")
  }
  
  def handle(h: Happening) {
    val tags = List(h.name) :: Nil
    val expType = db.getExpenseType(new ObjectId(h.expType))
    val newExpDet = ExpenseDetail(new ObjectId(), h.name, List(h.name.toUpperCase), expType)
    db.addExpenseDetail(newExpDet)
    h.purchases
      .map(pid => {
        db.getPurchase(new ObjectId(pid)) match {
          case Some(p) => Some(Purchase(p._id, p.bookedDate, p.interestDate, p.textcode, p.description, p.amount, p.archiveref, p.account, Some(newExpDet)))
          case None => None
        }
      })
      .map(optP => optP match {
        case Some(p) => db.updatePurchase(p)
        case None => None
      })
  }

  def edit(expDetId : String) = Action { request =>
    val expDetail = db.getExpenseDetail(new ObjectId(expDetId))
    expDetail match {
      case Some(ed) => {
        request.body.asJson.get.validate[ExpenseDetail] match {
          case s : JsSuccess[ExpenseDetail] => {
            db.updateExpenseDetail(s.get)
            Ok("Ok")
          }
          case e : JsError => {
            Logger.error("jsError: " + e)
            InternalServerError("Oops");
          }
        }
      }
      case None => {
        Logger.error("Fant ikke")
        InternalServerError("Oops");
      }
    }
  }

  def delete(expDetId : String) = Action { request =>
    db.deleteExpenseDetail(new ObjectId(expDetId))
    Ok("Ok")
  }

  private def getRequestParameter(request : Request[AnyContent], paramName : String) : String = {
    val paramVal = request.body.asJson.get \ paramName
    paramVal match {
      case s : JsDefined => s.as[JsString].value
      case _ => ""
    }
  }

  private def parseRequest(request : Request[AnyContent], paramName : String) : Happening  =  {
    request.body.asJson.get.validate[Happening]((JsPath \ paramName).read[Happening]) match {
      case s: JsSuccess[Happening] =>  s.get
      case e: JsError => {
        println(e.errors)
        throw new RuntimeException("Error");
      }
    }
  }
}