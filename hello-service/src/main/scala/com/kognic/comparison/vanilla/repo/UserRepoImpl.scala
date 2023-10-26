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
  def getUser(userId: UserId): Future[Either[DomainError, User]] = {
    val path = baseDir / s"user_$userId.json"
    println(s"Reading user $userId from file")
    /*
     We would prefer not to catch all errors here (we have the same problem of what to catch from the future again),
     and instead send Eithers directly from openSource and parseUser. This is a hassle though since I cannot use
     Using then. So this is a bit less correct but more convenient.
     */
    Future.fromTry(Using(openSource(path))(parseUser))
      .map(Right(_))
      .recover {
        case e: NullPointerException => Left(UserNotFoundError.fromException(e))
        case e: ParsingException => Left(JsonParseError.fromException(e))
      }
  }

  private def openSource(path: Path): BufferedSource = Source.fromInputStream(getClass.getResourceAsStream(path.toString()))

  private def parseUser(source: BufferedSource): User = source.getLines().mkString.parseJson.convertTo[User]
}

object UserRepoImpl {
  import scala.concurrent.ExecutionContext.Implicits.global

  private lazy val instance = new UserRepoImpl(Path("/."))

  def apply(): UserRepoImpl = instance
}
