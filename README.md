scala_curl
==========

`scala_curl` is a [ApacheHttpComponents](http://hc.apache.org/index.html) client wrapper library.

Only to use simply HTTP client access.

method: `GET, PUT, POST, DELETE`

## Install

```scala
  git clone https://github.com/Sunao-Yoshii/scala_curl.git
  sbt clean update publishLocal
```

## Using

### Settings

  Definition `net.azalea.curl.RequestOption` instance to using scope.

  ```scala
  import bet.azalea.curl._
  import net.azalea.curl.HTTPHelper._

  implicit val config = HTTP.options
  ```

  `HTTP.options` are contains default HTTP options.

  You can change with `clone` method.

### GET

  ```scala
    val response         = HTTP.get("http://localhost:8000")
    val status:Int       = response.status // returns server response code. like 200
    val body:Array[Byte] = response.bodyAsBytes // returns body content as byte array.
    val bodyStr:String   = response.bodyAsString() // returns body content as String. decoding with UTF-8
    val bodySJISStr:String   = response.bodyAsString("Shift_JIS") // returns body content as String. decoding with Shift_JIS
  ```

  HTTP.get returns `net.azalea.curl.Response` instance.

### PUT

  ```scala
    // PUT String contents.
    val response = HTTP.put("http://localhost/path/to", "SampleMessage".toEntity())
  ```

### POST

  ```scala
    // POST String contents.
    val response = HTTP.post("http://localhost/path/to", "SampleMessage")
  ```

### DELETE

  ```scala
    val respond = HTTP.delete("http://localhost:9100/")
  ```

### Send String

  ```scala
    val response = HTTP.post("http://localhost/path/to", "SampleMessage")
  ```

### Send File

  ```scala
    val file = new java.io.File("sample.txt")
    val response = HTTP.post("http://localhost/path/to", file)
  ```

### Send Form

  ```scala
    val respond = HTTP.post("http://localhost:9100/form", Map(
      "param1" -> "value1",
      "param2" -> "value2"
    ))
  ```

### Send multipart

  ```scala
      val respond = HTTP.post("http://localhost:9100/multipart", Map[String, ContentBody](
        "param1" -> "value1".toField(),
        "param2" -> "value2".toField(),
        "file" -> file.toField()
      ))
  ```

### URL helper

  ```scala
    val localhost = "http" % "localhost" // "http://localhost" with toString method
    val withPort  = localhost % 8000 // "http://localhost:8000"
    val withPath  = localhost % "/path/to" // "http://localhost/path/to"
    val withParam = withPath.params(Map("param1" -> "value1")) // "http://localhost/path/to?param1=value1" parameter encoded by UTF-8.
    withParam.toString() // return "http://localhost/path/to?param1=value1"
  ```

## WARNING

  This library is not yet complete.

  Test is also insufficient. There is a possibility that some sort of bug is present.

  Please be careful when using this library.

## LICENSE

  ApacheCommons version 2