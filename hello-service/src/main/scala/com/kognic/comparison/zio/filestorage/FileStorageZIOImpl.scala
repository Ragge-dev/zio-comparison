package com.kognic.comparison.zio.filestorage

import com.kognic.common.utils.ImplicitConversion.asFiniteDuration
import com.kognic.comparison.DomainError.{IOError, NotFoundError}
import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}
import spray.json.*
import zio.{Console, Schedule, URLayer, ZIO, ZLayer, durationInt}

import scala.io.{BufferedSource, Source}
import scala.reflect.io.Path

case class FileStorageZIOImpl(baseDir: Path) extends FileStorageZIO {
  // One line to get exponential backoff
  private val exponentialBackoffSchedule = (Schedule.exponential(10.milliseconds) >>> Schedule.elapsed).whileOutput(_ < 1.seconds)

  def getUser(userId: UserId): ZIO[Any, DomainError, User] = {
    getUserImpl(userId).retry(exponentialBackoffSchedule)
  }

  private def getUserImpl(userId: UserId): ZIO[Any, DomainError, User] =
    ZIO.scoped {
      // Some errors we don't want or need to recover from, then we can use orDie which will "let it crash"
      Console.printLine(s"Reading user $userId from file").orDie *>
        ZIO.acquireReleaseWith(openSource(userId))(closeSource)(parseUser)
    }

  def parseUser(source: BufferedSource): ZIO[Any, IOError, User] = {
    // Attempt will catch any exceptions and wrap them in a failed ZIO
    ZIO.attempt(source.getLines().mkString.parseJson.convertTo[User])
      .mapError(e => IOError(s"Could not parse file to user", e))
  }

  private def openSource(userId: UserId): ZIO[Any, NotFoundError, BufferedSource] = {
    val path = baseDir / s"user_$userId.json"
    ZIO.attempt(Source.fromInputStream(getClass.getResourceAsStream(path.toString())))
      .mapError {
        case e: NullPointerException => NotFoundError(s"Could not find file with path: $path", e)
        case e => NotFoundError(s"User with id $userId was not found", e)
      }
  }

  def closeSource(source: BufferedSource): ZIO[Any, Nothing, Unit] = ZIO.succeed(source.close())

}

object FileStorageZIOImpl {
  val layer: URLayer[Path, FileStorageZIO] = ZLayer.fromFunction(FileStorageZIOImpl.apply _)
}
