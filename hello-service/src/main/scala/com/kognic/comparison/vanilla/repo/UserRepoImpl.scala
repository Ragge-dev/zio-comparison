package com.kognic.comparison.vanilla.repo

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.User
import spray.json.*

import scala.concurrent.Future
import scala.io.{BufferedSource, Source}
import scala.reflect.io.Path
import scala.util.Using

class UserRepoImpl(baseDir: Path) extends UserRepo {
  def getUser(userId: UserId): Future[User] = {
    val path = baseDir / s"user_$userId.json"
    println(s"Reading user $userId from file")
    /* In this case, reading from a file does not need a Future. But if the implementation would read from a
     * database we would need a Future.
     */
    Future.fromTry(Using(openSource(path))(parseUser))
  }

  private def openSource(path: Path): BufferedSource = Source.fromInputStream(getClass.getResourceAsStream(path.toString()))

  private def parseUser(source: BufferedSource): User = source.getLines().mkString.parseJson.convertTo[User]
}

object UserRepoImpl {

  private lazy val instance = new UserRepoImpl(Path("/."))

  def apply(): UserRepoImpl = instance
}
