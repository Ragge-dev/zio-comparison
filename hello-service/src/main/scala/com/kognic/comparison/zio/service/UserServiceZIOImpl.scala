package com.kognic.comparison.zio.service

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.zio.filestorage.FileStorageZIO
import com.kognic.comparison.{DomainError, User}
import zio.{ZIO, ZLayer}


case class UserServiceZIOImpl(fileStorage: FileStorageZIO) extends UserServiceZIO {
  def getUsers(userIds: Seq[UserId]): ZIO[Any, DomainError, Seq[User]] =
    ZIO.foreachPar(userIds)(fileStorage.getUser)
}

object UserServiceZIOImpl {
  // To use UserServiceZIOImpl as a dependency we need to create a ZLayer with it
  // Note that this ZLayer has a dependency on FileStorageZIO, the same dependency
  // as the UserServiceZIOImpl class does
  val layer: ZLayer[FileStorageZIO, Nothing, UserServiceZIOImpl] =
    ZLayer.fromFunction(UserServiceZIOImpl.apply _)
}