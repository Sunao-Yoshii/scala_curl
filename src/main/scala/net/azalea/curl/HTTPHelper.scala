package net.azalea.curl

import org.apache.http.entity.{ContentType, FileEntity, StringEntity}
import org.apache.http.client.utils.URIBuilder
import org.apache.http.Consts
import org.apache.http.message.BasicNameValuePair
import org.apache.http.entity.mime.content._
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.HttpEntity
import java.nio.charset.Charset

object HTTPHelper {
  implicit class MapConversion(values: Map[String, ContentBody]) {
    def toEntity():HttpEntity = {
      val builder = MultipartEntityBuilder.create()
      values.foreach {
        case (k, v) => builder.addPart(k, v)
      }
      builder.build()
    }
  }

  implicit def entityConversion(value: String) = new {
    def toEntity(mimeType:String = "plain/text")(implicit options: RequestOption = HTTP.options) =
      new StringEntity(value, ContentType.create(mimeType, options.encoding))
  }

  implicit def entityConversion(file: java.io.File) = new {
    def toEntity() = new FileEntity(file)
  }

  implicit class UriSchema(protocol: String) {
    val builder = new URIBuilder()
    builder.setScheme(protocol)

    def %(host: String) = {
      new UriHost(this, host)
    }
  }

  class UriHost(schema: UriSchema, host:String) {
    lazy val builder = schema.builder.setHost(host)

    def %(port: Int) = {
      builder.setPort(port)
      this
    }

    def %(path: String) = {
      new UriPath(this, path)
    }

    override def toString() = builder.build().toString
  }

  class UriPath(host:UriHost, path:String) {
    import scala.collection.JavaConversions._
    lazy val builder = host.builder.setPath(path)

    override def toString() = builder.build().toString

    def params(params: (String, String)*):UriPath = {
      this.params(params.toMap)
    }

    def params(params: Map[String, String]):UriPath = {
      val parameter = params.map {
        case (k, v) => new BasicNameValuePair(k, v)
      }
      builder.addParameters(parameter.toList)
      this
    }
  }
}
