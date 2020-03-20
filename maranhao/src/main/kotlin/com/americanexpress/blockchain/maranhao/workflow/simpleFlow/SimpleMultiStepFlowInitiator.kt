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

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.TimeWindow
import net.corda.core.identity.Party
import com.americanexpress.blockchain.maranhao.workflow.MultiStepFlowInitiator
import com.americanexpress.blockchain.maranhao.workflow.simpleFlow.logging.SimpleLog

/**
 * simple flow implementation
 * @param IN - typically a business object (e.g. a Loan)
 * @constructor
 */
abstract class SimpleMultiStepFlowInitiator<IN, OUT>(input: IN) : com.americanexpress.blockchain.maranhao.workflow
    .MultiStepFlowInitiator<IN, SimpleFlowData, OUT>(input) {

    override fun getFlowTracker() = SimpleFlowTracker

    /**
     * By default uses DefaultLogStep. Should be overwritten, especially since the context
     * sharedData may contain sensitive info.
     *
     * @return LogStep<CTX, OUT>
     */

    override fun getLog() = SimpleLog<OUT>()


    /**
     * list of signers participating in this flow. If any is missing, workflow will throw an error
     * @return List<Party>
     */
    abstract fun getListOfSigners() : List<Party>

    /**
     * An identifier string for the state (e.g. package name)
     * @return String
     */
    abstract fun getStateId() : String

    /**
     * Implementation must return a valid state based on arguments passed in to constructor (<IN>)
     * @return ContractState
     */
    open fun getState() : ContractState? = null

    /**
     * The commmand data (only one in case of simple flow
     * @return CommandData
     */
    abstract fun getCommandData() : CommandData

    /**
     * TimeWindow: If specified in the [TransactionBuilder] notary acts as a timestamp authority for that transaction
     * @return TimeWindow
     */
    open fun getTimeWindow() : TimeWindow? = null

    /**
     * Base class needs the initiated flow steps, so that it can iterate over them
     *
     * @param input IN
     * @return Array<FlowStep<SimpleFlowData>>
     */
    @Suspendable
    override fun getFlowSteps(input: IN): Array<com.americanexpress.blockchain.maranhao.workflow
        .FlowStep<SimpleFlowData, OUT>> {

        return arrayOf(
                com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step
                        .SimpleFlowInitializerStep<OUT>(getStateId(), getState(),
                        getCommandData(), getListOfSigners(), getTimeWindow()), initialProcessingStep,
                com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowTxGeneratorStep(),
                com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowTransactionVerifyStep(),
                com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowSignStep(),
                com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowGatherSignaturesStep(),
                com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowFinalStep()
        )
    }

    /**
     * Any implementation in need to do extra processing can override this step
     */

    open var initialProcessingStep: com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step
    .SimpleFlowStep<OUT> = object: com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step
    .SimpleFlowStep<OUT> {
        override fun execute(ctx: com.americanexpress.blockchain.maranhao.workflow
            .FlowContext<SimpleFlowData, OUT>) {
            // this is a NOP implementation
        }
    }
}
