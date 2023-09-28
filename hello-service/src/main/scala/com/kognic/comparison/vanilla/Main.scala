package com.kognic.comparison.vanilla

import com.kognic.comparison.Ids.UserId
<<<<<<< HEAD
import com.kognic.comparison.vanilla.service.UserServiceImpl
import com.kognic.comparison.{DomainError, User}
=======
import com.kognic.comparison.User
>>>>>>> 83d490e (No implementations)
import com.kognic.core.application.ThreadPools.Implicits.mappingExecutionContext


object Main extends App {
<<<<<<< HEAD
  private val userService = UserServiceImpl()

=======
  private val userService = UserService()
>>>>>>> 83d490e (No implementations)
  val userIds = Seq(7, 1, 2, 3, 4, 5, 6).map(id => UserId(id))

  // Could do something specific for each error here (e.g. return specific http status code)
  private def handleUsers(users: Seq[User]): Unit = users.foreach(println)

  private val result = for {
    users <- userService.getUsers(userIds)
  } yield handleUsers(users)

  result.map(_ => System.exit(0))

}
