package helper

import org.scalatra.ScalatraServlet

class SimpleGetServlet extends ScalatraServlet {
  get("/") {
    "Hello world!"
  }
}
