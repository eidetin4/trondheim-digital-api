package modules

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import contracts.StudentSubjectContracts
import models.StudentSubjectModels
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import services.StudentSubjectService

class StudentSubjectModuleSpec extends AsyncFlatSpec with AsyncIOSpec with Matchers:

  class MockStudentSubjectService extends StudentSubjectService(null):

    override def getStudents: IO[StudentSubjectContracts.Students] = {
      IO.pure(
        StudentSubjectContracts.Students(
          students = Vector(
            StudentSubjectContracts.Student(
              name = "John Doe",
              subjects = Vector(
                StudentSubjectContracts.StudentSubject(subjectId = 1, grade = "A"),
                StudentSubjectContracts.StudentSubject(subjectId = 2, grade = "B")
              )
            ),
            StudentSubjectContracts.Student(
              name = "Jane Smith",
              subjects = Vector(
                StudentSubjectContracts.StudentSubject(subjectId = 2, grade = "D"),
                StudentSubjectContracts.StudentSubject(subjectId = 3, grade = "C")
              )
            )
          )
        )
      )
    }

    override def getSubjects: IO[StudentSubjectContracts.Subjects] = {
      IO.pure(
        StudentSubjectContracts.Subjects(
          subjects = Vector(
            StudentSubjectContracts.Subject(id = 1, title = "Math", subjectCode = "MATH101"),
            StudentSubjectContracts.Subject(id = 2, title = "English", subjectCode = "ENG202"),
            StudentSubjectContracts.Subject(id = 3, title = "Science", subjectCode = "SCI303"),
            StudentSubjectContracts.Subject(id = 4, title = "History", subjectCode = "HIST404")
          )
        )
      )
    }


  "StudentSubjectModule" should "return students with grades for a given subject code" in {
    val mockService = new MockStudentSubjectService
    val module = new StudentSubjectModule(mockService)

    val subjectCode = "ENG202"

    val expectedResponse = StudentSubjectModels.StudentResponse(
      students = Vector(
        StudentSubjectModels.StudentWithGrade(name = "John Doe", grade = "B"),
        StudentSubjectModels.StudentWithGrade(name = "Jane Smith", grade = "D")
      )
    )

    module.getStudentsBySubjectCode(subjectCode).asserting { response =>
      response shouldEqual expectedResponse
    }
  }

  it should "return an empty response when no students are found for the given subject code" in {
    val mockService = new MockStudentSubjectService
    val module = new StudentSubjectModule(mockService)

    val subjectCode = "HIST404"

    module.getStudentsBySubjectCode(subjectCode).asserting { response =>
      response.students shouldBe empty
    }
  }

  it should "return an empty response when the subject code does not exist" in {
    val mockService = new MockStudentSubjectService
    val module = new StudentSubjectModule(mockService)

    val subjectCode = "NON_EXISTENT_CODE"

    module.getStudentsBySubjectCode(subjectCode).asserting { response =>
      response.students shouldBe empty
    }
  }

  it should "throw an exception when the subject code is empty" in {
    val mockService = new MockStudentSubjectService
    val module = new StudentSubjectModule(mockService)

    val subjectCode = ""

    module.getStudentsBySubjectCode(subjectCode).assertThrows[SubjectCodeEmptyException]
  }
