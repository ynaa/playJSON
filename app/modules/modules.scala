package modules

import db._
import com.google.inject.AbstractModule
import com.google.inject.name.Names
  
class ProdModule extends AbstractModule {
  def configure() {
      bind(classOf[MyEconomyDbApi]).to(classOf[MongoDb])
  }
}

class DevModule extends AbstractModule {
  def configure() {
      bind(classOf[MyEconomyDbApi]).to(classOf[MongoDb])
  }
}

class TestModule extends AbstractModule {
  def configure() {
      bind(classOf[MyEconomyDbApi]).to(classOf[DummyDb])
  }
}