package kamon.demo.tracing.search.conf

import scala.concurrent.duration.FiniteDuration

trait ConfigSupport {
  protected lazy val config: Config.AppConfig = Config.loadConfig
}

object Config extends ConfigLoader {

  def loadConfig: Config.AppConfig = pureconfig.loadConfigOrThrow[Config.AppConfig](config)

  case class AppConfig(server: ServerConfig, client: ClientConfig, service: ServiceConfig)

  case class ClientConfig(maxTotalConnections: Int, idleTimeout: FiniteDuration, requestTimeout: FiniteDuration)
  case class ServerConfig(host: String, port: Int)
  case class ServiceConfig(internalProvider: ServiceConfig.InternalProvider,
                           externalProvider: ServiceConfig.ExternalProvider)

  object ServiceConfig {
    case class InternalProvider(host: String, endpoint: String)
    case class ExternalProvider(host: String, endpoint: String)
  }

  lazy val appConfig: AppConfig = pureconfig.loadConfigOrThrow[AppConfig](config)
}
