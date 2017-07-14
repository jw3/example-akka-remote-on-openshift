package example.api

import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpec}


class UnmarshallingSpec extends WordSpec with Matchers {

  val cfg = ConfigFactory.parseString(
    """
      |example.services = [
      |  {
      |    name = "b"
      |    system = "example"
      |    host = "127.0.0.1"
      |    port = 2551
      |  },
      |  {
      |    name = "c"
      |    system = "example"
      |    host = "127.0.0.1"
      |    port = 2551
      |  }
      |]
    """.stripMargin)


  "find" should {
    "locate existing" in {
      ServiceDef.named("b", cfg) should matchPattern { case Some(ServiceDef("b", _, _, _)) ⇒ }
    }

    "handle non-existing" in {
      ServiceDef.named("x", cfg) shouldBe None
    }
  }
}
