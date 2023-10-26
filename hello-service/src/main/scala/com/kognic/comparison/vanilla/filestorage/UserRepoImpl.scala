package com.kognic.comparison.vanilla.filestorage

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.User

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.io.Path

class UserRepoImpl(baseDir: Path)(implicit ec: ExecutionContext) extends UserRepo {
  def getUser(userId: UserId): Future[User] = ???
}

object UserRepoImpl {
  import com.kognic.core.application.ThreadPools.Implicits.ioBoundExecutor
  private lazy val instance = new UserRepoImpl(Path("/."))

  def apply(): UserRepoImpl = instance
}
