# Compare Service Pattern
### Vanilla Scala
Let's start simple and then build on top of our vanilla Scala example. We
start with 3 files of interest `Main.scala`, `UserServiceImpl.scala` and `UserRepo.scala`:

```scala
trait UserRepo {
  def getUser(userId: UserId): Future[User]
}

class UserServiceImpl(userRepo: UserRepo)(implicit ec: ExecutionContext) {
  def getUsers(userIds: Seq[UserId]): Future[Seq[User]] =
    for {
      users <- Future.sequence(userIds.map(userRepo.getUser))
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
Go to the hello-service folder and try to compile the code using `sbt compile`, it works 
even though `FileStorageImpl` has not
implemented the `getUser` method! This is so that you can quickly create classes / services
working towards interfaces without having to implement the interfaces immediately.

Now try to run the code (either from terminal `sbt run` or IntelliJ), what happens?

### ZIO
Reminder:
*A `ZIO[R, E, A]` value is an immutable value that lazily describes a workflow or job. The
workflow requires some environment `R`, and may fail with an error of type `E`, or succeed
with a value of type `A`.*

Here we have a brief look at the [Service Pattern](https://zio.dev/reference/service-pattern/)
in ZIO.

We have the same three files as in vanilla Scala, but we have suffixed them with ZIO just to make it
very explicit in this case.

```scala
trait UserRepoZIO {
  def getUser(userId: UserId): ZIO[Any, Nothing, User]
}

case class UserServiceZIOImpl(userRepo: UserRepoZIO) extends UserServiceZIO {
  def getUsers(userIds: Seq[UserId]): ZIO[Any, Nothing, Seq[User]] =
    ZIO.foreachPar(userIds)(userRepo.getUser)
}
```
Here `Any` means that the method has no required environment, and `Nothing` means that
the method cannot fail (execpt in some very unexpected way, in that case the ZIO will 
[die](https://zio.dev/reference/core/cause/#die)). You may notice that  

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