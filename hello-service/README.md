# Services and business logic

## Compare Service Pattern
Now we have properly implemented the `getUser` method in `UserRepo` implementation. Lets compare
the two implementations. Run the Main class in the `vanilla` package and the `zio` package by 
clicking the green arrow in IntelliJ. You need to run it like this because of how I have 
taken a shortcut regarding how I fetch the resources (jsons containing users).

Both programs should be able to run, but what happens if you add a new userId to the list
which does not exist?

### Vanilla Scala
Take a look at the `UserRepoImpl` implementation.
```scala
class UserRepoImpl(baseDir: Path) extends UserRepo {
  def getUser(userId: UserId): Future[User] = {
    val path = baseDir / s"user_$userId.json"
    println(s"Reading user $userId from file")
    Future.fromTry(Using(openSource(path))(parseUser))
  }
  private def openSource(path: Path): BufferedSource = Source.fromInputStream(getClass.getResourceAsStream(path.toString()))
  private def parseUser(source: BufferedSource): User = source.getLines().mkString.parseJson.convertTo[User]
}
```
Quite compact! In this example we just want to parse the content of the file immediately and then
close the file. In this case we can use `Using` construct for automatic resource management. If 
we needed to use the resource for a longer time we would need to use try / finally, you
can try it out as an exercise.

There is one major problem with this implementation, can you catch it?

The problem is that `getUser` can fail in a few predictable ways.
`Using` produce a Try which can fail with a `NullPointerException` if 
the file does not exist and a `DeserializationError` if it cannot parse the content of the file.
This is then converted to the `Future` by `Future.fromTry`. This is not at all 
transparent in the code, and especially so for anyone
calling this method. Imagine that we have a larger program than a few files, we would have to be 
omniscient to know what errors our program can throw.
Thus it becomes tempting to always handle any errors where they occur (e.g. using try / catch)
and not let some
higher level code handle them. We'll see how we can tackle this in the next session.

### ZIO
Reminder:
*A `ZIO[R, E, A]` value is an immutable value that lazily describes a workflow or job. The
workflow requires some environment `R`, and may fail with an error of type `E`, or succeed
with a value of type `A`.*

The ZIO implementation at this stage looks very similar to the vanilla one:
```scala

case class UserRepoZIOImpl(baseDir: Path) extends UserRepoZIO {
  def getUser(userId: UserId): ZIO[Any, Throwable, User] = {
    val path = baseDir / s"user_$userId.json"
    Console.printLine(s"Reading user $userId from file") *>
      ZIO.acquireReleaseWith(openSource(path))(closeSource)(parseUser)
  }
  
  def parseUser(source: BufferedSource): ZIO[Any, Throwable, User] =
    ZIO.attempt(source.getLines().mkString.parseJson.convertTo[User])

  private def openSource(path: Path): ZIO[Any, Throwable, BufferedSource] =
    ZIO.attempt(Source.fromInputStream(getClass.getResourceAsStream(path.toString())))
  
  private def closeSource(source: BufferedSource): ZIO[Any, Nothing, Unit] = ZIO.succeed(source.close())
}
```
The implementation is slightly better than the vanilla one since `getUser` returns a `ZIO` 
which can fail with a `Throwable`, so it is clear for anyone that the method can 
fail. We were forced to change the interface `getUser` from not throwing any errors, 
to actually returning a `Throwable` when we implemented the method.

`Zio.acquireReleaseWith` is a method similar to `Using`, which is good when you want to
use a resource immediately after opening it, but every parameter needs to be a ZIO.
Why is that?
It's because ZIO:s
*describe* effects (programs) we want to execute. In this case, we have one ZIO describing
how we want to
open the resource, one for how we are going to parse the resource and one for how we are
going to close it.

You can try doing a similar thing for the vanilla case (wrap open and parse functions in a
`Try` or `try / catch`).
It will become apparent that it is not as easy to compose the different functions
as it is with ZIO.

The ZIO case is far 
from great though since we still have the same problem as in the vanilla case, we don't know
with what errors the method can fail. We would again have to be omniscient and pattern 
match for 
all possible errors (or handle errors equally) if we want to handle them in the calling code.

## Typed Errors
When we implemented the `getUser` method in the vanilla case we did not have to change
anything in the interface, even though we created an implementation which can throw 
errors. In ZIO we had to change the interface to return a `ZIO` which can fail.

Next we want to make it clear for both implementations what errors our program 
can fail with. Have a look in `service-pattern-3`.

```bash
git checkout service-pattern-3
```