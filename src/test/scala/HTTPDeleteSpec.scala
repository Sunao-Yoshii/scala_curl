import helper.WithServer
import net.azalea.curl.HTTP
import org.scalatra.ScalatraServlet
import org.specs2.mutable.Specification

class DeleteServerServlet extends ScalatraServlet {
  delete("/") {
    "Hello DELETE!"
  }
}

class HTTPDeleteSpec extends Specification {
  sequential

  class SimpleMockServer extends WithServer {
    def servlet = classOf[DeleteServerServlet]
  }

  "delete" should {
    "can access remote with DELETE" in new SimpleMockServer {
      val respond = HTTP.delete("http://localhost:9100/")
      respond.status mustEqual 200
      respond.bodyAsString() mustEqual "Hello DELETE!"
    }
  }
}
