package api

import cats.effect.*
import org.http4s.dsl.io.*
import org.http4s.*
import org.http4s.circe.*
import models.StudentSubjectModels
import modules.{StudentSubjectModule, SubjectCodeEmptyException}

class StudentSubjectApi(studentSubjectModule: StudentSubjectModule):

  implicit val studentResponseEncoder: EntityEncoder[IO, StudentSubjectModels.StudentResponse] = jsonEncoderOf[IO, StudentSubjectModels.StudentResponse]
  
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {

      case req @ Method.GET -> Root / "students" :? SubjectCodeQueryParamMatcher(subjectCode) =>
        studentSubjectModule.getStudentsBySubjectCode(subjectCode)
          .flatMap(students => Ok(students))
          .handleErrorWith {
            case e: SubjectCodeEmptyException =>
              BadRequest(s"Invalid request: ${e.getMessage}")
            case e =>
              InternalServerError(s"An unexpected error occurred: ${e.getMessage}")
          }
    }
  
object SubjectCodeQueryParamMatcher extends QueryParamDecoderMatcher[String]("subjectCode")

case class StudentSubjectApiError(message: String, cause: Throwable) extends Exception(message, cause)
