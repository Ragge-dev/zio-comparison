package com.kognic.comparison.zio.filestorage

import com.kognic.common.utils.ImplicitConversion.asFiniteDuration
import com.kognic.comparison.DomainError.{IOError, NotFoundError}
import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}
import spray.json.*
import zio.{Console, Schedule, ZIO, ZLayer, durationInt}

import scala.io.{BufferedSource, Source}
import scala.reflect.io.Path

case class FileStorageZIOImpl(baseDir: Path) extends FileStorageZIO {

  def getUser(userId: UserId): ZIO[Any, DomainError, User] = {
    // One line to get exponential backoff
    val exponentialBackoffSchedule = (Schedule.exponential(10.milliseconds) >>> Schedule.elapsed).whileOutput(_ < 1.seconds)
    getUserImpl(userId).retry(exponentialBackoffSchedule)
  }

  // Scopes define the lifetime of resources, in this case the source is only open during the parsing of the user
  private def getUserImpl(userId: UserId): ZIO[Any, DomainError, User] =
      // Some errors we don't want or need to recover from, then we can use orDie which will "let it crash"
      Console.printLine(s"Reading user $userId from file").orDie *>
        ZIO.acquireReleaseWith(openSource(userId))(closeSource)(parseUser)

  def parseUser(source: BufferedSource): ZIO[Any, IOError, User] =
    ZIO.attempt(source.getLines().mkString.parseJson.convertTo[User])
      .mapError(e => IOError(s"Could not parse file to user", e))

  private def openSource(userId: UserId): ZIO[Any, NotFoundError, BufferedSource] = {
    val path = baseDir / s"user_$userId.json"
    ZIO.attempt(Source.fromInputStream(getClass.getResourceAsStream(path.toString())))
      .mapError {
        case e: NullPointerException => NotFoundError(s"Could not find file with path: $path", e)
        case e => NotFoundError(s"User with id $userId was not found", e)
      }
  }

  private def closeSource(source: BufferedSource): ZIO[Any, Nothing, Unit] = ZIO.succeed(source.close())

}

object FileStorageZIOImpl {
  // To use FileStorageZIOImpl as a dependency we need to create a ZLayer with it.
  // Note that this ZLayer has a dependency as well, on a Path
  val layer: ZLayer[Path, Nothing, FileStorageZIOImpl] = ZLayer.fromFunction(FileStorageZIOImpl.apply _)
}
