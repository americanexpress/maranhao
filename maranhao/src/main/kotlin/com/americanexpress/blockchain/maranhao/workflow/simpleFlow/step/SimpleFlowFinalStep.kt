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

package com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FinalityFlow
import net.corda.core.transactions.SignedTransaction

/**
 *
 * Last step when FinalityFlow is called.
 * @property signedTransaction SignedTransaction? just for testing. Should not be assigned value during runtime
 */
object SimpleFlowFinalStep : com.americanexpress.blockchain.maranhao.workflow
    .FinalFlowStep<com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowData> {

    var signedTransaction: SignedTransaction? = null

    /**
     * Implementation of FinalFlowStep
     *
     * @param ctx com.americanexpress.blockchain.maranhao.workflow
    .FlowContext<com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowData>
     */
    @Suspendable
    override fun execute(ctx: com.americanexpress.blockchain.maranhao.workflow
    .FlowContext<com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowData>) {

        ctx.track(com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowTracker.FinalizingTransaction)
        com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowFinalStep.signedTransaction =
                ctx.workFlow.subFlow(

                FinalityFlow(
                        ctx.sharedData!!.fullySignedTransaction!!,
                        ctx.sharedData!!.flowSessions!!.toSet(),
                        com.americanexpress.blockchain.maranhao.workflow.simpleFlow
                                .SimpleFlowTracker.FinalizingTransaction.childProgressTracker()))
    }

    /**
     * Workflow implementing MultiStepFlowInitiator will return the SignedTransaction from this method
     * in the overridden call function
     *
     * @return SignedTransaction
     */
    @Suspendable override fun getFinalSignedTransaction(): SignedTransaction {
        return com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowFinalStep.signedTransaction!!
    }
}
