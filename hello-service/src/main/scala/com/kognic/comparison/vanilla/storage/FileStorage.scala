package com.kognic.comparison.vanilla.storage

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}

import scala.concurrent.Future

trait FileStorage {
  def getUser(userId: UserId): Future[Either[DomainError, User]]
}

