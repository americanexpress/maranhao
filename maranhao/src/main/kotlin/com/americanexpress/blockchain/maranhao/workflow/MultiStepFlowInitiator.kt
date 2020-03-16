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
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowLogic
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
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
abstract class MultiStepFlowInitiator<IN, CTX, OUT>(val input: IN) : FlowLogic<OUT>() {

    @Suspendable
    abstract fun getFlowSteps(input: IN) : Array<FlowStep<CTX, OUT>>

    /**
     * By default uses FirstNotaryStrategy, unless overriden
     *
     * @return NotaryStrategy
     */
    open fun getNotaryStrategy() : NotaryStrategy = FirstNotaryStrategy()

    /**
     * By default uses DefaultLogStep. Should be overwritten, especially since the context
     * sharedData may contain sensitive info.
     *
     * @return LogStep<CTX, OUT>
     */

    open fun getLog(): Log<CTX, OUT> = DefaultLog()

    /**
     * Must implement this method to return FlowTracker
     * @return com.americanexpress.blockchain.maranhao.workflow.FlowTracker
     */
    abstract fun getFlowTracker() : FlowTracker

    override val progressTracker = getFlowTracker().progressTracker()

    /**
     * Function to fetch unconsumed linear state from vault. Flows will invoke this function to consume the 'unspend' state
     * @param linearId LinearId of the state to query for
     * @return StateAndRef<T>?
     *
     */
    inline fun <reified T: LinearState> getUnconsumedLinearStateByLinearId(linearId: UniqueIdentifier): StateAndRef<T>? {
        val queryCriteria = QueryCriteria.LinearStateQueryCriteria(
                linearId = listOf(linearId),
                status = Vault.StateStatus.UNCONSUMED
        )
        return serviceHub.vaultService.queryBy<T>(queryCriteria).states.singleOrNull()
    }

    abstract fun returnValue(): OUT

    var signedTransaction: SignedTransaction? = null

    /**
     * Implementation of abstract method in FlowLogic
     * @return SignedTransaction
     */
    @Suspendable
    override fun call(): OUT {
        val ctx = FlowContext<CTX, OUT>(getNotaryStrategy()
                .getNotary(serviceHub), this, serviceHub)

        val logStep = getLog()
        val flowSteps = getFlowSteps(input)

        flowSteps.forEach {
            it.execute(ctx)
            logger.info(logStep.logMessage(ctx))
        }

        val finalFlowStep = flowSteps.last() as FinalFlowStep<CTX, OUT>
        logger.info(logStep.logMessage(ctx))
        signedTransaction = finalFlowStep.getFinalSignedTransaction()

        return returnValue()
    }
}

