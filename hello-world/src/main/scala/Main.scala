object Main extends App {
  private def printMsg(): Unit = println("Hello World! From Scala:)")
  private val runPrintMsg = printMsg()

  // Showcases how printMsg is not referentially transparent
  // loop with runPrintMsg only prints once
  // https://blog.rockthejvm.com/referential-transparency/
  //  1.to(10).foreach(_ => printMsg())
  1.to(10).foreach(_ => runPrintMsg)

}
