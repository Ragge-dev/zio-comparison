package com.kognic.comparison.vanilla.service

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.vanilla.filestorage.{FileStorage, FileStorageImpl}
import com.kognic.comparison.{DomainError, User}

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl(fileStorage: FileStorage)(implicit ec: ExecutionContext) extends UserService {
  // Expected errors are caught in the Either, unexpected errors causes the Future to fail
  def getUsers(userIds: Seq[UserId]): Future[Either[DomainError, Seq[User]]] =
    for {
      users <- Future.sequence(userIds.map(fileStorage.getUser))
    } yield liftError(users)

  /*
   * We need to lift the errors from the Seq[Either[DomainError, User]] to Either[DomainError, Seq[User]] in order
   * to uphold our interface in getUsers.
   */
  private def liftError(users: Seq[Either[DomainError, User]]): Either[DomainError, Seq[User]] =
    users.foldLeft(Right(Seq()): Either[DomainError, Seq[User]]) {
      case (acc, Right(user)) => acc.map(_ :+ user)
      case (_, Left(e)) => Left(e)
    }
}

object UserServiceImpl {

  import com.kognic.core.application.ThreadPools.Implicits.ioBoundExecutor

  private lazy val instance = new UserServiceImpl(FileStorageImpl())

  def apply(): UserServiceImpl = instance
}