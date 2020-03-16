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
import com.americanexpress.blockchain.maranhao.workflow.FlowContext
import com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowData
import com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowTracker.VerifyingTransaction


/**
 * simple flow verification step. Contract is called to make sure conditions are met
 */
class SimpleFlowTransactionVerifyStep<OUT> :
        com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowStep<OUT> {

    /**
     * overridden function called by flow iteratively
     * @param ctx FlowContext<SimpleFlowData>
     */

    @Suspendable
    override fun execute(
            ctx: com.americanexpress.blockchain.maranhao.workflow
            .FlowContext<com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowData, OUT>) {

        ctx.track(VerifyingTransaction)

        ctx.sharedData!!.transactionBuilder!!.verify(ctx.serviceHub)
    }
}
