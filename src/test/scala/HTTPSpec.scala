import helper._
import javax.servlet.http.HttpServlet
import net.azalea.curl.HTTP
import org.specs2.mutable.Specification

class HTTPSpecGet extends Specification {
  class SimpleGetBefore extends WithServer {
    def servlet = classOf[SimpleGetServlet]
  }

  "get" should {
    "拾って来れる" in new SimpleGetBefore {
      val respond = HTTP.get("http://localhost:9100/")
      respond.status mustEqual 200
      respond.bodyAsString() mustEqual "Hello world!"
    }
  }
}
