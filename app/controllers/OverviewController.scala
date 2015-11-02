package controllers

import db._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import java.util.Date
import org.joda.time._
import domain._
import play.api.libs.json._
import helpers.Writes._
import com.google.inject._

@Singleton
class OverviewController @Inject()(db: MyEconomyDbApi)  extends Controller {

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
    val yearIntervals = for (year <- 0 to antYears)
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
    val result = sumByExpenseType(db, interval, getNumberOfMontsInInterval(interval) > 1)

    Ok(Json.toJson(Json.obj("result" -> result)))    
  }

  def getYearInterval(year: Int) = Action {
    val today = new DateTime()
    val numOfMonths = year match {
      case x if x == today.getYear => today.getMonthOfYear
      case _ => 12
    }
    val thisYearInterval =
      for(month <- 1 to numOfMonths) yield intervalFromStartOfMonthToStartOfNextMonth(
        new DateTime(year, month, 1, 0, 0), new DateTime(year, month, 1, 0, 0))

    Ok(Json.toJson(Json.obj("result" -> Json.toJson(thisYearInterval))))
  }
}