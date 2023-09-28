import com.kognic.comparison.zio.MainZIO
import zio.test.{Spec, TestConsole, ZIOSpecDefault, assertTrue}

// Example of testing a ZIO Service coming up
//object MainZIOTest extends ZIOSpecDefault {
//  override def spec = suite("MainZIO")(
//    // We test that the program actually outputs the message
//    test("The message should be correct") {
//      for {
//        _ <- MainZIO.run
//        message <- TestConsole.output
//      } yield assertTrue(message == Vector("Hello World! From ZIO:)\n"))
//    }
//  )
//}
