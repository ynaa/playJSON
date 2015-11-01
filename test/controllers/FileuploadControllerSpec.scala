package controllers

import db._

import org.joda.time._
import ynaa.jsontest.domain._
import com.mongodb.casbah.Imports._

import java.text.SimpleDateFormat
import java.util.Date
import java.text.DateFormat

class FileuploadControllerSpec {
  /*extends Specification 

    "FileuploadControllerSpec" should {
        val db = new DummyDb
        val controller = new FileUploadController(db)

        "give Nil if linje contains a special word " in {
            val linje = "INNGÅENDE SALDO"
            val result = controller.filterLine(linje)
            result must be(Nil)
        }

        "not give Nil if linje does not contain a special word " in {
            val linje = "RANDOM ORD"
            val result = controller.filterLine(linje)
            result must not be(Nil)
        }

        "filter lines based on config-value" in {
            val fileContent = scala.io.Source.fromFile("/Users/ynaa/csv/2014/sparebanken-august.csv", "ISO-8859-1")
            val lines = fileContent.getLines
            lines must not be (Nil)

            val list = lines.foldLeft(List.empty[String])((tempList, l) => tempList ::: controller.filterLine(l))
            list must not be(Nil)
            for(l <- list){
                l must not contain ("faste avgifter")
                l must not contain ("Yngve Andreas Aas")
            }
            list must not be(Nil)
        }

        "match purchase-text to exp detail " in {
            val purchaseText = "Dette er en tag11 test på innhold"
            val expDetail = controller.matchExpDetailText(purchaseText, db.getExpenseDetails)

            expDetail must not be(Nil)
            expDetail must beSome
            expDetail.get.description must be ("Detail 1")

            val noExpDetail = controller.matchExpDetailText("Dette er en test på innhold som ikke matcher", db.getExpenseDetails)

            noExpDetail must not be(Nil)
            noExpDetail must beNone
        }

        "checkIfStringContainsWholeWord should work " in {
            val string = "dette er. en test,"
            val correctWords = List("dette", "er", "en", "test", "dette er", "en test");
            correctWords.foreach(word => {
                val res = controller.checkIfStringContainsWholeWord(string, word)
                res should beTrue
            })
            string must not be ("")

            val newString = "dette. er en. test,"
            val incorrectWords = List("dette er", "en test");
            incorrectWords.foreach(word => {
                val res = controller.checkIfStringContainsWholeWord(newString, word)
                res should beFalse
            })

            string must not be ("")
        }

        "createSkandia should work " in {
            val list = List("2012-01-31", "2012-02-01", "90010000", "Kreditrente", "KREDITRENTER", "2,08")
            val listen = List("2012-01-31", "2012-02-01", "90010000", "Kreditrente", "KREDITRENTER", "", "6,08")
            verifySkandiaCreation(controller.createSkandia(list, new SimpleDateFormat("yyyy-MM-dd")), -2.0)
            verifySkandiaCreation(controller.createSkandia(listen, new SimpleDateFormat("yyyy-MM-dd")), 6.0)
        }

        "createVisakreditt should work " in {
            val list = List("10.02.2010", "08.02.2010", "HI-FI KLUBBEN BERGEN", "14623" )
            val listen = List("25.02.2010", "", "Månedlig betaling kredittkort", "", "8463,95")
            val formatter = new SimpleDateFormat("dd.MM.yyyy")

            verifyVisaCreditCreation(controller.createVisakreditt(list, formatter), -14623.0, formatter.parse("10.02.2010"), formatter.parse("08.02.2010"))
            verifyVisaCreditCreation(controller.createVisakreditt(listen, formatter), 8463.0, formatter.parse("25.02.2010"), formatter.parse("25.02.2010"))
        }
    }

    def verifyVisaCreditCreation(purchase: Purchase, expectedAmount: Double, expectedBookedAmount: Date, expectedInterestDate: Date)= {
        purchase must not beNull
        val amount = purchase.amount
        amount must beEqualTo (expectedAmount)
        val bookedDate = purchase.bookedDate
        bookedDate must beEqualTo (expectedBookedAmount)
        val interestDate = purchase.interestDate
        interestDate must beEqualTo (expectedInterestDate)
    }

    def verifySkandiaCreation(purchase: Purchase, expectedAmount: Double) = {
        purchase must not beNull
        val amount = purchase.amount
        amount must beEqualTo (expectedAmount)
    }
    */
}