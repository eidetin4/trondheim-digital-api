import api.StudentSubjectApi
import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.{ipv4, port}
import modules.StudentSubjectModule
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*

import scala.concurrent.duration.DurationInt

object Main extends IOApp:
  
  private val serverHost = ipv4"0.0.0.0"
  private val serverPort = port"8080"
  private val clientTimeout = 30.seconds
  
  def run(args: List[String]): IO[ExitCode] = {
    EmberClientBuilder
      .default[IO]
      .withTimeout(clientTimeout)
      .build
      .use { client =>
        for {
          studentSubjectModule <- StudentSubjectModule(client)
          api = new StudentSubjectApi(studentSubjectModule)
          exitCode <- EmberServerBuilder
            .default[IO]
            .withHost(serverHost)
            .withPort(serverPort)
            .withHttpApp(api.routes.orNotFound)
            .build
            .use(_ => IO.never)
            .as(ExitCode.Success)
        } yield exitCode
      }
  }
