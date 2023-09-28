package com.kognic.comparison.zio.filestorage

import com.kognic.common.utils.ImplicitConversion.asFiniteDuration
import com.kognic.comparison.DomainError.NotFoundError
import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}
import spray.json.*
import zio.{Console, Schedule, Scope, URLayer, ZIO, ZLayer, durationInt}

import scala.io.{BufferedSource, Source}
import scala.reflect.io.Path

case class FileStorageZIOImpl(baseDir: Path) extends FileStorageZIO {
  def getUser(userId: UserId): ZIO[Any, DomainError, User] =
    ZIO.scoped {
      val exponentialBackoffSchedule = (Schedule.exponential(10.milliseconds) >>> Schedule.elapsed).whileOutput(_ < 1.seconds)
      getUserImpl(userId)
        .retry(exponentialBackoffSchedule)
        .mapError(e => NotFoundError(s"User with id $userId not found", e))
    }

  // Reading from a file does not need a ZIO, but it would in case we fetch from a database or cloud
  private def getUserImpl(userId: UserId): ZIO[Scope, Throwable, User] = {
    val path = baseDir / s"user_$userId.json"
    Console.printLine(s"Reading user $userId from file") *> readFromFile(path)
  }

  private def readFromFile(path: Path): ZIO[Scope, Throwable, User] = {
    def parseUser(source: BufferedSource): ZIO[Any, Throwable, User] = ZIO.attempt(source.getLines().mkString.parseJson.convertTo[User])
    def openSource: ZIO[Any, Throwable, BufferedSource] = ZIO.attempt(Source.fromInputStream(getClass.getResourceAsStream(path.toString())))
    def closeSource(source: BufferedSource): ZIO[Any, Nothing, Unit] = ZIO.succeed(source.close())

    ZIO.acquireReleaseWith(openSource)(closeSource)(parseUser)
  }
}

object FileStorageZIOImpl {
  val layer: URLayer[Path, FileStorageZIO] = ZLayer.fromFunction(FileStorageZIOImpl.apply _)
}
