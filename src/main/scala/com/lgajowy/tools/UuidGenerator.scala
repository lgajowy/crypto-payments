package com.lgajowy.tools

import java.util.UUID

trait UuidGenerator {
  def generate(): UUID
}

object UuidGenerator {
  implicit val defaultGenerator = new UuidGenerator {
    override def generate(): UUID = UUID.randomUUID()
  }
}
