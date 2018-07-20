package kamon.demo.tracing.user.api

import cats.effect.IO
import io.circe.syntax._
import kamon.demo.tracing.user.program.UserProgram
import kamon.demo.tracing.user.utils.LogSupport
import org.http4s._
import org.http4s.dsl.io._

import scala.util.control.NonFatal

case class UserService(userProgram: UserProgram) extends LogSupport {
  import kamon.demo.tracing.user.utils.CirceUtils.circeCustomSyntax._

  def service: HttpService[IO] = {

    def route: HttpService[IO] = HttpService[IO] {
      case GET -> Root / LongVar(userId) => handleGetUser(userId)
    }

    def handleGetUser(userId: Long): IO[Response[IO]] = {
      userProgram
        .findById(userId)
        .attempt
        .flatMap {
          case Right(Some(user)) => Ok(user.asJson)
          case Right(None) => NotFound(())
          case Left(NonFatal(exception)) =>
            val message = s"Error getting user by ID for [UserId = $userId]. Cause: ${exception.getMessage}"
            log.error(message, exception)
            InternalServerError(message)
        }
    }

    route
  }
}
