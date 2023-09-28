package com.kognic.comparison

abstract class DomainError(msg: String, cause: Throwable) extends Throwable(msg, cause) {
  override def getMessage: String = msg
}
object DomainError {
  case class NotFoundError(msg: String, cause: Throwable) extends DomainError(msg, cause)
  case class IOError(msg: String, cause: Throwable) extends DomainError(msg, cause)
}