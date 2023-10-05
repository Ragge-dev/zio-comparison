package com.kognic.comparison.zio.filestorage

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}
import zio.mock.*
import zio.{URLayer, ZIO, ZLayer}

case class FileStorageZIOMock(proxy: Proxy) extends FileStorageZIO {
  override def getUser(userId: UserId): ZIO[Any, DomainError, User] =
    proxy(FileStorageZIOMock.GetUser, userId)
}

object FileStorageZIOMock extends Mock[FileStorageZIO] {
  object GetUser extends Method[UserId, DomainError, User]

  override val compose: URLayer[Proxy, FileStorageZIO] =
    ZLayer.fromFunction(FileStorageZIOMock.apply _)
}
