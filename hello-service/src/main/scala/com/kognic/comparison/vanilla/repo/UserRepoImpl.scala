package com.kognic.comparison.vanilla.repo

import com.kognic.comparison.DomainError.{JsonParseError, UserNotFoundError}
import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.{DomainError, User}
import spray.json.*
import spray.json.JsonParser.ParsingException

import scala.concurrent.{ExecutionContext, Future}
import scala.io.{BufferedSource, Source}
import scala.reflect.io.Path
import scala.util.Using

class UserRepoImpl(baseDir: Path)(implicit e: ExecutionContext) extends UserRepo {
  def getUser(userId: UserId): Future[User] = {
    val path = baseDir / s"user_$userId.json"
    println(s"Reading user $userId from file")
    Future.fromTry(Using(openSource(path))(parseUser))
      .recoverWith {
        case e: NullPointerException => Future.failed(UserNotFoundError.fromException(e))
        case e: ParsingException => Future.failed(JsonParseError.fromException(e))
      }
  }

  private def openSource(path: Path): BufferedSource = Source.fromInputStream(getClass.getResourceAsStream(path.toString()))

  private def parseUser(source: BufferedSource): User = source.getLines().mkString.parseJson.convertTo[User]
}
