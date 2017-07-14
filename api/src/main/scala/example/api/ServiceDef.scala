package example.api

import com.typesafe.config.{Config, ConfigRenderOptions, ConfigValueFactory}
import net.ceedubs.ficus.Ficus._
import spray.json._


case class ServiceDef(name: String, system: String, host: String, port: Int)

object ServiceDef extends DefaultJsonProtocol {
  implicit val format: RootJsonFormat[ServiceDef] = jsonFormat4(ServiceDef.apply)

  def named(name: String, config: Config): Option[ServiceDef] = {
    config.getAs[List[Config]]("example.services")
    .flatMap(
      _.find(
        _.getAs[String]("name")
        .exists(_ == name)
      )
    )
    .map(unmarshal)
  }

  def list(config: Config): List[ServiceDef] = {
    config.getAs[List[Config]]("example.services")
    .getOrElse(List.empty)
    .map(unmarshal)
  }

  private def unmarshal(config: Config): ServiceDef = {
    portAsInt(config)
    .root.render(ConfigRenderOptions.concise)
    .parseJson
    .convertTo[ServiceDef]
  }

  private def portAsInt(cfg: Config) =
    if (cfg.hasPath("port")) cfg.withValue("port", ConfigValueFactory.fromAnyRef(cfg.getInt("port"))) else cfg
}
