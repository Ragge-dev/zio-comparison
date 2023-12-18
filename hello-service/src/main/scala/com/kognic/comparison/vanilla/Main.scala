package com.kognic.comparison.vanilla

import com.kognic.comparison.DomainError.{JsonParseError, UserNotFoundError}
import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.vanilla.repo.UserRepoImpl
import com.kognic.comparison.vanilla.service.UserServiceImpl
import com.kognic.core.application.DefaultService

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.reflect.io.Path


object Main extends App with DefaultService {

  private val userRepo = new UserRepoImpl(Path("/."))
  private val userService = new UserServiceImpl(userRepo)

  val userIds = Seq(1, 2, 3, 4, 5, 6).map(id => UserId(id))

  private val program =
    userService.getUsers(userIds)
      .map(_.foreach(println))
      .recover{
        case _: UserNotFoundError => println("Special case for UserNotFound")
        case _: JsonParseError => println("Special case for JsonParseError")
      }

  // Now when we can run the program, we need to wait for the result
  Await.result(program, 1.minute)
}
