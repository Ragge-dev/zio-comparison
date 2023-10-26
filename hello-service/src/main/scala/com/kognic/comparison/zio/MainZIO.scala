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

  val userIds = Seq(7, 1, 2, 3, 4, 5, 6).map(id => UserId(id))

  /*
   Program can now fail in several different ways, but now we only catch and print
   any errors.
   */
  override def run: ZIO[Any, IOException, Unit] = program
    .catchAll(error => Console.printLine(s"Error: $error"))
    .provide(
      UserServiceZIOImpl.layer, // Has a FileStorageZIO as dependency, which needs to be provided
      UserRepoZIOImpl.layer, // Has a Path as dependency, which needs to be provided
      basePath // Has no dependencies
    )

  private def program: ZIO[UserServiceZIO, Throwable, Unit] = {
    for {
      users <- UserServiceZIO.getUsers(userIds)
      _ <- ZIO.foreachDiscard(users)(Console.printLine)
    } yield ()
  }

}
