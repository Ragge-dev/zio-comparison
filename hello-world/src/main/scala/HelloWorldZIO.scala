import zio.{Console, ExitCode, ZIO, ZIOAppDefault}

import java.io.IOException

object HelloWorldZIO extends ZIOAppDefault {
  override def run: ZIO[Any, IOException, Unit] = program

  def program: ZIO[Any, IOException, Unit] = Console.printLine("Hello World! From ZIO:)")
}
