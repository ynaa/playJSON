import com.mongodb.MongoException

package object domain {
  def mongoFail = throw new MongoException("Field not found, someone messed with the DB")
}