package api

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import models.StudentSubjectModels
import modules.{StudentSubjectModule, SubjectCodeEmptyException}
import org.http4s.implicits.uri
import org.http4s.{Method, Request, Status}
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

class StudentSubjectApiSpec extends AsyncFlatSpec with Matchers:

  private val mockModule = new StudentSubjectModule(null) {
    override def getStudentsBySubjectCode(subjectCode: String): IO[StudentSubjectModels.StudentResponse] = {
      if (subjectCode == "ERROR") {
        IO.raiseError(new IllegalArgumentException("Test error"))
      } else if (subjectCode == "") {
        IO.raiseError(SubjectCodeEmptyException("Subject code cannot be empty"))
      } else if (subjectCode == "UNKNOWN") {
        IO.pure(StudentSubjectModels.StudentResponse(Vector.empty))
      } else {
        IO.pure(StudentSubjectModels.StudentResponse(Vector.empty))
      }
    }
  }

  private val api = new StudentSubjectApi(mockModule)

  "GET /students" should "return 400 for empty subject code" in {
    val request = Request[IO](Method.GET, uri"/students?subjectCode=")
    val response = api.routes.orNotFound.run(request).unsafeRunSync()

    response.status shouldBe Status.BadRequest
  }

  it should "return 200 with empty array for unknown subject code" in {
    val request = Request[IO](Method.GET, uri"/students?subjectCode=UNKNOWN")
    val response = api.routes.orNotFound.run(request).unsafeRunSync()

    response.status shouldBe Status.Ok
  }

  it should "return 500 for internal server error" in {
    val request = Request[IO](Method.GET, uri"/students?subjectCode=ERROR")
    val response = api.routes.orNotFound.run(request).unsafeRunSync()

    response.status shouldBe Status.InternalServerError
  }
