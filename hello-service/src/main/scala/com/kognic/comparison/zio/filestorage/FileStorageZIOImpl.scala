package com.kognic.comparison.zio.filestorage

import com.kognic.comparison.DomainError.NotFoundError
import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}
import spray.json.*
import zio.{Console, Scope, URLayer, ZIO, ZLayer}

import scala.io.{BufferedSource, Source}
import scala.reflect.io.Path

case class FileStorageZIOImpl(baseDir: Path) extends FileStorageZIO {
  def getUser(userId: UserId): ZIO[Any, DomainError, User] =
    getUserImpl(userId)
      .mapError(e => NotFoundError(s"User with id $userId not found", e))

  private def getUserImpl(userId: UserId): ZIO[Any, Throwable, User] = {
    val path = baseDir / s"user_$userId.json"
    Console.printLine(s"Reading user $userId from file") *> readFromFile(path)
  }

  private def readFromFile(path: Path): ZIO[Any, Throwable, User] = {
    def parseUser(source: BufferedSource): ZIO[Any, Throwable, User] = ZIO.attempt(source.getLines().mkString.parseJson.convertTo[User])
    def openSource: ZIO[Any, Throwable, BufferedSource] = ZIO.attempt(Source.fromInputStream(getClass.getResourceAsStream(path.toString())))
    def closeSource(source: BufferedSource): ZIO[Any, Nothing, Unit] = ZIO.succeed(source.close())

    // When handling resources which needs to be closed a ZIO.scoped is used to ensure that the resource is closed
    // when the code within the scope is done executing.
    ZIO.scoped {
      // Convenient method to acquire, release and use a resource
      ZIO.acquireReleaseWith(openSource)(closeSource)(parseUser)
    }
  }
}

object FileStorageZIOImpl {
  val layer: URLayer[Path, FileStorageZIO] = ZLayer.fromFunction(FileStorageZIOImpl.apply _)
}
