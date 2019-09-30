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

import co.paralleluniverse.fibers.Suspendable
import com.americanexpress.blockchain.maranhao.NotaryStrategy
import net.corda.core.flows.FlowLogic
import net.corda.core.transactions.SignedTransaction

/**
 * Core flow implementation. Uses template pattern to gather steps, notary strategy, progress tracker
 * and business data from specific implementations.
 *
 * @param IN
 * @param CTX
 * @property input IN
 * @property progressTracker ProgressTracker
 * @constructor
 */
abstract class MultiStepFlowInitiator<IN, CTX>(val input: IN) : FlowLogic<SignedTransaction>() {

    @Suspendable
    abstract fun getFlowSteps(input: IN) : Array<com.americanexpress.blockchain.maranhao.workflow.FlowStep<CTX>>

    /**
     * By default uses FirstNotaryStrategy, unless overriden
     *
     * @return NotaryStrategy
     */
    open fun getNotaryStrategy() : NotaryStrategy =
            com.americanexpress.blockchain.maranhao.workflow.FirstNotaryStrategy()

    /**
     * Must implement this method to return FlowTracker
     * @return com.americanexpress.blockchain.maranhao.workflow.FlowTracker
     */
    abstract fun getFlowTracker() : com.americanexpress.blockchain.maranhao.workflow.FlowTracker

    override val progressTracker = getFlowTracker().progressTracker()

    /**
     * Implementation of abstract method in FlowLogic
     * @return SignedTransaction
     */
    @Suspendable
    override fun call(): SignedTransaction {
        val ctx = com.americanexpress.blockchain.maranhao.workflow.FlowContext<CTX>(getNotaryStrategy()
                .getNotary(serviceHub), this, serviceHub)

        val flowSteps = getFlowSteps(input)

        flowSteps.forEach {
            it.execute(ctx)
        }

        val finalFlowStep = flowSteps.last() as com.americanexpress.blockchain.maranhao.workflow.FinalFlowStep<CTX>
        return finalFlowStep.getFinalSignedTransaction()
    }
}

