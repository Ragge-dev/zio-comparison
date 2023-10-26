package com.kognic.comparison.vanilla

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.User
import com.kognic.comparison.vanilla.service.UserServiceImpl
import com.kognic.core.application.DefaultService
import com.kognic.core.application.ThreadPools.Implicits.mappingExecutionContext

import scala.util.{Failure, Success}


object Main extends App with DefaultService{
  private val userService = UserServiceImpl()

  val userIds = Seq(1, 2, 3, 4, 5, 6).map(id => UserId(id))

  private val program = for {
    users <- userService.getUsers(userIds)
  } yield printUsers(users)

  private def printUsers(users: Seq[User]): Unit = users.foreach(println)

  // Now when we can run the program, we need to wait for the result
  program.onComplete {
    case Success(_) => ()
    case Failure(exception) => logger.error("", exception)
  }
}
