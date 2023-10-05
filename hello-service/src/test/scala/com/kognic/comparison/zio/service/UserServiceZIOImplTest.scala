package com.kognic.comparison.zio.service

import com.kognic.comparison.Ids.UserId
import com.kognic.comparison.User
import com.kognic.comparison.zio.filestorage.FileStorageZIOMock
import zio.mock.{Expectation, MockReporter}
import zio.test.*

object UserServiceZIOImplTest extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment, Any] = suite("MainZIO")(
    // We test that the program actually outputs the message
    test("The message should be correct") {
      val fileStorageMock = FileStorageZIOMock.GetUser(
        assertion = Assertion.equalTo(UserId(1)),
        result = Expectation.value(
          User(UserId(1), "Jane", 66),
        )
      ) && FileStorageZIOMock.GetUser(
        assertion = Assertion.equalTo(UserId(2)),
        result = Expectation.value(
          User(UserId(2), "Joe", 33),
        )
      )

      val result = for {
        users <- UserServiceZIO.getUsers(Seq(UserId(1), UserId(2)))
      } yield assertTrue(users == Seq(
        User(UserId(1), "Jane", 66),
        User(UserId(2), "Joe", 33)
      ))

      result.provide(
        UserServiceZIOImpl.layer, // This layer has a dependency on FileStorageZIO, which we need to provide
        fileStorageMock) // We provide a mock for the FileStorageZIO dependency
    } @@ MockReporter()
  )
}
