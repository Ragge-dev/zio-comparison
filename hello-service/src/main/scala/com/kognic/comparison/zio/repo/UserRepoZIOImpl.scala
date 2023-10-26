package com.kognic.comparison.zio.repo

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}
import zio.{ZIO, ZLayer}

import scala.reflect.io.Path

case class UserRepoZIOImpl(baseDir: Path) extends UserRepoZIO {
  def getUser(userId: UserId): ZIO[Any, DomainError, User] = ???
}

object UserRepoZIOImpl {
  // To use FileStorageZIOImpl as a dependency we need to create a ZLayer with it.
  // Note that this ZLayer has a dependency as well, on a Path
  val layer: ZLayer[Path, Nothing, UserRepoZIOImpl] = ZLayer.fromFunction(UserRepoZIOImpl.apply _)
}
