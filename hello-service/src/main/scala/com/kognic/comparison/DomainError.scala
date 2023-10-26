package com.kognic.comparison

sealed abstract class DomainError(msg: String, error: Throwable) extends Throwable(msg, error) {

}

object DomainError {
  case class UserNotFoundError(msg: String, error: Throwable) extends DomainError(msg, error)
  case class UserConfigNotFound(msg: String, error: Throwable) extends DomainError(msg, error)
  case class JsonParseError(msg: String, error: Throwable) extends DomainError(msg, error)
}
