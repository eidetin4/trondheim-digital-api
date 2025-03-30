package contracts

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

object StudentSubjectContracts:

  case class StudentSubject(subjectId: Int, grade: String)
  object StudentSubject:
    implicit val decoder: Decoder[StudentSubject] = deriveDecoder[StudentSubject]

  case class Student(name: String, subjects: Vector[StudentSubject])
  object Student:
    implicit val decoder: Decoder[Student] = deriveDecoder[Student]

  case class Students(students: Vector[Student])
  object Students:
    implicit val decoder: Decoder[Students] = deriveDecoder[Students]

  case class Subject(id: Int, title: String, subjectCode: String)
  object Subject:
    implicit val decoder: Decoder[Subject] = deriveDecoder[Subject]
    
  case class Subjects(subjects: Vector[Subject])
  object Subjects:
    implicit val decoder: Decoder[Subjects] = deriveDecoder[Subjects]
