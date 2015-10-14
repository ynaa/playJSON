package modules

import db._
import com.tzavellas.sse.guice.ScalaModule

class ProdModule extends ScalaModule {
  def configure() {
    bind[MyEconomyDbApi].to[MongoDb]
  }
}

class DevModule extends ScalaModule {
  def configure() {
    bind[MyEconomyDbApi].to[MongoDb]
  }
}

class TestModule extends ScalaModule {
  def configure() {
    bind[MyEconomyDbApi].to[DummyDb]
  }
}