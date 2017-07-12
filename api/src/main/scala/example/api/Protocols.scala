package example.api

import spray.json.{DefaultJsonProtocol, RootJsonFormat}


object Protocols {
  case class ServiceDef(name: String, system: String, host: String, port: Int)

  object ServiceDef extends DefaultJsonProtocol {
    implicit val format: RootJsonFormat[ServiceDef] = jsonFormat4(ServiceDef.apply)
  }
}
