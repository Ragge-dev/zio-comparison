package com.kognic.comparison.zio

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.zio.repo.UserRepoZIOImpl
import com.kognic.comparison.zio.service.{UserServiceZIO, UserServiceZIOImpl}
import com.kognic.comparison.{DomainError, User}
import zio.{Console, ZIO, ZIOAppDefault, ZLayer}

import scala.reflect.io.Path

object MainZIO extends ZIOAppDefault {
  private val basePath = ZLayer.succeed(Path("/."))
  private val userIds = Seq(1, 2, 3, 4, 5, 6).map(id => UserId(id))

  /*
  Program can only fail with our DomainError type
   */
  override def run: ZIO[Any, DomainError, Unit] = program
    .provide(
      UserServiceZIOImpl.layer, // Has a FileStorageZIO as dependency, which needs to be provided
      UserRepoZIOImpl.layer, // Has a Path as dependency, which needs to be provided
      basePath // Has no dependencies
    )

  private def program: ZIO[UserServiceZIO, DomainError, Unit] =
    for {
      users <- UserServiceZIO.getUsers(userIds)
      _ <- ZIO.foreachDiscard(users)(printUser)
    } yield ()

  private def printUser(user: User): ZIO[Any, Nothing, Unit] =
    Console.printLine(user).orDie

}
