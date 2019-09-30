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

package com.americanexpress.blockchain.maranhao.workflow

import net.corda.core.flows.FlowLogic
import net.corda.core.identity.Party
import net.corda.core.node.ServiceHub
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.ProgressTracker

/**
 * Object of this type to be passed from step to step
 *
 * @param CTX
 * @property notary Party
 * @property workFlow FlowLogic<SignedTransaction>
 * @property serviceHub ServiceHub
 * @property sharedData CTX?
 * @constructor
 */
open class FlowContext<CTX>(
        var notary: Party,
        var workFlow: FlowLogic<SignedTransaction>,
        var serviceHub: ServiceHub,
        var sharedData: CTX? = null
) {
    /**
     * Helper function to set workflow progress tracker
     * @param step ProgressTracker.Step
     */
    open fun track(step: ProgressTracker.Step) {
        this.workFlow.progressTracker!!.currentStep = step
    }
}
