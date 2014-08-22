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
import controllers.helper._
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

  def getYearInterval(year: Int) = Action {
    val thisYearInterval =
      for(month <- 1 to 12) yield intervalFromStartOfMonthToStartOfNextMonth(
        new DateTime(year, month, 1, 0, 0), new DateTime(year, month, 1, 0, 0))

    Ok(Json.toJson(Json.obj("result" -> Json.toJson(thisYearInterval))))
  }
  def firstDateOfYear(year : Int) = new DateTime(year, 1, 1, 0, 0)

  def lastDateOfYear(year : Int) = new DateTime(year, 12, 31, 0, 0)

  def getByInterval(startDat : String, end : String) = Action { request =>
    val startDate = createDateTime(startDat)
    val endDate = createDateTime(end)
    val interval = new Interval(startDate, endDate)
    val result = sumByExpenseType(db.getExpenseTypes, interval, getNumberOfMontsInInterval(interval) > 1)

    Ok(Json.toJson(Json.obj("result" -> result)))
  }

  def createDateTime(dateString: String) = {
    new DateTime(new BigDecimal(new java.math.BigDecimal(dateString)).toLong)
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

  def findSumByExpenseTypeForIntervals(intervals : List[Interval], expenses : List[ExpenseType], snitt : Boolean = false) = {
    Map(intervals.map(
      int => (int ->
        sumByExpenseType(expenses, int, snitt))).map { a => a._1 -> a._2 } : _*)
  }

  def sumByExpenseType(expenses : List[ExpenseType], interval : Interval, average : Boolean) = {
    Map(expenses.map(et => {
      val purchases = db.getPurchasesByExpenseTypeAndDate(et, interval)
      val numOfMonths = getNumberOfMontsInInterval(interval)
      val sum = purchases.foldLeft(0.0)((tempSum, p) => p.amount + tempSum)
      average match {
        case true => (et.typeName -> (sum / numOfMonths).toInt)
        case false => (et.typeName -> sum.toInt)
      }
    }).map { a => a._1 -> a._2 } : _*)
  }

  private def intervalFromStartOfMonthToStartOfNextMonth(startDate : DateTime, endDate : DateTime) = {
    val start = startDate.dayOfMonth().withMinimumValue.withTimeAtStartOfDay
    val end = endDate.plusMonths(1).dayOfMonth().withMinimumValue.withTimeAtStartOfDay
    new Interval(start, end)
  }

  def createIntervals(startDate : DateTime, endDate : DateTime) : List[Interval] = {
    if (!endDate.isAfter(startDate)) {
      Nil
    } else {
      createInterval(endDate) :: createIntervals(startDate, endDate.minusMonths(1))
    }
  }

  private def createInterval(date : DateTime) = {
    val startOfMonth = date.dayOfMonth().withMinimumValue.withTimeAtStartOfDay
    val endOfMonth = date.plusMonths(1).dayOfMonth().withMinimumValue.withTimeAtStartOfDay
    new Interval(startOfMonth, endOfMonth)
  }

  private def getNumberOfMontsInInterval(interval : Interval) = {
    val period = new Period(interval.getStart, interval.getEnd)
    period.getYears * 12 + period.getMonths
  }

  def getNameOfInterval(int : Interval) = {
    int.getStart.year.getAsText + " - " + int.getStart.monthOfYear.getAsText
  }
}