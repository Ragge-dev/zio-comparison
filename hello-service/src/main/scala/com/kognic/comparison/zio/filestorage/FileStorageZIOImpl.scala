package com.kognic.comparison.zio.filestorage

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.User
import zio.{ZIO, ZLayer}

import scala.reflect.io.Path

case class FileStorageZIOImpl(baseDir: Path) extends FileStorageZIO {
  def getUser(userId: UserId): ZIO[Any, Nothing, User] = ???
}

object FileStorageZIOImpl {
  // To use FileStorageZIOImpl as a dependency we need to create a ZLayer with it.
  // Note that this ZLayer has a dependency as well, on a Path
  val layer: ZLayer[Path, Nothing, FileStorageZIOImpl] = ZLayer.fromFunction(FileStorageZIOImpl.apply _)
}
