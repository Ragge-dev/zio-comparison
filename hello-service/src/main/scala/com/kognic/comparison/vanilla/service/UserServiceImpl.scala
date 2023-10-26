package com.kognic.comparison.vanilla.service

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.User
import com.kognic.comparison.vanilla.repo.{UserRepo, UserRepoImpl}

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl(fileStorage: UserRepo)(implicit ec: ExecutionContext) extends UserService {
  // Expected errors are caught in the Either, unexpected errors causes the Future to fail
  def getUsers(userIds: Seq[UserId]): Future[Seq[User]] =
    for {
      users <- Future.sequence(userIds.map(fileStorage.getUser))
    } yield users

}

object UserServiceImpl {

  import com.kognic.core.application.ThreadPools.Implicits.ioBoundExecutor

  private lazy val instance = new UserServiceImpl(UserRepoImpl())

  def apply(): UserServiceImpl = instance
}