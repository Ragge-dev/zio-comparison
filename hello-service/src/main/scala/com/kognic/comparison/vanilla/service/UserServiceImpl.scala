package com.kognic.comparison.vanilla.service

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.vanilla.filestorage.{FileStorage, FileStorageImpl}
import com.kognic.comparison.{DomainError, User}
import com.kognic.core.application.DefaultService

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl(fileStorage: FileStorage)(implicit ec: ExecutionContext) extends UserService with DefaultService {
  // Expected errors are caught in the Either, unexpected errors causes the Future to fail
  def getUsers(userIds: Seq[UserId]): Future[Either[DomainError, Seq[User]]] =
    for {
      nestedUsers <- Future.sequence(userIds.map(fileStorage.getUser))
      users = liftError(nestedUsers)
      _ = logError(users)
    } yield users

  /*
   * We need to lift the errors from the Seq[Either[DomainError, User]] to Either[DomainError, Seq[User]] in order
   * to uphold our interface in getUsers.
   */
  private def liftError(users: Seq[Either[DomainError, User]]): Either[DomainError, Seq[User]] =
    users.foldLeft(Right(Seq()): Either[DomainError, Seq[User]]) {
      case (acc, Right(user)) => acc.map(_ :+ user)
      case (_, Left(e)) => Left(e)
    }

  private def logError(users: Either[DomainError, Seq[User]]): Unit =
    users match {
      case Left(error) => logger.error("Failed to fetch users with error: ", error)
      case Right(_) => ()
    }
}

object UserServiceImpl {

  import com.kognic.core.application.ThreadPools.Implicits.ioBoundExecutor

  private lazy val instance = new UserServiceImpl(FileStorageImpl())

  def apply(): UserServiceImpl = instance
}