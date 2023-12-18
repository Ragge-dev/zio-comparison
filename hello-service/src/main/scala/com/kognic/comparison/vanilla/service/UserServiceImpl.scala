package com.kognic.comparison.vanilla.service

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.User
import com.kognic.comparison.vanilla.repo.UserRepo

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl(fileStorage: UserRepo)(implicit ec: ExecutionContext) extends UserService {
  def getUsers(userIds: Seq[UserId]): Future[Seq[User]] =
    for {
      users <- Future.sequence(userIds.map(fileStorage.getUser))
    } yield users

}