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

import com.americanexpress.blockchain.maranhao.workflow.FlowContext
import com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowData
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.TimeWindow
import net.corda.core.identity.Party

class SimpleFlowInitializerStep<OUT>(stateId : String,
                                state: ContractState?,
                                commandData: CommandData,
                                signatories: List<Party>,
                                timeWindow: TimeWindow?,
                                val sharedData: SimpleFlowData = SimpleFlowData(stateId,
                                        outputState = state, commandData = commandData,

                                        signatories = signatories, timeWindow = timeWindow)) :
        com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowStep<OUT> {

    /**
     * implementation of SimpleFlowStep
     *
     * @param ctx com.americanexpress.blockchain.maranhao.workflow
    .FlowContext<com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowData>
     */
    override fun execute(ctx: com.americanexpress.blockchain.maranhao.workflow
    .FlowContext<com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowData, OUT>) {

        ctx.sharedData = sharedData
    }
}
