package helper

import org.specs2.mutable.BeforeAfter
import org.scalatra.test.EmbeddedJettyContainer
import javax.servlet.http.HttpServlet

abstract class WithServer(override val port: Int = 9100) extends BeforeAfter with EmbeddedJettyContainer {

  def servlet:Class[_ <: HttpServlet]

  val holder = addServlet(servlet, "/*")

  def before = {
    this.start()
  }

  def after = {
    this.stop()
  }
}
