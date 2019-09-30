package com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step

import com.americanexpress.blockchain.maranhao.workflow.simpleFlowContext
import org.junit.Test

class SimpleFlowTransactionVerifyStepTest {
    private val verifyStep = com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowTransactionVerifyStep

    @Test
    fun execute() {
        verifyStep.execute(simpleFlowContext)
    }
}