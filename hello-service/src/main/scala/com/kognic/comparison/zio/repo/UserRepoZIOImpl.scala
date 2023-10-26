package com.kognic.comparison.zio.repo

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.User
import zio.{Console, ZIO, ZLayer}

import scala.io.{BufferedSource, Source}
import scala.reflect.io.Path
import spray.json.*

case class UserRepoZIOImpl(baseDir: Path) extends UserRepoZIO {
  /*
   * Parsing the User and opening the file can fail, so we use ZIO.attempt to catch
   * any errors. Every error in ZIO is a Throwable.
   */
  def getUser(userId: UserId): ZIO[Any, Throwable, User] =
    Console.printLine(s"Reading user $userId from file") *>
      ZIO.acquireReleaseWith(openSource(userId))(closeSource)(parseUser)

  def parseUser(source: BufferedSource): ZIO[Any, Throwable, User] =
    ZIO.attempt(source.getLines().mkString.parseJson.convertTo[User])

  private def openSource(userId: UserId): ZIO[Any, Throwable, BufferedSource] = {
    val path = baseDir / s"user_$userId.json"
    ZIO.attempt(Source.fromInputStream(getClass.getResourceAsStream(path.toString())))
  }

  private def closeSource(source: BufferedSource): ZIO[Any, Nothing, Unit] = ZIO.succeed(source.close())

}

object UserRepoZIOImpl {
  // To use FileStorageZIOImpl as a dependency we need to create a ZLayer with it.
  // Note that this ZLayer has a dependency as well, on a Path
  val layer: ZLayer[Path, Nothing, UserRepoZIOImpl] = ZLayer.fromFunction(UserRepoZIOImpl.apply _)
}
