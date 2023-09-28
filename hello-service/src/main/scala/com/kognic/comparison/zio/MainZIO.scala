package com.kognic.comparison.zio

import com.kognic.comparison.DomainError.{IOError, NotFoundError}
import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.zio.filestorage.FileStorageZIOImpl
import com.kognic.comparison.zio.service.{UserServiceZIO, UserServiceZIOImpl}
import com.kognic.comparison.{DomainError, User}
import zio.{Console, ZIO, ZIOAppDefault, ZLayer}

import java.io.IOException
import scala.reflect.io.Path

object MainZIO extends ZIOAppDefault {
  private val basePath = ZLayer.succeed(Path("/."))

  val userIds = Seq(7, 1, 2, 3, 4, 5, 6).map(id => UserId(id))

  // Program has UserServiceZIO as dependency, which needs to be provided
  override def run: ZIO[Any, IOException, Unit] = program
    .provide(
      UserServiceZIOImpl.layer, // Has a FileStorageZIO as dependency, which needs to be provided
      FileStorageZIOImpl.layer, // Has a Path as dependency, which needs to be provided
      basePath // Has no dependencies
    )

  private def program: ZIO[UserServiceZIO, Nothing, Unit] =
    for {
      users <- UserServiceZIO.getUsers(userIds)
      _ <- ZIO.foreachDiscard(users)(a => printUser(a))
    } yield ()

  private def printUser(user: User): ZIO[Any, Nothing, Unit] =
    Console.printLine(user).orDie

}
