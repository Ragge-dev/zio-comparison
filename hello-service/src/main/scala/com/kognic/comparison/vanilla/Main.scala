package com.kognic.comparison.vanilla

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.vanilla.service.UserServiceImpl
import com.kognic.core.application.ThreadPools.Implicits.mappingExecutionContext


object Main extends App {
  private val userService = UserServiceImpl()

  val userIds = Seq(1, 2, 3, 4, 5, 6).map(id => UserId(id))


  private val result = for {
    users <- userService.getUsers(userIds)
  } yield users.foreach(println)

  result.map(_ => System.exit(0))

}
