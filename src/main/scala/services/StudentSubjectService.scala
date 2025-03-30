package services

import cats.effect.IO
import cats.effect.std.Env
import contracts.StudentSubjectContracts.{Students, Subjects}
import io.circe.Error as CirceError
import org.http4s.circe.jsonOf
import org.http4s.client.{Client, UnexpectedStatus}
import org.http4s.{Header, Headers, Method, Request, Uri}
import org.typelevel.ci.CIString

import java.util.Base64

class StudentSubjectService(client: Client[IO]):

  private val baseUrl = "https://recruit-task.tipcloudqa.trondheim.kommune.no"

  private def createAuthHeaders(): IO[Headers] = (
    Env[IO].get("STUDENT_API_USERNAME"),
    Env[IO].get("STUDENT_API_PASSWORD")
  ).parMapN {
    case (Some(username), Some(password)) => {
      val authString = s"$username:$password"
      val encodedAuth = Base64.getEncoder.encodeToString(authString.getBytes)

      Headers(
        Header.Raw(CIString("Authorization"), s"Basic $encodedAuth")
      )
    }
    case _ => {
      Headers.empty
    }
  }
  
  def getStudents: IO[Students] = {
    val uriAttempt = Uri.fromString(s"$baseUrl/v2/students")

    uriAttempt.fold(
      e => IO.raiseError(ParseUriException(s"Failed to parse students URI: ${e.message}")),
      uri => createAuthHeaders().flatMap { headers =>
        val request = Request[IO](
          method = Method.GET,
          uri = uri,
          headers = headers
        )

        handleHttpErrors(client.expect[Students](request)(jsonOf[IO, Students]))
      }
    )
  }
  
  def getSubjects: IO[Subjects] = {
    val uriAttempt = Uri.fromString(s"$baseUrl/v2/subjects")

    uriAttempt.fold(
      e => IO.raiseError(ParseUriException(s"Failed to parse subjects URI: ${e.message}")),
      uri => createAuthHeaders().flatMap { headers =>
        val request = Request[IO](
          method = Method.GET,
          uri = uri,
          headers = headers
        )

        handleHttpErrors(client.expect[Subjects](request)(jsonOf[IO, Subjects]))
      }
    )
  }

  private def handleHttpErrors[A](io: IO[A]): IO[A] = {
    io.handleErrorWith {
      case UnexpectedStatus(status, _, _) =>
        IO.raiseError(UnexpectedHttpStatusException(status.code, s"Unexpected HTTP status: ${status.reason}"))
      case e: CirceError =>
        IO.raiseError(JsonDecodingException("Failed to decode JSON response", e))
      case e: Exception =>
        IO.raiseError(HttpRequestException("Failed to make HTTP request", e))
    }
  }

case class UnexpectedHttpStatusException(status: Int, message: String) extends Exception(s"Unexpected HTTP status: $status - $message")
case class JsonDecodingException(message: String, cause: Throwable) extends Exception(message, cause)
case class HttpRequestException(message: String, cause: Throwable) extends Exception(message, cause)
case class ParseUriException(message: String) extends Exception(message)
