package com.americanexpress.blockchain.maranhao.workflow.simpleFlow.logging

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import net.corda.core.identity.Party

class PartyAdapter : TypeAdapter<Party>() {
    override fun write(out: JsonWriter?, value: Party?) {
        if(value == null) {
            out!!.value("null")
            return
        }
        out!!.value(value.toString())
    }

    override fun read(`in`: JsonReader?): Party? {
        return null
    }
}
