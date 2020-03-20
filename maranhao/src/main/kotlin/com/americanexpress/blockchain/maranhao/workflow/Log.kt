package com.americanexpress.blockchain.maranhao.workflow

import net.corda.core.serialization.CordaSerializable


/**
 * All steps in the framework must implement this interface
 * @param CTX
 */

interface Log<CTX, OUT> {
    fun logMessage(ctx: FlowContext<CTX, OUT>) : String
}
