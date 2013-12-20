package net.azalea.curl

import org.apache.http.impl.client.{TargetAuthenticationStrategy, HttpClients}
import org.apache.http.client.methods._
import org.apache.http.protocol.HTTP._
import org.apache.http._
import org.apache.commons.io.IOUtils

case class Response(response: HttpResponse) {
  private lazy val statusLine = response.getStatusLine

  lazy val headers = response.getAllHeaders.map(h => h.getName -> h.getValue).toMap

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
                         agent: String = HTTP.DEFAULT_AGENT) {

  def inject(request: HttpRequestBase) {
    request.setProtocolVersion(protocolVersion)
    request.setHeader("Accept-Encoding", acceptEncoding)
    request.setHeader("Accept-Language", acceptEncoding)
    request.setHeader("User-Agent", agent)
  }
}

object HTTP {
  val DEFAULT_AGENT = "User-Agent: ScalaCurl/0.1"

  val defaultConfig = RequestOption()

  private def withClient(request: HttpRequestBase, option: RequestOption) = {
    val client = HttpClients.createDefault()
    client.execute(request)
  }

  /**
   * GET command.
   */
  def get(url: String)(implicit requestOption: RequestOption = defaultConfig): Response = {
    val setting = new HttpGet(url)
    requestOption.inject(setting)
    Response(withClient(setting, requestOption))
  }
}
