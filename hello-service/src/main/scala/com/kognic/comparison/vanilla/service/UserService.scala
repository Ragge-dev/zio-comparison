package com.kognic.comparison.vanilla.service

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.User

import scala.concurrent.Future

trait UserService {
  def getUsers(userIds: Seq[UserId]): Future[Seq[User]]
}
