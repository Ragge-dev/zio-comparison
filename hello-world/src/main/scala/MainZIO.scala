import zio.{Console, ExitCode, ZIO, ZIOAppDefault}

import java.io.IOException

object MainZIO extends ZIOAppDefault {
  override def run = program

  def program: ZIO[Any, IOException, Unit] = Console.printLine("Hello World! From ZIO:)")
}
