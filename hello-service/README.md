# Services and business logic

## Compare Service Pattern
Now we added typed errors to our service methods. We will use where `DomainError` is a sealed 
trait that represents all the errors that can occur in our domain.

```scala
abstract class DomainError(msg: String, cause: Throwable) extends Throwable(msg, cause) {
  override def getMessage: String = msg
}
object DomainError {
  case class NotFoundError(msg: String, cause: Throwable) extends DomainError(msg, cause)
  case class IOError(msg: String, cause: Throwable) extends DomainError(msg, cause)
}
```

### Vanilla Scala
A common way to model errors in scala is by using scala is to use `Either[ErrorType, SuccessType]`. 
This changes our services like so:

```scala
trait FileStorage {
  def getUser(userId: UserId): Future[Either[DomainError, User]]
}

class UserService(fileStorage: FileStorage)(implicit ec: ExecutionContext) {
  def getUsers(userIds: Seq[UserId]): Future[Either[DomainError, Seq[User]]] = ???
} 
```
Now we have explicitly stated that our service can fail with a `DomainError`, and these
errors are something we need to handle upstream when calling these methods. Have a look 
in `FileStorageImpl.scala`, `UserService.scala` and `Main.scala` to see how 
they are implemented. 

### ZIO
Reminder:
*A `ZIO[R, E, A]` value is an immutable value that lazily describes a workflow or job. The
workflow requires some environment `R`, and may fail with an error of type `E`, or succeed
with a value of type `A`.*

In the ZIO case the error is already typed in the ZIO, we simply add DomainError to show
that is the type of error we can expect.
```scala
trait UserServiceZIO {
  def getUsers(userIds: Seq[UserId]): ZIO[Any, DomainError, Seq[User]]
}

trait UserServiceZIO {
  def getUsers(userIds: Seq[UserId]): ZIO[Any, DomainError, Seq[User]]
}
```

Here `Any` means that the method has no requirements for the environment.

Have a look in `FileStorageZIOImpl.scala`, `UserServiceZIO.scala` and `MainZIO.scala` to see how
there are implemented. There is some 
In order to start simple and look at parts at a time, checkout the branch `service-pattern` using:
```bash
git checkout service-pattern
```

if you're not interested in that you can look at the finished result in here.
In this package we have a look at the Service Pattern in ZIO, and compare that 
to how we usually create services at Kognic. 

The two services are actually very similar. The only real differences being 
the new `Zlayer` syntax of ZIO, and that ZIO has a built in way to lift errors from 
a sequence.
