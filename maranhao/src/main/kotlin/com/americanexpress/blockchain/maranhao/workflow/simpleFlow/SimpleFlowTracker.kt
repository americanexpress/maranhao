/**
 * Copyright 2019 American Express Travel Related Services Company, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.americanexpress.blockchain.maranhao.workflow.simpleFlow

import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.utilities.ProgressTracker
import com.americanexpress.blockchain.maranhao.workflow.FlowTracker

/**
 * collection of ProgressTracker objects to be used by individual steps in a simple flow
 */
object SimpleFlowTracker : FlowTracker {
    object GeneratingTransaction :
            ProgressTracker.Step("Generating transaction.")
    object VerifyingTransaction :
            ProgressTracker.Step("Verifying contract constraints.")
    object SigningTransaction :
            ProgressTracker.Step("Signing transaction with our private key.")
    object GatheringSignatures :
            ProgressTracker.Step("Gathering the counterparty's signature.") {
        override fun childProgressTracker() = CollectSignaturesFlow.tracker()
    }

    object FinalizingTransaction : ProgressTracker.Step(
            "Obtaining notary signature and recording transaction.") {
        override fun childProgressTracker() = FinalityFlow.tracker()
    }

    override fun progressTracker() = ProgressTracker(
            GeneratingTransaction,
            VerifyingTransaction,
            SigningTransaction,
            GatheringSignatures,
            FinalizingTransaction
    )
}
