package com.americanexpress.blockchain.maranhao.workflow

class DefaultLog<CTX, OUT> : Log<CTX, OUT> {
    override fun logMessage(ctx: FlowContext<CTX, OUT>): String {
        return ctx.toString()
    }
}