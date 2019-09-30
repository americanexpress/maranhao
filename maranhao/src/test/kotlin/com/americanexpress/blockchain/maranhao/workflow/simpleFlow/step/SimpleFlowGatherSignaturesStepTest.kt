package com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step

import com.americanexpress.blockchain.maranhao.workflow.simpleFlowContext
import org.junit.Test

import org.junit.Assert.*
import kotlin.test.assertFailsWith

class SimpleFlowGatherSignaturesStepTest {

    @Test
    fun execute() {
        val exception = assertFailsWith<IllegalStateException> {
            com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step
                    .SimpleFlowGatherSignaturesStep.execute(simpleFlowContext)
        }
        assertEquals("You cannot access the flow's state machine until the flow has been started.", exception.message)
    }
}