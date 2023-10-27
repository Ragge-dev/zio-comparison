package com.kognic.comparison.vanilla.service

import com.kognic.comparison.DomainError.UserNotFoundError
import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}
import com.kognic.comparison.vanilla.repo.{UserRepo, UserRepoImpl}

import scala.concurrent.{ExecutionContext, Future}

class UserServiceImpl(fileStorage: UserRepo)(implicit ec: ExecutionContext) extends UserService {
  def getUsers(userIds: Seq[UserId]): Future[Either[DomainError, Seq[User]]] =
    for {
      users <- Future.sequence(userIds.map(getUser))
    } yield liftErrors(users)

  private def getUser(userId: UserId): Future[Either[DomainError, User]] =
    for {
    user <- fileStorage.getUser(userId)
  } yield user match {
      case Left(_: UserNotFoundError) => Right(User(userId, "Default", -1))
      case Right(user) => Right(user)
      case Left(error) => Left(error)
    }

  private def liftErrors(users: Seq[Either[DomainError, User]]): Either[DomainError, Seq[User]] =
    users.foldLeft(Right(Seq.empty[User]): Either[DomainError, Seq[User]]) {
      case (acc, Right(user)) => acc.map(_ :+ user)
      case (_, Left(error)) => Left(error)
    }
}

object UserServiceImpl {

  import scala.concurrent.ExecutionContext.Implicits.global

  private lazy val instance = new UserServiceImpl(UserRepoImpl())

  def apply(): UserServiceImpl = instance
}