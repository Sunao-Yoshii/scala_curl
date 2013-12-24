package net.azalea.curl

import org.apache.http.entity.{ContentType, FileEntity, StringEntity}
import org.apache.http.client.utils.URIBuilder
import org.apache.http.message.BasicNameValuePair
import org.apache.http.entity.mime.content._
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.HttpEntity
import org.apache.http.client.entity.UrlEncodedFormEntity
import scala.collection.JavaConversions._

object HTTPHelper {
  implicit def ConvertMultipart(values: Map[String, ContentBody]):HttpEntity = {
    val builder = MultipartEntityBuilder.create()
    values.foreach {
      case (k, v) => builder.addPart(k, v)
    }
    builder.build()
  }

  implicit def ConvertForm(values: Map[String, String])(implicit options: RequestOption = HTTP.options):HttpEntity = {
    new UrlEncodedFormEntity(values.map(kv => new BasicNameValuePair(kv._1, kv._2)).toList, options.encoding)
  }

  implicit def StringToEntity(value: String) = {
    new StringEntity(value, ContentType.create("plain/text", HTTP.options.encoding))
  }

  implicit class StringConvertHelper(value: String) {
    def toBody(mimeType:String = "plain/text")(implicit options: RequestOption = HTTP.options) =
      new StringEntity(value, ContentType.create(mimeType, options.encoding))

    def toField(mimeType:String = "plain/text")(implicit options: RequestOption = HTTP.options) =
      new StringBody(value, ContentType.create(mimeType, options.encoding))
  }

  implicit class BytesConversionHelper(bytes: Array[Byte]) {
    def toField(filename: String) = new ByteArrayBody(bytes, filename)
  }

  implicit class FileConversionHelper(file: java.io.File) {
    def toEntity(contentType:ContentType = ContentType.APPLICATION_OCTET_STREAM) =
      new FileEntity(file, contentType)

    def toField(contentType:ContentType = ContentType.APPLICATION_OCTET_STREAM) =
      new FileBody(file, contentType, file.getName)
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
