package controllers


import db._
import play.api.libs.json._
import play.api._
import play.api.mvc._
import play.api.libs.json.JsString
import com.mongodb.casbah.Imports._
import ynaa.jsontest.domain._
import java.text.SimpleDateFormat
import org.joda.time.DateTime
import helpers.Writes._
import controllers.helper._

object PurchaseController extends Controller {

  val db: MyEconomyDbApi = MongoDBSetup.dbApi
  
  def list(page : Int = 0, expTypeId : Option[String] = None, expDetId : String = "", start : String = "", slutt : String = "") = Action {
    
    val exTypeObjectId = expTypeId match{
      case Some(et) => Some(new ObjectId(et))
      case None => None
    }
    val purchases = db.getPurchases(page, 0, 0, exTypeObjectId, expDetId, convertToDate(start), convertToDate(slutt))
    
    val expDets = db.getExpenseDetails.map{ ed => Json.toJson(ed) }
    val expTypes = db.getExpenseTypes.map{ et => Json.toJson(et) }
    val result = Map("purchasesList" -> purchases.map{ purchase => Json.toJson(purchase)}, "expDetList" -> expDets, "expTypesList" -> expTypes)
    
    Ok(Json.toJson(Json.obj("result" -> result)))
  }

  def edit(pId : String) = Action { request =>
    val purchase = db.getPurchase(new ObjectId(pId))
    purchase match {
      case Some(p) => {
        request.body.asJson.get.validate[Purchase] match {
          case s : JsSuccess[Purchase] => {
            db.updatePurchase(s.get)
            Ok("Ok")
          }
          case e : JsError => {
            println("jsError: " + e)
            InternalServerError("Oops");
          }
        }
      }
      case None => InternalServerError("Oops");
    }
  }
  
  def delete(pId : String) = Action {
    db.deletePurchase(new ObjectId(pId))
    Ok("OK")
  }
  
  
  def filter = Action {
    val purchases = db.getPurchases()
    val map = purchases.map{ purchase => Json.toJson(purchase)}
    Ok(Json.toJson(Json.obj("purchaseList" -> map)))
  }
}