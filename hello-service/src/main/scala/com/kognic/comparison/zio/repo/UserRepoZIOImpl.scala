package com.kognic.comparison.zio.repo

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}
import com.kognic.comparison.DomainError.{JsonParseError, UserNotFoundError}
import zio.{Console, ZIO, ZLayer}

import scala.io.{BufferedSource, Source}
import scala.reflect.io.Path
import spray.json.*

case class UserRepoZIOImpl(baseDir: Path) extends UserRepoZIO {
  /*
  Now getUser can fail with one of several DomainErrors, which is a much
  smaller group of errors than Throwable.

  Logging in ZIO cannot fail, but Console.printLine can. I choose
  Console.printLine in my examples because it prints much less info, and
  then I choose to crash the program (.orDie) if it fails.
  */
  def getUser(userId: UserId): ZIO[Any, DomainError, User] =
    Console.printLine(s"Reading user $userId from file").orDie *>
      ZIO.acquireReleaseWith(openSource(userId))(closeSource)(parseUser)

  def parseUser(source: BufferedSource): ZIO[Any, JsonParseError, User] =
    ZIO.attempt(source.getLines().mkString.parseJson.convertTo[User])
      .mapError(e => JsonParseError("Failed to parse user", e))

  private def openSource(userId: UserId): ZIO[Any, UserNotFoundError, BufferedSource] = {
    val path = baseDir / s"user_$userId.json"
    ZIO.attempt(Source.fromInputStream(getClass.getResourceAsStream(path.toString())))
      .mapError(e => UserNotFoundError(s"User with id $userId not found", e))
  }
  private def closeSource(source: BufferedSource): ZIO[Any, Nothing, Unit] = ZIO.succeed(source.close())

}

object UserRepoZIOImpl {
  // To use FileStorageZIOImpl as a dependency we need to create a ZLayer with it.
  // Note that this ZLayer has a dependency as well, on a Path
  val layer: ZLayer[Path, Nothing, UserRepoZIOImpl] = ZLayer.fromFunction(UserRepoZIOImpl.apply _)
}
