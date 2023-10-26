package com.kognic.comparison.zio

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.User
import com.kognic.comparison.zio.repo.UserRepoZIOImpl
import com.kognic.comparison.zio.service.{UserServiceZIO, UserServiceZIOImpl}
import zio.{Console, ZIO, ZIOAppDefault, ZLayer}

import java.io.IOException
import scala.reflect.io.Path

object MainZIO extends ZIOAppDefault {
  private val basePath = ZLayer.succeed(Path("/."))
  val userIds = Seq(1, 2, 3, 4, 5, 6).map(id => UserId(id))

  /*
   Our program can now fail in several different ways, but we don't know how it can fail. So we catch
   everything and print the errors.
   */
  override def run: ZIO[Any, Nothing, Unit] = program
    .catchAllCause(e => ZIO.logErrorCause("Error running program: ", e))
    .provide(
      UserServiceZIOImpl.layer, // Has a FileStorageZIO as dependency, which needs to be provided
      UserRepoZIOImpl.layer, // Has a Path as dependency, which needs to be provided
      basePath // Has no dependencies
    )

  private def program: ZIO[UserServiceZIO, Throwable, Unit] =
    for {
      users <- UserServiceZIO.getUsers(userIds)
      _ <- ZIO.foreachDiscard(users)(printUser)
    } yield ()

  private def printUser(user: User): ZIO[Any, IOException, Unit] =
    Console.printLine(user)

}
