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

package fixtures.messages

import fixtures.i18n

object ConfirmationMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String
    val heading: String
    val movementInfoH2: String
    val movementInfoArc: String
    val movementInfoDate: String
    val print: String
    val whatMayNeedDoH2: String
    val whatMayNeedDoP1: String
    val whatNextH2: String
    val whatNextP1: String
    val shortageH3: String
    val shortageP1: String
    val excessH3: String
    val excessP1: String
    val excessSameGoodsBullet1: String
    val excessSameGoodsBullet2: String
    val excessP2: String
    val excessDifferentGoodsBullet1: String
    val excessDifferentGoodsBullet2: String
    val feedback: String
    val returnToMovementLink: String
    val returnToAccountHomeLink: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val heading = "Explanation for a shortage or excess submitted"
    override val title: String = titleHelper(heading)
    override val movementInfoH2 = "Movement information"
    override val movementInfoArc = "Movement ARC"
    override val movementInfoDate = "Date of submission"
    override val print: String = "Print this screen to make a record of your submission."
    override val whatNextH2 = "What happens next"
    override val whatNextP1 = "The movement will be updated to show you have successfully submitted an explanation for shortage or excess. This may not be happen straight away."
    override val whatMayNeedDoH2 = "What you may need to do"
    override val whatMayNeedDoP1 = "This explanation for a shortage or excess has been submitted to HMRC only. If you are a Northern Ireland warehousekeeper receiving goods from an EU member state, this information will also be visible to the state of dispatch."
    override val shortageH3 = "If you recorded a shortage"
    override val shortageP1 = "A duty point may be created for any goods in this movement that are short. This means HMRC will require duty to be paid on those goods by the person or organisation that is holding them."
    override val excessH3 = "If you recorded an excess"
    override val excessP1 = "When the excess goods match what’s on the electronic administrative document (eAD) you must:"
    override val excessSameGoodsBullet1 = "keep a full audit trail of the onward movement of goods you do not keep"
    override val excessSameGoodsBullet2 = "accept the duty liability for any goods you keep"
    override val excessP2 = "When the excess goods do not match what’s on the eAD:"
    override val excessDifferentGoodsBullet1 = "a duty point will be created for those goods"
    override val excessDifferentGoodsBullet2 = "the person or organisation holding the goods must immediately pay the duty on them"
    override val feedback = "What did you think of this service? (opens in new tab) (takes 30 seconds)"
    override val returnToMovementLink: String = "Return to movement"
    override val returnToAccountHomeLink: String = "Return to account home"
  }

}
