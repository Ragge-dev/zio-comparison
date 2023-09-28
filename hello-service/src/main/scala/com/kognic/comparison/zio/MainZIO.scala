package com.kognic.comparison.zio

import com.kognic.comparison.DomainError.{IOError, NotFoundError}
import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.zio.filestorage.FileStorageZIOImpl
import com.kognic.comparison.zio.service.{UserServiceZIO, UserServiceZIOImpl}
import com.kognic.comparison.{DomainError, User}
import zio.{Console, IO, ZIO, ZIOAppDefault, ZLayer}

import java.io.IOException
import scala.reflect.io.Path

object MainZIO extends ZIOAppDefault {
  private val basePath = ZLayer.succeed(Path("/."))

  val userIds = Seq(1, 2, 3, 4, 5, 6).map(id => UserId(id))

  // Program has UserServiceZIO as dependency, which needs to be provided
  override def run: ZIO[Any, IOException, Unit] = program
    .catchAll(handleError)
    .provide(
      UserServiceZIOImpl.layer, // Has a FileStorageZIO as dependency, which needs to be provided
      FileStorageZIOImpl.layer, // Has a Path as dependency, which needs to be provided
      basePath // Has no dependencies
    )

  private def program: ZIO[UserServiceZIO, DomainError, Unit] =
    for {
      users <- UserServiceZIO.getUsers(userIds)
      _ <- ZIO.foreachDiscard(users)(a => printUser(a))
    } yield ()

  private def printUser(user: User): ZIO[Any, IOError, Unit] =
    Console.printLine(user)
      .mapError(e => IOError("Failed to print users to terminal", e))

  // Could do something specific for each error (e.g. return specific http status code)
  private def handleError(error: DomainError): ZIO[Any, IOException, Unit] = error match {
    case NotFoundError(msg, _) => Console.printLine(msg)
    case IOError(msg, _) => Console.printLine(msg)
  }
}
