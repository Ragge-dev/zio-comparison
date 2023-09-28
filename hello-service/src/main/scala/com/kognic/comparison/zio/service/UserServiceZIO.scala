package com.kognic.comparison.zio.service

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}
import zio.{IO, ZIO}

trait UserServiceZIO {
  def getUsers(userIds: Seq[UserId]): ZIO[Any, DomainError, Seq[User]]
}

object UserServiceZIO {
  def getUsers(userIds: Seq[UserId]): ZIO[UserServiceZIO, DomainError, Seq[User]] =
    ZIO.serviceWithZIO(_.getUsers(userIds))
}
