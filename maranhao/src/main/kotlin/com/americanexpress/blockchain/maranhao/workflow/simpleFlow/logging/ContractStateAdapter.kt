package com.americanexpress.blockchain.maranhao.workflow.simpleFlow.logging

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import net.corda.core.contracts.ContractState


class ContractStateAdapter : TypeAdapter<ContractState>() {

    override fun write(out: JsonWriter, value: ContractState?) {

        if(value == null) {
            out.value("null")
            return
        }

        val fields = value::class.java.declaredFields

        out.beginObject()

        fields.forEach {
            it.isAccessible = true
            out.name(it.name).value(it[value].toString())
        }

        out.endObject()
    }

    override fun read(`in`: JsonReader): ContractState? {
        return null
    }
}
