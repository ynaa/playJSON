package controllers

import java.text.SimpleDateFormat
import org.joda.time.DateTime

package object helper {
  val convertToDate = (dateString : String) => {
    if (dateString == "") {
      null
    } else {
      val dateFormat = new SimpleDateFormat("dd.MM.yyyy")
      val date = dateFormat.parse(dateString)
      new DateTime(date)
    }
  }
}