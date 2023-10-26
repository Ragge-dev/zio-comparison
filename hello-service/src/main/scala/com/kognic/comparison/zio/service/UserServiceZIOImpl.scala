package com.kognic.comparison.zio.service

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.zio.filestorage.UserRepoZIO
import com.kognic.comparison.{DomainError, User}
import zio.{ZIO, ZLayer}


class UserServiceZIOImpl(userRepo: UserRepoZIO) extends UserServiceZIO {
  def getUsers(userIds: Seq[UserId]): ZIO[Any, Nothing, Seq[User]] =
    ZIO.foreachPar(userIds)(userRepo.getUser)
}

object UserServiceZIOImpl {
  // To use UserServiceZIOImpl as a dependency we need to create a ZLayer with it
  // Note that this ZLayer has a dependency on FileStorageZIO, the same dependency
  // as the UserServiceZIOImpl class does
  val layer: ZLayer[UserRepoZIO, Nothing, UserServiceZIOImpl] =
    ZLayer.fromFunction(new UserServiceZIOImpl(_))
}