package com.kognic.comparison

import com.kognic.common.json.BaseJsonSupport.*
import com.kognic.comparison.Ids.UserId
import spray.json.JsonFormat

case class User(userId: UserId, name: String, age: Int)

object User {
  implicit val userFormat: JsonFormat[User] = jsonFormat3(User.apply)
}