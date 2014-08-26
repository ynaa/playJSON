package controllers

import db._
import org.specs2.mutable._
import java.util._
import org.joda.time._
import ynaa.jsontest.domain._
import com.mongodb.casbah.Imports._

class PackageSpec extends Specification {

	"PackageSpec" should {
		"convert String To Date " in {
			val dateAsString = "12.05.1977"
			val date = convertToDate(dateAsString)

			1977 must beEqualTo(date.year().get())
			12 must beEqualTo(date.getDayOfMonth)
			5 must beEqualTo(date.getMonthOfYear)
		}

		"give 1 and 1 as first day and month" in {			
			val date = firstDateOfYear(2000)

			1 must beEqualTo(date.getDayOfMonth)
			1 must beEqualTo(date.getMonthOfYear)
		}

		"give 31 and 12 as first day and month " in {		
			val date = lastDateOfYear(2000)

			31 must beEqualTo(date.getDayOfMonth)
			12 must beEqualTo(date.getMonthOfYear)
		}
		"calculate Number Of Monts In An Interval" in {
			val first = firstDateOfYear(2000)
			val last = firstDateOfYear(2001)
			val interval = new Interval(first, last)

			val months = getNumberOfMontsInInterval(interval)

			12 must beEqualTo(months)
			
		}
		"get sums from empty db" in {
			val db = DummyDb
			db.purchases = Nil
			db.expenseTypes = Nil
			db.expenseDetails = Nil
			val interval = new Interval(firstDateOfYear(2000), firstDateOfYear(2001))
			val emptySums = sumByExpenseType(db, interval, false)
			emptySums must beEmpty

			db.expenseTypes = ExpenseType(new ObjectId(), "Type") :: Nil
			val oneSums = sumByExpenseType(db, interval, false) 
			oneSums must not be empty
			oneSums must haveKey ("Type")
			oneSums must havePairs ("Type" -> 0)
						
			
			"Hello world" must endWith("world")
		}
	}
}
