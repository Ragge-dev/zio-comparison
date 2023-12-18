# Services and business logic

## Compare Service Pattern
Now we have properly implemented typed errors in our code. Lets compare
the two implementations. Run the Main class in the `vanilla` package and the `zio` package by 
clicking the green arrow in IntelliJ. You need to run it like this because of how I have 
taken a shortcut regarding how I fetch the resources (jsons containing users).

Both programs should be able to run, and if you add a userId which does not exist, or mess 
with a json file you will not get a stacktrace. Instead you will get a simple print, which
is simply to show how you can handle errors at the edge of your program.

### Vanilla Scala
Instead of having all of the errors baked into the Future we instead keep unexpected errors
in the Future and the expected errors in the Either. And this works! But it is not very
ergonomic. Biggest problem is that we have to manually lift errors out of the Sequence in
`UserServiceImpl`, and that Eithers are not modelled to have the Left as 
an error channel.

This could be fixed by using some other effect system which more ergonomically can handle 
errors than trying to do it with Futures and Eithers. Two examples I know of are Monix and
ZIO. 

### ZIO
Reminder:
*A `ZIO[R, E, A]` value is an immutable value that lazily describes a workflow or job. The
workflow requires some environment `R`, and may fail with an error of type `E`, or succeed
with a value of type `A`.*

ZIO is entirely built around typing errors, in fact it's error model is
[lossless](https://zio.dev/reference/core/cause) which means that nothing related to the
error is lost.
Since Futures and Eithers are not built for typed errors this is not
a very fair comparison. It does show though that if you want to ergonomically use 
type errors in your program you need something more than vanilla scala. 

## Other features we will look at next
  - Integration with Futures
  - Resource Management
  - Retries
