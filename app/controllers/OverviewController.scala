package controllers

import db._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import java.util.Date
import org.joda.time._
import ynaa.jsontest.domain._
import play.api.libs.json._
import helpers.Writes._


object OverviewController extends Controller {

  val db : MyEconomyDbApi = MongoDBSetup.dbApi

  def getIntervals = Action {
    val today = new DateTime
    val firstDate = db.getFirstDate
    val lastMonthInterval = 
      intervalFromStartOfMonthToStartOfNextMonth(today.minusMonths(1), today.minusMonths(1))
    val threeMonthsInterval = 
      intervalFromStartOfMonthToStartOfNextMonth(today.minusMonths(3), today.minusMonths(1))
    val allMonthsInterval = 
      intervalFromStartOfMonthToStartOfNextMonth(firstDate, today)

    val antYears = today.getYear - firstDate.getYear
    val yearIntervals = for (year <- 1 to antYears)
      yield intervalFromStartOfMonthToStartOfNextMonth(firstDateOfYear(today.getYear - year), lastDateOfYear(today.getYear - year))

    val result = Map("lastMonth" -> Json.toJson(lastMonthInterval),
      "threeMonths" -> Json.toJson(threeMonthsInterval),
      "allMonths" -> Json.toJson(allMonthsInterval),
      "yearIntervals" -> Json.toJson(yearIntervals))

    Ok(Json.toJson(Json.obj("result" -> result)))
  }

  def getByInterval(startDat : String, end : String) = Action { request =>
    
    val startDate = createDateTime(startDat)
    val endDate = createDateTime(end)
    val interval = new Interval(startDate, endDate)
    val result = sumByExpenseType(db.getExpenseTypes, interval, getNumberOfMontsInInterval(interval) > 1)

    Ok(Json.toJson(Json.obj("result" -> result)))
    
  }

  def show() = Action { request =>
    val expenses = db.getExpenseTypes
    val purchases = db.getPurchases()

    val firstPurchase = db.getFirstDate
    val lastPurchase = db.getLastDate

    val lastMonthInterval = intervalFromStartOfMonthToStartOfNextMonth(lastPurchase, lastPurchase)
    val threeMonthsInterval = intervalFromStartOfMonthToStartOfNextMonth(lastPurchase.minusMonths(2), lastPurchase)
    val allMonthsInterval = intervalFromStartOfMonthToStartOfNextMonth(firstPurchase, lastPurchase)
    val snittIntervals = findSumByExpenseTypeForIntervals(lastMonthInterval :: threeMonthsInterval :: allMonthsInterval :: Nil, expenses, true)
    val snittene = Map("Siden starten" -> snittIntervals(allMonthsInterval),
      "3 siste måneder" -> snittIntervals(threeMonthsInterval),
      "Siste måned" -> snittIntervals(lastMonthInterval))
    val intervals = createIntervals(firstPurchase, new DateTime)
    val itervalExpPurchaseList = findSumByExpenseTypeForIntervals(intervals.reverse, expenses)

    val jsonResult = itervalExpPurchaseList.map(iep => (getNameOfInterval(iep._1) -> iep._2))

    val result = Map("snittene" -> Json.toJson(snittene),
      "itervalExpPurchaseList" -> Json.toJson(jsonResult),
      "intervals" -> Json.toJson(intervals))

    Ok(Json.toJson(Json.obj("result" -> result)))
  }

  def getYearInterval(year: Int) = Action {
    val thisYearInterval =
      for(month <- 1 to 12) yield intervalFromStartOfMonthToStartOfNextMonth(
        new DateTime(year, month, 1, 0, 0), new DateTime(year, month, 1, 0, 0))

    Ok(Json.toJson(Json.obj("result" -> Json.toJson(thisYearInterval))))
  }

}