package com.americanexpress.blockchain.maranhao.workflow.simpleFlow.logging

import com.americanexpress.blockchain.maranhao.workflow.FlowContext
import com.americanexpress.blockchain.maranhao.workflow.Log
import com.americanexpress.blockchain.maranhao.workflow.simpleFlow.SimpleFlowData
import com.google.gson.GsonBuilder
import net.corda.core.contracts.ContractState
import net.corda.core.identity.Party

class SimpleLog<OUT> : Log<SimpleFlowData, OUT> {

    companion object {
        val gson = GsonBuilder()
                .registerTypeAdapter(ContractState::class.java, ContractStateAdapter())
                .registerTypeAdapter(Party::class.java, PartyAdapter())
                .setLenient()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation()
                .disableHtmlEscaping()
                .create()
    }

    override fun logMessage(ctx: FlowContext<SimpleFlowData, OUT>) : String {
        val ret = gson.toJson(ctx.sharedData, SimpleFlowData::class.java)
        return ret
    }
}