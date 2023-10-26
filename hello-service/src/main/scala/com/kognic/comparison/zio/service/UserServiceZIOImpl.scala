package com.kognic.comparison.zio.service

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.zio.repo.UserRepoZIO
import com.kognic.comparison.{DomainError, User}
import zio.{ZIO, ZLayer}


class UserServiceZIOImpl(userRepo: UserRepoZIO) extends UserServiceZIO {
  /*

  */
  def getUsers(userIds: Seq[UserId]): ZIO[Any, DomainError, Seq[User]] =
    ZIO.foreachPar(userIds)(userRepo.getUser)

  private def getUser(userId: UserId): ZIO[Any, DomainError, User] =
    userRepo.getUser(userId)
      .orElse(ZIO.succeed(User(userId, "Unknown", -1)))

}

object UserServiceZIOImpl {
  // To use UserServiceZIOImpl as a dependency we need to create a ZLayer with it
  // Note that this ZLayer has a dependency on FileStorageZIO, the same dependency
  // as the UserServiceZIOImpl class does
  val layer: ZLayer[UserRepoZIO, Nothing, UserServiceZIOImpl] =
  ZLayer.fromFunction(new UserServiceZIOImpl(_))
}