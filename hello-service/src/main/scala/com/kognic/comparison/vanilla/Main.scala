package com.kognic.comparison.vanilla

import com.kognic.comparison.DomainError.NotFoundError
import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}
import com.kognic.core.application.ThreadPools.Implicits.mappingExecutionContext


object Main extends App {
  private val userService = UserService()

  val userIds = Seq(7, 1, 2, 3, 4, 5, 6).map(id => UserId(id))

  // Could do something specific for each error here (e.g. return specific http status code)
  private def handleUsers(users: Either[DomainError, Seq[User]]): Unit = users match {
    case Left(value: NotFoundError) =>
      println(value.msg)
    case Left(value) => println(value)
    case Right(value) => value.foreach(println)
  }

  private val result = for {
    users <- userService.getUsers(userIds)
  } yield handleUsers(users)

  result.map(_ => System.exit(0))

}
