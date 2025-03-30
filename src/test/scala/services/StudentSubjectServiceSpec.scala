package services

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import contracts.StudentSubjectContracts
import org.http4s.dsl.io.*
import org.http4s.*
import org.http4s.client.Client
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

class StudentSubjectServiceSpec extends AsyncFlatSpec with Matchers:

  "StudentSubjectService" should "return students successfully" in {
    val testClient = Client.fromHttpApp[IO](
      HttpRoutes.of[IO] {
        case GET -> Root / "v2" / "students" =>
          Ok("""{"students": []}""")
        case _ => NotFound()
      }.orNotFound
    )

    val service = new StudentSubjectService(testClient)

    service.getStudents.unsafeRunSync() shouldBe a[StudentSubjectContracts.Students]
  }

  it should "handle URI parsing errors" in {
    val invalidUriClient = Client.fromHttpApp[IO](
      HttpRoutes.of[IO] {
        case GET -> Root / "v2" / "students" =>
          BadRequest("Invalid URI")
        case _ => NotFound()
      }.orNotFound
    )

    val service = new StudentSubjectService(invalidUriClient)

    service.getStudents.attempt.unsafeRunSync() shouldBe a[Left[_, _]]
  }

  it should "handle HTTP errors" in {
    val errorClient = Client.fromHttpApp[IO](
      HttpRoutes.of[IO] {
        case GET -> Root / "v2" / "students" =>
          InternalServerError("Internal Server Error")
        case _ => NotFound()
      }.orNotFound
    )

    val service = new StudentSubjectService(errorClient)

    service.getStudents.attempt.unsafeRunSync() shouldBe a[Left[_, _]]
  }

