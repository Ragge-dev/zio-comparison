package com.kognic.comparison.zio.filestorage

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}
import zio.ZIO

trait FileStorageZIO {
  def getUser(userId: UserId): ZIO[Any, DomainError, User]
}

