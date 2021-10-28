package com.lgajowy

import com.lgajowy.UserRegistry.ActionPerformed

import java.util.UUID

object JsonFormats {
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
