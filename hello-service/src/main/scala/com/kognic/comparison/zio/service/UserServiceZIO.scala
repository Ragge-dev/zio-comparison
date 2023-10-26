package com.kognic.comparison.zio.service

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}
import zio.ZIO

trait UserServiceZIO {
  def getUsers(userIds: Seq[UserId]): ZIO[Any, DomainError, Seq[User]]
}

object UserServiceZIO {
  /*
  * Note that the returned ZIO has a dependency on UserServiceZIO, while it does not in the trait.
  * This is because when we create an implementation of the trait we will provide everything we need
  * in order to use getUsers (see UserServiceZIOImpl).
  *
  * However, we might want to use getUsers in a different context, for example in a test. Then we can use
  * this pattern to be able to call getUsers like a static method, but then we need to provide the dependency
  * ourselves (see UserServiceZIOImplTest).
  */
  def getUsers(userIds: Seq[UserId]): ZIO[UserServiceZIO, DomainError, Seq[User]] =
    ZIO.serviceWithZIO(_.getUsers(userIds))
}
