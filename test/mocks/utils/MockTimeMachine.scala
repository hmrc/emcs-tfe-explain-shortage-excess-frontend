/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mocks.utils

import org.scalamock.handlers.CallHandler0
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import utils.TimeMachine

import java.time.LocalDateTime

trait MockTimeMachine extends MockFactory {

  val mockTimeMachine: TimeMachine = mock[TimeMachine]

  object MockTimeMachine extends Matchers {

    def now(ldt: LocalDateTime): CallHandler0[LocalDateTime] =
      (() => mockTimeMachine.now()).expects().returns(ldt)

  }
}
