import zio.ZIO
import zio.test.{Spec, TestConsole, ZIOSpecDefault, assertTrue}

import java.io.IOException

object MainZIOTest extends ZIOSpecDefault {
  override def spec: Spec[Any, IOException] = suite("MainZIO")(
    // We test that the program actually outputs the message every time.
    // Works because ZIO is referentially transparent
    // https://blog.rockthejvm.com/referential-transparency/
    test("The output should be correct Example 1") {
      val program = HelloWorldZIO.program
      for {
        _ <- ZIO.foreachDiscard(1.to(5))(_ => program)
        consoleOutputs <- TestConsole.output
      } yield assertTrue(consoleOutputs.head == "Hello World! From ZIO:)\n",
        consoleOutputs.length == 5
      )
    },
    test("The output should be correct Example 2") {
      for {
        _ <- ZIO.foreachDiscard(1.to(5))(_ => HelloWorldZIO.program)
        consoleOutputs <- TestConsole.output
      } yield assertTrue(consoleOutputs.head == "Hello World! From ZIO:)\n",
        consoleOutputs.length == 5
      )
    }
  )
}
