//package controllers

import db._
import java.text.SimpleDateFormat
import org.joda.time._
import domain._

import com.typesafe.config._
import collection.JavaConversions._


package object controllers {

  val convertToDate = (dateString : String) => {
    if (dateString == "") {
      null
    } else {
      val dateFormat = new SimpleDateFormat("dd.MM.yyyy")
      val date = dateFormat.parse(dateString)
      new DateTime(date)
    }
  }

  def firstDateOfYear(year : Int) = new DateTime(year, 1, 1, 0, 0)

  def lastDateOfYear(year : Int) = {
    val today = new DateTime()
    if(year == today.getYear) {
      new DateTime(year, today.getMonthOfYear + 1, 1, 0, 0).minusDays(1)
    }
    else {
      new DateTime(year, 12, 31, 0, 0)
    }
  }

  def createDateTime(dateString: String) = {
    new DateTime(new BigDecimal(new java.math.BigDecimal(dateString)).toLong)
  }

  def findSumByExpenseTypeForIntervals(intervals : List[Interval], dben : MyEconomyDbApi, snitt : Boolean = false) = {
    Map(intervals.map(
      int => (int ->
        sumByExpenseType(dben, int, snitt))).map { a => a._1 -> a._2 } : _*)
  }

  def sumByExpenseType(dben : MyEconomyDbApi, interval : Interval, average : Boolean) = {
    val expenses = dben.getExpenseTypes
    Map(expenses.map(et => {
      val purchases = dben.getPurchasesByExpenseTypeAndDate(et, interval)
      val numOfMonths = getNumberOfMontsInInterval(interval)
      val sum = purchases.foldLeft(0.0)((tempSum, p) => p.amount + tempSum)
      average match {
        case true => (et.typeName -> (sum / numOfMonths).toInt)
        case false => (et.typeName -> sum.toInt)
      }
    }).map { a => a._1 -> a._2 } : _*)
  }

   def intervalFromStartOfMonthToStartOfNextMonth(startDate : DateTime, endDate : DateTime) = {
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

   def createInterval(date : DateTime) = {
    val startOfMonth = date.dayOfMonth().withMinimumValue.withTimeAtStartOfDay
    val endOfMonth = date.plusMonths(1).dayOfMonth().withMinimumValue.withTimeAtStartOfDay
    new Interval(startOfMonth, endOfMonth)
  }

  def getNumberOfMontsInInterval(interval : Interval) = {
    val period = new Period(interval.getStart, interval.getEnd)
    period.getYears * 12 + period.getMonths
  }

  def getNameOfInterval(int : Interval) = {
    int.getStart.year.getAsText + " - " + int.getStart.monthOfYear.getAsText
  }


  lazy val config = ConfigFactory.load("myEconomyApp")

  def read(configKey: String) = config.getString(configKey)

  def readAsList(configKey: String): List[String] = {
    config.getStringList(configKey).toList
  }
}