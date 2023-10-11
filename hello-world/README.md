## Hello World

### Your first ZIO program

Shows the very basic of how a program is created using regular Scala or ZIO.
When talking about Scala without ZIO the term "Vanilla" will be used from
time to time.

## Vanilla Main

Our first program is a simple "Hello World" program. Just a few rows:

```scala
object Main extends App {
  private def printMsg(): Unit = println("Hello World! From Scala:)")
  private val runPrintMsg = printMsg()
  
  1.to(10).foreach(_ => printMsg()) // Example 1
  // 1.to(10).foreach(_ => runPrintMsg) // Example 2
}
```
Not only are we printing hello world though, but we are also showcasing
something which recurrs in Scala if we don't handle it explicitly. Printing
someting to the console is a side effect, and we are doing it several times.
However in example one the message is printed 10 times, while in example 
two it is only printed once. 

This is an example of the `printMsg` method 
not being [referentially transparent](https://blog.rockthejvm.com/referential-transparency/) 
(Link to a good article by Rock the JVM). In short the method behaves differently 
depending on how it is used, which could easily lead to bugs without constance vigilance! 
(Mad Eye Moody anyone?)

## ZIO Main
[Introduction](https://zio.dev/reference/core/zio/) to the ZIO type. In short:

*A `ZIO[R, E, A]` value is an immutable value that lazily describes a workflow or job. 
The workflow requires some environment `R`, and may fail with an error of type `E`, 
or succeed with a value of type `A`.*

Our first program in ZIO has a little different syntax:
```scala

object MainZIO extends ZIOAppDefault {
  override def run: ZIO[Any, IOException, Unit] = program
  def program: ZIO[Any, IOException, Unit] = Console.printLine("Hello World! From ZIO:)")
}
```

Where `Console` is a companion object provided by the zio package. 
ZIO has a `run` method you need to override, which acts as the main function. Just for clarity
I usually define my actual program in a method with the same name, it does little here but can 
help in more complex cases. The biggest difference otherwise is that when you print something 
using ZIO it returns a `ZIO` type, which can fail with `IOException` or succeed returning a `Unit`.

### Testing
Also have a look in the test file where we are able to test what the program prints to the console, not
at all trivial in regular Scala! We also show there that not matter how you call the ZIO code it will
return the same result.
