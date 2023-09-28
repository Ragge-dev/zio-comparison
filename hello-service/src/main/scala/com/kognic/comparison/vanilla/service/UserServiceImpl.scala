package com.kognic.comparison.vanilla.service

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.User
import com.kognic.comparison.vanilla.filestorage.{FileStorage, FileStorageImpl}

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl(fileStorage: FileStorage)(implicit ec: ExecutionContext) extends UserService {
  def getUsers(userIds: Seq[UserId]): Future[Seq[User]] =
    for {
      users <- Future.sequence(userIds.map(fileStorage.getUser))
    } yield users
}

object UserServiceImpl {
  import com.kognic.core.application.ThreadPools.Implicits.mappingExecutionContext
  private lazy val instance = new UserServiceImpl(FileStorageImpl())

  def apply(): UserServiceImpl = instance
}