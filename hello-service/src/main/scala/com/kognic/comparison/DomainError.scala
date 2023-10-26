package com.kognic.comparison

// T
sealed abstract class DomainError(msg: String, error: Throwable) extends Throwable(msg, error)

object DomainError {
  case class UserNotFoundError(msg: String, error: Throwable) extends DomainError(msg, error)
  object UserNotFoundError {
    def fromException(error: Exception): UserNotFoundError = UserNotFoundError(error.getMessage, error)
  }
  case class JsonParseError(msg: String, error: Throwable) extends DomainError(msg, error)
  object JsonParseError {
    def fromException(error: Exception): JsonParseError = JsonParseError(error.getMessage, error)
  }
}
