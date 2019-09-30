package com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step

import com.americanexpress.blockchain.maranhao.workflow.simpleFlowContext

import org.junit.Test

import org.junit.Assert.*

class SimpleFlowSignStepTest {

    @Test
    fun execute() {
        com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowSignStep.execute(simpleFlowContext)
    }
}