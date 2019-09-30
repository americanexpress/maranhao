package com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step

import com.americanexpress.blockchain.maranhao.workflow.signedTransaction
import org.junit.Test

import org.junit.Assert.*

class SimpleFlowFinalStepTest {

    private val step = com.americanexpress.blockchain.maranhao.workflow.simpleFlow.step.SimpleFlowFinalStep

    @Test
    fun execute() {
    }

    @Test
    fun getFinalSignedTransaction() {
        step.signedTransaction = signedTransaction
        assertNotNull(step.getFinalSignedTransaction())
    }
}