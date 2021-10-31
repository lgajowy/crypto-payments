package com.lgajowy.tools

import java.time.Clock

object ClockProvider {
  implicit val clock: Clock = Clock.systemUTC()
}
