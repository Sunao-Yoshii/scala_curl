package net.azalea.curl

import org.apache.http.impl.client.{DefaultRedirectStrategy, HttpClients}
import org.apache.http.client.methods._
import org.apache.http.protocol.HTTP._
import org.apache.http._
import org.apache.commons.io.IOUtils
import scala.collection.JavaConversions._
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import java.nio.charset.Charset
import org.apache.http.client.{CredentialsProvider, RedirectStrategy}
import org.apache.http.client.protocol.HttpClientContext

case class Response(response: HttpResponse) {
  private lazy val statusLine = response.getStatusLine

  lazy val headers:Map[String, String] = response.getAllHeaders.map(h => h.getName -> h.getValue).toMap

  private lazy val body = response.getEntity

  def status = statusLine.getStatusCode

  def reasonPhase = statusLine.getReasonPhrase

  def protocolVersion = statusLine.getProtocolVersion.getProtocol

  lazy val bodyAsBytes = IOUtils.toByteArray(body.getContent)

  def bodyAsString(encoding: String = "UTF-8") = new String(bodyAsBytes, encoding)
}

case class RequestOption(protocolVersion: ProtocolVersion = HttpVersion.HTTP_1_1,
                         charset: String = UTF_8,
                         acceptEncoding: String = "gzip, deflate",
                         acceptLanguage: String = "ja, en",
                         agent: String = HTTP.DEFAULT_AGENT,
                         customHeaders: Map[String, String] = Map.empty,
                         redirectStrategy: RedirectStrategy = DefaultRedirectStrategy.INSTANCE,
                         credentials: Option[CredentialsProvider] = None) {
  
  def inject(request: HttpRequestBase) {
    request.setProtocolVersion(protocolVersion)
    request.addHeader("Accept-Encoding", acceptEncoding)
    request.addHeader("Accept-Language", acceptLanguage)
    request.addHeader("User-Agent", agent)
    customHeaders.map {
      case (k, v) => request.addHeader(k, v)
    }
  }
  
  lazy val encoding = Charset.forName(charset)
}

object HTTP {
  val DEFAULT_AGENT = "User-Agent: ScalaCurl/0.1"

  val options = RequestOption()

  private def withClient(request: HttpRequestBase, option: RequestOption) = {
    val client = HttpClients.custom()
      .setRedirectStrategy(option.redirectStrategy)
      .build()
    val httpContext = HttpClientContext.create()
    option.credentials.foreach(provider => {
      httpContext.setCredentialsProvider(provider)
    })
    client.execute(request)
  }

  /**
   * GET command.
   */
  def get(url: String)(implicit requestOption: RequestOption = options): Response = {
    val setting = new HttpGet(url)
    requestOption.inject(setting)
    Response(withClient(setting, requestOption))
  }

  /**
   * DELETE command.
   * @param url
   * @param requestOption
   * @return
   */
  def delete(url:String)(implicit requestOption: RequestOption = options): Response = {
    val setting = new HttpDelete(url)
    requestOption.inject(setting)
    Response(withClient(setting, requestOption))
  }

  /**
   * PUT Command.
   * @param url
   * @param entity
   * @param requestOption
   * @return
   */
  def put(url:String, entity: HttpEntity)(implicit requestOption: RequestOption = options): Response = {
    val setting = new HttpPut(url)
    requestOption.inject(setting)
    setting.setEntity(entity)
    Response(withClient(setting, requestOption))
  }

  /**
   * POST Command.
   * @param url
   * @param entity
   * @param requestOption
   * @return
   */
  def post(url:String, entity: HttpEntity)(implicit requestOption: RequestOption = options): Response = {
    val setting = new HttpPost(url)
    requestOption.inject(setting)
    setting.setEntity(entity)
    Response(withClient(setting, requestOption))
  }
}
