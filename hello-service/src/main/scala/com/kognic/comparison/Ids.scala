package com.kognic.comparison

import com.kognic.core.tagging.TaggedType.{@@, Tag}
object Ids {

  type UserIdTag = "UserId"
  type UserId = Int @@ UserIdTag
  def UserId(id: Int): UserId = Tag[Int, UserIdTag](id)

}
