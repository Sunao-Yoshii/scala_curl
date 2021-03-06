import helper.WithServer
import java.io.File
import net.azalea.curl.HTTP
import org.apache.http.entity.mime.content.ContentBody
import org.apache.http.entity.StringEntity
import org.scalatra.ScalatraServlet
import org.scalatra.servlet.{MultipartConfig, FileUploadSupport}
import org.specs2.mutable.Specification
import scala.io.Source

class PostServerServlet extends ScalatraServlet with FileUploadSupport {
  post("/") {
    "Hello POST!"
  }

  post("/reflect") {
    request.body
  }

  post("/form") {
    params.map(v => s"${v._1} : ${v._2}").mkString("\n")
  }

  post("/multipart") {
    val string = params.toSeq.sortBy(_._1).map(v => s"${v._1} : ${v._2}").mkString("\n")
    val file   = fileParams("file")
    string + "\n" + s"${file.getFieldName} : ${file.getName}"
  }
}

class HTTPPostSpec extends Specification {
  sequential

  class SimpleMockServer extends WithServer {
    def servlet = classOf[PostServerServlet]

    holder.getRegistration.setMultipartConfig(
      MultipartConfig(
        maxFileSize = Some(3*1024*1024),
        fileSizeThreshold = Some(1*1024*1024)
      ).toMultipartConfigElement
    )
  }

  "post" should {
    "can access remote with POST" in new SimpleMockServer {
      val respond = HTTP.post("http://localhost:9100/", new StringEntity("SampleMessage"))
      respond.status mustEqual 200
      respond.bodyAsString() mustEqual "Hello POST!"
    }

    "can send entity body" in new SimpleMockServer {
      val respond = HTTP.post("http://localhost:9100/reflect", new StringEntity("SampleMessage"))
      respond.status mustEqual 200
      respond.bodyAsString() mustEqual "SampleMessage"
    }
  }

  val file = new File(getClass.getResource("sample.txt").getPath())
  val fileText = {
    val source = Source.fromFile(file)
    val all    = source.getLines().mkString("\n")
    source.close()
    all
  }

  "Using HTTPHelper DSL" should {
    import net.azalea.curl.HTTPHelper._

    """String content (using toEntity)""" in new SimpleMockServer {
      val respond = HTTP.post("http://localhost:9100/reflect", "SampleMessage".toBody())
      respond.status mustEqual 200
      respond.bodyAsString() mustEqual "SampleMessage"
    }

    """String content (implicit conversion)""" in new SimpleMockServer {
      val respond = HTTP.post("http://localhost:9100/reflect", "SampleMessage")
      respond.status mustEqual 200
      respond.bodyAsString() mustEqual "SampleMessage"
    }

    """File content""" in new SimpleMockServer {
      val respond = HTTP.post("http://localhost:9100/reflect", file.toEntity())
      respond.status mustEqual 200
      respond.bodyAsString() mustEqual fileText
    }

    "form content" in new SimpleMockServer {
      val respond = HTTP.post("http://localhost:9100/form", Map(
        "param1" -> "value1",
        "param2" -> "value2"
      ))
      respond.status mustEqual 200
      respond.bodyAsString() mustEqual "param1 : value1\nparam2 : value2"
    }

    "multipart" in new SimpleMockServer {
      val respond = HTTP.post("http://localhost:9100/multipart", Map[String, ContentBody](
        "param1" -> "value1".toField(),
        "param2" -> "value2".toField(),
        "file" -> file.toField()
      ))
      respond.status mustEqual 200
      respond.bodyAsString() mustEqual "param1 : value1\nparam2 : value2\nfile : sample.txt"
    }
  }
}
