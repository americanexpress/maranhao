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
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.TimeWindow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.node.ServiceHub
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.seconds
import org.mockito.Mockito
import javax.annotation.Signed

val party = Mockito.mock(Party::class.java)
val signatories = listOf(party)
val state = Mockito.mock(ContractState::class.java)
val serviceHub = Mockito.mock(ServiceHub::class.java)
val signedTransaction = Mockito.mock(SignedTransaction::class.java)
val commandData = Mockito.mock(CommandData::class.java)
val partyName = CordaX500Name("Alice", "New York", "US")
val flowContext = FlowContext(party, DummyFlow(), serviceHub, DummyCtx())
val transactionBuilder = Mockito.mock(TransactionBuilder::class.java)
val flowSession = Mockito.mock(FlowSession::class.java)
val timeWindow = Mockito.mock(TimeWindow::class.java)


val simpleFlowDummyData = com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowData(stateId = "dummy",
        inputState = state, commandData = commandData,
        signatories = signatories, transactionBuilder = transactionBuilder,
        signedTransaction = signedTransaction, fullySignedTransaction = signedTransaction,
        flowSessions = listOf(flowSession), timeWindow = timeWindow)

class DummyFlow : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        return Mockito.mock(SignedTransaction::class.java)
    }

    override val progressTracker = com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowTracker.progressTracker()
}

class DummyCtx {
    var dummyValue: Int = 0
}

class DummyData {
    var dummyValue: Int = 0
}

class DummyStep : com.americanexpress.blockchain.maranhao.workflow.FinalFlowStep<DummyCtx, SignedTransaction> {
    override fun execute(ctx: com.americanexpress.blockchain.maranhao.workflow.FlowContext<DummyCtx, SignedTransaction>) {
    }

    override fun getFinalSignedTransaction(): SignedTransaction {
        return signedTransaction
    }
}

class TestMultiStepFlowInitiator(input: DummyData) : com.americanexpress.blockchain.maranhao.workflow
    .MultiStepFlowInitiator<DummyData, DummyCtx, SignedTransaction>(input) {

    override fun returnValue(): SignedTransaction {
        return signedTransaction!!
    }

    override fun getFlowTracker(): FlowTracker {
        return com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowTracker
    }

    fun getListOfSigners() : List<Party> = listOf(party)
    fun getStateId() : String = "dummy"
    fun getState() : ContractState = state
    fun getCommandData() : CommandData = commandData

    @Suspendable
    override fun getFlowSteps(input: DummyData): Array<com.americanexpress.blockchain.maranhao.workflow.FlowStep<DummyCtx, SignedTransaction>> {
        return arrayOf(DummyStep())
    }
}

class TestSimpleFlow(input: DummyData) : com.americanexpress.blockchain.maranhao.workflow.simpleFlow
    .SimpleMultiStepFlowInitiator<DummyData, SignedTransaction>(input) {

    override fun getListOfSigners(): List<Party> = listOf(party)
    override fun getStateId() : String = "dummy"
    override fun getState() : ContractState = state
    override fun getCommandData() : CommandData = commandData
    override fun getTimeWindow(): TimeWindow? = timeWindow

    override fun returnValue(): SignedTransaction {
        return signedTransaction!!
    }
}

val simpleFlow = TestSimpleFlow(DummyData())
val simpleFlowContext = com.americanexpress.blockchain.maranhao.workflow
        .FlowContext(party, simpleFlow, serviceHub, simpleFlowDummyData)

val simpleTxGenStep = com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowTxGeneratorStep<SignedTransaction>()

class TestSimpleAcceptor : com.americanexpress.blockchain.maranhao.workflow.simpleFlow
    .SimpleAcceptorWithWitnessNotifier() {

    override fun getWitnesses(): List<Party> {
        return listOf(party)
     }

    override fun preCall(): SignedTransaction {
        return signedTransaction
    }
}

val simpleAcceptor = TestSimpleAcceptor()
