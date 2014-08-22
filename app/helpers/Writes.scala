package helpers

import ynaa.jsontest.domain._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import org.bson.types.ObjectId
import java.util.Date
import play.api.data.validation.ValidationError
import org.joda.time.Interval
import org.joda.time.DateTime
import play.api.libs.json.Json.toJsFieldJsValueWrapper

object Writes {

  implicit val jodaISODateReads : Reads[org.joda.time.DateTime] = new Reads[org.joda.time.DateTime] {
    import org.joda.time.DateTime

    val df = org.joda.time.format.ISODateTimeFormat.dateTime()

    def reads(json : JsValue) : JsResult[DateTime] = json match {
      case JsNumber(d) => JsSuccess(new DateTime(d.toLong))
      case JsString(s) => parseDate(s) match {
        case Some(d) => JsSuccess(d)
        case None => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.date.isoformat", "ISO8601"))))
      }
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.date"))))
    }

    private def parseDate(input : String) : Option[DateTime] =
      scala.util.control.Exception.allCatch[DateTime] opt (DateTime.parse(input, df))
  }

  implicit object intervalWrites extends Writes[Interval] {
    def writes(o : Interval) = {
      Json.obj(
          "name" -> JsString(o.getStart.year.getAsText + " - " + o.getStart.monthOfYear.getAsText),
          "year" -> JsString(o.getStart.year.getAsText),
          "month" -> JsString(o.getStart.monthOfYear.getAsText),
          "monthNum" -> JsString(o.getStart.getMonthOfYear.toString),
          "start" -> o.getStart,
          "end" -> o.getEnd)
    }
  }

  /**
   * Write ObjectId
   */
  implicit object objectIdWrites extends Writes[ObjectId] {
    def writes(o : ObjectId) = JsString(o.toString)
  }

  implicit object objectIdReads extends Reads[ObjectId] {
    def reads(json : JsValue) = json match {
      case JsString(s) => {
        if (ObjectId.isValid(s))
          JsSuccess(new ObjectId(s))
        else
          JsError("validate.error.objectid")
      }
      case _ => JsError("validate.error.expected.jsstring")
    }
  }

  implicit val expTypeWrites = new Writes[ExpenseType] {
    def writes(et : ExpenseType) = Json.obj(
      "_id" -> et._id.toString,
      "typeName" -> et.typeName)
  }

  implicit val expTypeReads : Reads[ExpenseType] = (
    (JsPath \ "_id").read[ObjectId] and
    (JsPath \ "typeName").read[String])(ExpenseType.apply _)

  implicit val expDetWrites = new Writes[ExpenseDetail] {
    def writes(ed : ExpenseDetail) = Json.obj(
      "_id" -> ed._id.toString,
      "description" -> ed.description,
      "searchTags" -> ed.searchTags,
      "expenseType" -> ed.expenseType)
  }

  implicit val expenseDetailReads : Reads[ExpenseDetail] = (
    (JsPath \ "_id").read[ObjectId] and
    (JsPath \ "description").read[String] and
    (JsPath \ "searchTags").read[List[String]] and
    (JsPath \ "expenseType").readNullable[ExpenseType])(ExpenseDetail.apply _)

  implicit val purchaseWrites = new Writes[Purchase] {
    def writes(purchase : Purchase) = Json.obj(
      "_id" -> purchase._id.toString,
      "bookedDate" -> purchase.bookedDate,
      "interestDate" -> purchase.interestDate,
      "textcode" -> purchase.textcode,
      "description" -> purchase.description,
      "amount" -> purchase.amount,
      "account" -> purchase.account,
      "archiveref" -> purchase.archiveref,
      "expenseDetail" -> purchase.expenseDetail)
  }

  implicit val purchaseReads : Reads[Purchase] = (
    (JsPath \ "_id").read[ObjectId] and
    (JsPath \ "bookedDate").read[Date] and
    (JsPath \ "interestDate").read[Date] and
    (JsPath \ "textcode").read[String] and
    (JsPath \ "description").read[String] and
    (JsPath \ "amount").read[Double] and
    (JsPath \ "account").read[String] and
    (JsPath \ "archiveref").read[String] and
    (JsPath \ "expenseDetail").readNullable[ExpenseDetail])(Purchase.apply _)

}