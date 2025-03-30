package models

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

object StudentSubjectModels:

  final case class StudentWithGrade(name: String, grade: String)
  object StudentWithGrade:
    implicit val encoder: Encoder[StudentWithGrade] = deriveEncoder[StudentWithGrade]
  
  final case class StudentResponse(students: Vector[StudentWithGrade])
  object StudentResponse:
    implicit val encoder: Encoder[StudentResponse] = deriveEncoder[StudentResponse]
