package test.kotlin.com.americanexpress.blockchain.example

import com.americanexpress.blockchain.example.flow.DummyLoanFlow
import com.americanexpress.blockchain.example.flow.DummyLoanFlow.Acceptor
import com.americanexpress.blockchain.example.flow.DummyLoanFlow.Initiator
import com.americanexpress.blockchain.example.flow.DummyLoanFlow.Loan
import com.americanexpress.blockchain.example.flow.DummyPayoffLoan
import com.americanexpress.blockchain.example.state.DummyLoanState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.TransactionVerificationException
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.toFuture
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow
import net.corda.testing.core.singleIdentity
import net.corda.testing.node.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith
import net.corda.testing.core.DUMMY_NOTARY_NAME
import org.junit.Assert
import kotlin.math.log
import kotlin.test.assertEquals


class DummyLoanFlowTest {
    private lateinit var network: MockNetwork
    private lateinit var a: StartedMockNode
    private lateinit var b: StartedMockNode
    private lateinit var s: StartedMockNode
    private lateinit var r: StartedMockNode

    private val aliceName = CordaX500Name("Alice", "New York", "US")
    private val bobName = CordaX500Name("Bob", "Tokyo", "JP")
    private val susanName = CordaX500Name("Susan", "Budapest", "HU")
    private val regulatorName = CordaX500Name("Regulator", "Phoenix", "US")
    private lateinit var alice: Party
    private lateinit var bob: Party
    private lateinit var susan: Party
    private lateinit var regulator: Party

    @Before
    fun setup() {
        val notarySpecs = listOf(MockNetworkNotarySpec(DUMMY_NOTARY_NAME, true))

        network = MockNetwork(MockNetworkParameters(
                notarySpecs = notarySpecs,
                cordappsForAllNodes = listOf(
                        TestCordapp.findCordapp("com.americanexpress.blockchain.example.contract"),
                        TestCordapp.findCordapp("com.americanexpress.blockchain.example.state"),
                        TestCordapp.findCordapp("com.americanexpress.blockchain.example.flow")
                )))
        a = network.createPartyNode(aliceName)
        b = network.createPartyNode(bobName)
        s = network.createPartyNode(susanName)
        r = network.createPartyNode(regulatorName)

        alice = a.info.singleIdentity()
        bob = b.info.singleIdentity()
        susan = s.info.singleIdentity()
        regulator = r.info.singleIdentity()

        // For real nodes this happens automatically, but we have to manually register the flow for tests.
        listOf(a, b, s).forEach { it.registerInitiatedFlow(Acceptor::class.java) }
        listOf(a, b, s).forEach { it.registerInitiatedFlow(DummyPayoffLoan.AcceptorWithNotifier::class.java) }
        r.registerInitiatedFlow(DummyPayoffLoan.ReceiveRegulatoryReportFlow::class.java)

        network.runNetwork()
    }

    @After
    fun tearDown() {
        network.stopNodes()
    }

    @Test
    fun `loan origination completion`() {
        val testId = UniqueIdentifier()

        val flow = Initiator(Loan(id = testId, amount = 1000,
                interest = 0F, borrower = bob, lender = susan))
        var future = a.startFlow(flow)
        network.runNetwork()

        val signedTx = future.getOrThrow()
        signedTx.verifySignaturesExcept(b.info.singleIdentity().owningKey)
        assertEquals(signedTx, b.services.validatedTransactions.getTransaction(signedTx.id))

        assertEquals(1, b.services.vaultService.queryBy<LinearState>().states.size)

        val payoffFlow = DummyPayoffLoan.Initiator(testId, bob, susan)
        val payoffFuture = b.startFlow(payoffFlow)
        network.runNetwork()

        val signedPayoffTx = payoffFuture.getOrThrow()
        val state = signedPayoffTx.coreTransaction.outputs[0].data as DummyLoanState
        Assert.assertEquals(0, state.value)

        val regulatorStates = r.services.vaultService.queryBy<LinearState>().states
        assertEquals(1, regulatorStates.size)
    }
}