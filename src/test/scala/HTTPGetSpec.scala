import helper._
import net.azalea.curl.HTTP
import org.specs2.mutable.Specification
import org.scalatra.ScalatraServlet

class GetServerServlet extends ScalatraServlet {
  get("/") {
    "Hello GET!"
  }
  
  get("/reflect") {
    request.parameters.toSeq.sortBy(_._1).map(v => s"${v._1} : ${v._2}").mkString("\n")
  }
}

class HTTPGetSpec extends Specification {
  sequential

  class SimpleMockServer extends WithServer {
    def servlet = classOf[GetServerServlet]
  }

  "get" should {
    "can access remote with GET" in new SimpleMockServer {
      val respond = HTTP.get("http://localhost:9100/")
      respond.status mustEqual 200
      respond.bodyAsString() mustEqual "Hello GET!"
    }

    "can send HTTP parameters" in new SimpleMockServer {
      val respond = HTTP.get("http://localhost:9100/reflect?parameter1=value&parameter2=value2")
      respond.status mustEqual 200
      respond.bodyAsString() mustEqual "parameter1 : value\nparameter2 : value2"
    }
  }
  
  "Using HTTPHelper DSL" should {
    import net.azalea.curl.HTTPHelper._

    """URI helper "http" % "localhost" % 9100 % "/reflect" params(Map) """ in new SimpleMockServer {
      val uri = "http" % "localhost" % 9100 % "/reflect" params(
        "parameter1" -> "value1",
        "parameter2" -> "value2"
        ) toString()

      uri mustEqual "http://localhost:9100/reflect?parameter1=value1&parameter2=value2"

      val respond = HTTP.get(uri.toString())
      respond.status mustEqual 200
      respond.bodyAsString() mustEqual "parameter1 : value1\nparameter2 : value2"
    }
  }
}
