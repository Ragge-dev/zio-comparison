package com.kognic.comparison.vanilla.filestorage

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.User

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.io.Path

class FileStorageImpl(baseDir: Path)(implicit ec: ExecutionContext) extends FileStorage {
  def getUser(userId: UserId): Future[User] = ???
}

object FileStorageImpl {
  import com.kognic.core.application.ThreadPools.Implicits.ioBoundExecutor
  private lazy val instance = new FileStorageImpl(Path("/."))

  def apply(): FileStorageImpl = instance
}
