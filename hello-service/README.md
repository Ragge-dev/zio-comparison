# Compare Service Pattern
### Vanilla Scala
Let's start simple and then build on top of our vanilla Scala example. We
start with 3 files of interest `Main.scala`, `UserServiceImpl.scala` and `FileStorage.scala`:
```scala
trait FileStorage {
  def getUser(userId: UserId): Future[User]
}

class UserServiceImpl(fileStorage: FileStorage)(implicit ec: ExecutionContext) {
  def getUsers(userIds: Seq[UserId]): Future[Seq[User]] =
    for {
      users <- Future.sequence(userIds.map(fileStorage.getUser))
    } yield users
}
```
`FileStorage` has a method to fetch a single user, and `UserService` has a method to
fetch multiple users. In `Main.scala` we fetch the users and print them:
```scala
object Main extends App {
  private val userService = UserServiceImpl()
  val userIds = Seq(7, 1, 2, 3, 4, 5, 6).map(id => UserId(id))
  private def handleUsers(users: Seq[User]): Unit = users.foreach(println)

  private val result = for {
    users <- userService.getUsers(userIds)
  } yield handleUsers(users)
  result.map(_ => System.exit(0))
}
```
However, the code now does not show us if we have to handle any errors from `FileStorage`. All we have
is a Future, which can fail with a fatal or non-fatal `Throwable` error. Many non-fatal errors can be 
expected and would be good to know about for anyone calling that method. We might want to handle 
certain errors, act on some, ignore others and let some continue to bubble up. Also, we want to
know if we don't have to worry about errors!

### ZIO
Reminder:
*A `ZIO[R, E, A]` value is an immutable value that lazily describes a workflow or job. The
workflow requires some environment `R`, and may fail with an error of type `E`, or succeed
with a value of type `A`.*

Here we have a brief look at the [Service Pattern](https://zio.dev/reference/service-pattern/)
in ZIO.

We have the same three files as in vanilla Scala, but we have suffixed them with ZIO just to make it
very explicit in this case. In the vanilla case we did not need to have a trait for the `UserServiceImpl`
class (even though it is good practice to use), but in ZIO we need to have a trait because of
how we call the method from the `MainZIO` object.
```scala
trait UserServiceZIO {
  def getUsers(userIds: Seq[UserId]): ZIO[Any, Nothing, Seq[User]]
}

object UserServiceZIO {
  // For an explanation of this funciton, see the actual code
  def getUsers(userIds: Seq[UserId]): ZIO[UserServiceZIO, Nothing, Seq[User]] =
    ZIO.serviceWithZIO(_.getUsers(userIds))
}
```

Here `Any` means that the method has no requirements for the environment, and `Nothing` means that
the method cannot fail (execpt in some very unexpected way, in that case the ZIO will 
[die](https://zio.dev/reference/core/cause/#die)). 

In `MainZIO.scala` we have the following code:
```scala
  private def program: ZIO[UserServiceZIO, Nothing, Unit] =
  for {
    users <- UserServiceZIO.getUsers(userIds)
    _ <- ZIO.foreachDiscard(users)(a => printUser(a))
  } yield ()

private def printUser(user: User): ZIO[Any, Nothing, Unit] =
  Console.printLine(user).orDie
```

To have it mirror the vanilla code we don't handle any errors, in the ZIO case `Console.printLine`
can fail with `IOException`. We add `.orDie` to say that if it does not succeed we want the program 
to crash. 


## Next
As mentioned earlier it can be beneficent to type errors, we use it to great effect already in our code in e.g.
the `ResponseConverter` trait in scala-common. In order to have a look at how we can solve this, checkout the branch 
`service-pattern-2`:
```bash
git checkout service-pattern-2
```