package com.kognic.comparison.zio.repo

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}
import zio.mock.*
import zio.{URLayer, ZIO, ZLayer}

// Mock implementation of FileStorageZIO
case class UserRepoZIOMock(proxy: Proxy) extends UserRepoZIO {
  override def getUser(userId: UserId): ZIO[Any, Nothing, User] =
    proxy(UserRepoZIOMock.GetUser, userId)
}

object UserRepoZIOMock extends Mock[UserRepoZIO] {
  object GetUser extends Method[UserId, Nothing, User]

  override val compose: URLayer[Proxy, UserRepoZIO] =
    ZLayer.fromFunction(UserRepoZIOMock.apply _)
}
