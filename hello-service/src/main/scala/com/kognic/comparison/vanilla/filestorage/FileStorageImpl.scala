package com.kognic.comparison.vanilla.filestorage

import com.kognic.common.utils.RetryFuture.retry
import com.kognic.comparison.DomainError.NotFoundError
import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}
import com.kognic.core.application.SystemActorSystem.Implicits.scheduler
import spray.json.*

import scala.concurrent.{ExecutionContext, Future}
import scala.io.{BufferedSource, Source}
import scala.reflect.io.Path
import scala.util.Using

class FileStorageImpl(baseDir: Path)(implicit ec: ExecutionContext) extends FileStorage {
  def getUser(userId: UserId): Future[Either[DomainError, User]] = {
    // retry is a custom implementation of retrying a Future, simple use-case but implementation is non-trivial.
    retry("getUser")(getUserImpl(userId))
      .map(Right(_))
      .recoverWith {
        // We want to catch this in getUserImpl but cannot, because retry only handles when Future fails
        // and not when it returns a Left
        case e => Future.successful(Left(NotFoundError(s"User with id $userId not found", e)))
      }
  }

  // Reading from a file does not need a Future, but it would in case we fetch from a database or cloud
  private def getUserImpl(userId: UserId): Future[User] = {
    val path = baseDir / s"user_$userId.json"
    println(s"Reading user $userId from file")
    Future.fromTry(Using(openSource(path))(parseUser))
  }

  def openSource(path: Path): BufferedSource = Source.fromInputStream(getClass.getResourceAsStream(path.toString()))

  def parseUser(source: BufferedSource): User = source.getLines().mkString.parseJson.convertTo[User]
}

object FileStorageImpl {

  import com.kognic.core.application.ThreadPools.Implicits.ioBoundExecutor

  private lazy val instance = new FileStorageImpl(Path("/."))

  def apply(): FileStorageImpl = instance
}
