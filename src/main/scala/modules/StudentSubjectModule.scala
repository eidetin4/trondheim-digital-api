package modules

import cats.effect.IO
import models.StudentSubjectModels
import org.http4s.client.Client
import services.StudentSubjectService

class StudentSubjectModule(studentSubjectService: StudentSubjectService):

  def getStudentsBySubjectCode(subjectCode: String): IO[StudentSubjectModels.StudentResponse] = {
    if (subjectCode.trim.isEmpty) {
      IO.raiseError(SubjectCodeEmptyException("Subject code cannot be empty"))
    } else {
      (studentSubjectService.getStudents, studentSubjectService.getSubjects)
        .parMapN { (studentResponse, subjectResponse) =>
          val maybeSubjectId = subjectResponse.subjects.find(_.subjectCode == subjectCode).map(_.id)

          maybeSubjectId match {
            case Some(subjectId) =>
              val studentsWithSubject = studentResponse.students.filter(student =>
                student.subjects.exists(subject => subject.subjectId == subjectId)
              )

              val studentsWithGrades = studentsWithSubject.map(student =>
                StudentSubjectModels.StudentWithGrade(
                  name = student.name,
                  grade = student.subjects.find(sub => sub.subjectId == subjectId).map(_.grade).getOrElse("")
                )
              )

              StudentSubjectModels.StudentResponse(studentsWithGrades)
            case None =>
              StudentSubjectModels.StudentResponse(Vector.empty)
          }
        }.handleErrorWith {
          case e: SubjectCodeEmptyException =>
            IO.raiseError(e)
          case e =>
            IO.raiseError(StudentSubjectModuleException(s"An error occurred while processing the request: ${e.getMessage}", e))
        }
    }
  }

object StudentSubjectModule:
  
  def apply(client: Client[IO]): IO[StudentSubjectModule] = IO {
    val studentSubjectService = new StudentSubjectService(client)
    new StudentSubjectModule(studentSubjectService)
  }.handleErrorWith { error =>
    IO.raiseError(ModuleInitializationException(s"Failed to create StudentSubjectModule: ${error.getMessage}", error))
  }

case class SubjectCodeEmptyException(message: String) extends Exception(message)
case class StudentSubjectModuleException(message: String, cause: Throwable) extends Exception(message, cause)
case class ModuleInitializationException(message: String, cause: Throwable) extends Exception(message, cause)
