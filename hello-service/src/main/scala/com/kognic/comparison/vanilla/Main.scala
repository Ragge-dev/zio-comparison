package com.kognic.comparison.vanilla

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.User
import com.kognic.comparison.vanilla.service.UserServiceImpl
import com.kognic.core.application.ThreadPools.Implicits.mappingExecutionContext

import scala.concurrent.Await
import scala.concurrent.duration.DurationDouble


object Main extends App {
  private val userService = UserServiceImpl()

  val userIds = Seq(7, 1, 2, 3, 4, 5, 6).map(id => UserId(id))

  // Could do something specific for each error here (e.g. return specific http status code)
  private def handleUsers(users: Seq[User]): Unit = users.foreach(println)

  private val result = for {
    users <- userService.getUsers(userIds)
  } yield handleUsers(users)

  // Now when we can run the program, we need to wait for the result
  Await.result(result, 1.minute)

}
