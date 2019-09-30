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
import net.corda.core.contracts.Command
import net.corda.core.transactions.TransactionBuilder

/**
 * simple flow initial step of generating transaction builder and then passing it along
 * to subsequent steps.
 *
 * @property stateId String - package name
 * @property state ContractState - populated state object
 * @property commandData CommandData - command
 * @property signatories List<Party> - list of parties that need to sign off
 * @constructor
 */
object SimpleFlowTxGeneratorStep : com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowStep {

    /**
     * overridden function called by flow iteratively
     * @param ctx FlowContext<SimpleFlowData>
     */

    @Suspendable
    override fun execute(
            ctx: com.americanexpress.blockchain.maranhao.workflow
            .FlowContext<com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowData>) {

        ctx.track(com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowTracker.GeneratingTransaction)

        var owningKeys = ctx.sharedData!!.signatories.map { it.owningKey }.toMutableList()
        owningKeys.add(ctx.workFlow.ourIdentity.owningKey)

        val txCommand = Command(ctx.sharedData!!.commandData, owningKeys)
        val txBuilder = TransactionBuilder(ctx.notary)

        if (ctx.sharedData!!.stateAndRef != null) {
            txBuilder.addInputState(ctx.sharedData!!.stateAndRef!!)
        }

        if (ctx.sharedData!!.referenceState != null) {
            txBuilder.addReferenceState(ctx.sharedData!!.referenceState!!)
        }

        if (ctx.sharedData!!.attachment != null) {
            txBuilder.addAttachment(ctx.sharedData!!.attachment!!)
        }

        if (ctx.sharedData!!.outputState != null) {
            txBuilder.addOutputState(ctx.sharedData!!.outputState!!, ctx.sharedData!!.stateId)
        }

        txBuilder.addCommand(txCommand)

        ctx.sharedData!!.transactionBuilder = txBuilder
    }
}
