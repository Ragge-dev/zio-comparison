package com.kognic.comparison.vanilla

import com.kognic.comparison.DomainError.{JsonParseError, UserNotFoundError}
import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.vanilla.service.UserServiceImpl
import com.kognic.comparison.{DomainError, User}
import com.kognic.core.application.DefaultService

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt


object Main extends App with DefaultService {
  private val userService = UserServiceImpl()

  val userIds = Seq(1, 2, 3, 4, 5, 6).map(id => UserId(id))

  private val program = for {
    result <- userService.getUsers(userIds)
  } yield printResult(result)

  private def printResult(users: Either[DomainError, Seq[User]]): Unit =
    users
      .fold(handleError, users => users.foreach(println))

  private def handleError(error: DomainError): Unit =
    error match {
      case _: UserNotFoundError => println("Special case for UserNotFound")
      case _: JsonParseError => println("Special case for JsonParseError")
    }

  // Now when we can run the program, we need to wait for the result
  Await.result(program, 1.minute)
}
