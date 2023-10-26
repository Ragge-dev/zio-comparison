package com.kognic.comparison.vanilla.repo

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}

import scala.concurrent.Future

trait UserRepo {
  def getUser(userId: UserId): Future[Either[DomainError, User]]
}

