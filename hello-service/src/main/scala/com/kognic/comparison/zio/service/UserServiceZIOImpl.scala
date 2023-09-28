package com.kognic.comparison.zio.service

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.zio.filestorage.FileStorageZIO
import com.kognic.comparison.{DomainError, User}
import zio.{IO, URLayer, ZIO, ZLayer}


case class UserServiceZIOImpl(fileStorage: FileStorageZIO) extends UserServiceZIO {
  def getUsers(userIds: Seq[UserId]): ZIO[Any, DomainError, Seq[User]] =
    ZIO.foreachPar(userIds)(fileStorage.getUser)
}

object UserServiceZIOImpl {
  val layer: ZLayer[FileStorageZIO, Nothing, UserServiceZIOImpl] =
    ZLayer.fromFunction(UserServiceZIOImpl.apply _)
}