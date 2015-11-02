package controllers

import db._
import play.api.libs.json._
import play.api._
import play.api.mvc._
import play.api.libs.json.JsString
import domain.ExpenseType
import com.mongodb.casbah.Imports._
import helpers._
import helpers.Writes._
import com.google.inject._

@Singleton
class ExpenseTypesController @Inject()(db: MyEconomyDbApi)  extends Controller {

  def list = Action {
    val expTypes = db.getExpenseTypes
    val map = expTypes.map{ et => Json.toJson(et)}

    Ok(Json.toJson(Json.obj("expTypesList" -> map)))
  }

  def edit(expTypeId : String) = Action { request =>
    val expType = db.getExpenseType(new ObjectId(expTypeId))
    expType match {
      case Some(et) => {
        val expTypeName = getRequestParameter(request, "typeName")
        val editedExpType = ExpenseType(et._id, expTypeName)
        db.updateExpenseType(editedExpType)
        Ok("Ok")
      }
      case None => InternalServerError("Oops");
    }
  }
  
  def delete(expTypeId : String) = Action {
    db.deleteExpenseType(new ObjectId(expTypeId))
    Ok("OK")
  }

  def add = Action { request =>
    val paramValue = getRequestParameter(request, "typeName")
    val expType = ExpenseType(new ObjectId, paramValue)
    db.addExpenseType(expType)
    Ok("OK")
  }

  private def getRequestParameter(request : Request[AnyContent], paramName : String) : String = {
    val paramVal = request.body.asJson.get \ paramName
    paramVal match {
      case s : JsString => s.value
      case _ => ""
    }
  }
}