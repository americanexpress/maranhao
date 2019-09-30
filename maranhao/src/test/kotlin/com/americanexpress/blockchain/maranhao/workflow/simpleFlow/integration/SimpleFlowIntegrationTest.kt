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

package com.americanexpress.blockchain.maranhao.workflow.simpleFlow.integration

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.*
import net.corda.core.flows.*
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.transactions.LedgerTransaction
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow
import net.corda.testing.core.singleIdentity
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkNotarySpec
import net.corda.testing.node.MockNodeParameters
import net.corda.testing.node.StartedMockNode
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class SomeContract : Contract {
    override fun verify(tx: LedgerTransaction) {}
    interface Commands : CommandData {
        class Request : Commands
    }
}

@BelongsToContract(SomeContract::class)
data class SomeState(
        val value: Int,
        override val linearId: UniqueIdentifier = UniqueIdentifier()) : LinearState {
    override val participants: List<AbstractParty> = listOf(bob)
}

data class Simple(val input: Int)


lateinit var alice: Party
lateinit var bob: Party

class SimpleFlowIntegrationTest {

    lateinit var mockNetwork: MockNetwork
    lateinit var a: StartedMockNode
    lateinit var b: StartedMockNode

    @InitiatingFlow
    @StartableByRPC
    class Initiator(input: Simple) : com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleMultiStepFlowInitiator<Simple>(input) {
        override fun getListOfSigners(): List<Party> = listOf(bob)
        override fun getCommandData(): CommandData = SomeContract.Commands.Request()
        override fun getState(): ContractState = SomeState(value = 6)
        @Suspendable
        override fun getStateId(): String =
                "com.americanexpress.blockchain.maranhao.workflow.simpleFlow.integration.SomeContract"
    }

    @InitiatedBy(Initiator::class)
    class Acceptor(val otherPartySession: FlowSession) : FlowLogic<SignedTransaction>() {

        @Suspendable
        override fun call(): SignedTransaction {
            val signTransactionFlow = object : SignTransactionFlow(otherPartySession) {
                override fun checkTransaction(stx: SignedTransaction) = requireThat {}
            }
            val txId = subFlow(signTransactionFlow).id
            return subFlow(ReceiveFinalityFlow(otherPartySession, expectedTxId = txId))
        }
    }

    @Before
    fun init() {
        mockNetwork = MockNetwork(listOf("com.americanexpress.blockchain.maranhao.workflow.simpleFlow.integration"),
                notarySpecs = listOf(MockNetworkNotarySpec(CordaX500Name("Notary", "London", "GB"))))
        a = mockNetwork.createNode(MockNodeParameters())
        b = mockNetwork.createNode(MockNodeParameters())
        alice = a.info.singleIdentity()
        bob = b.info.singleIdentity()
        val startedNodes = arrayListOf(a, b)
        // For real nodes this happens automatically, but we have to manually register the flow for tests
        startedNodes.forEach { it.registerInitiatedFlow(Acceptor::class.java) }
        mockNetwork.runNetwork()
    }

    @After
    fun end() {
        mockNetwork.stopNodes()
    }

    @Test
    fun `loan origination completion`() {
        val flow = Initiator(Simple(5))
        var future = a.startFlow(flow)
        mockNetwork.runNetwork()

        val signedTx = future.getOrThrow()
        signedTx.verifySignaturesExcept(b.info.singleIdentity().owningKey)
        assertEquals(signedTx, b.services.validatedTransactions.getTransaction(signedTx.id))
        assertEquals(1, b.services.vaultService.queryBy<LinearState>().states.size)
    }

}