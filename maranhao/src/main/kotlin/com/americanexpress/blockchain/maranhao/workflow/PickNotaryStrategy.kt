/**
 * Copyright 2020 American Express Travel Related Services Company, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.americanexpress.blockchain.maranhao.workflow

import co.paralleluniverse.fibers.Suspendable
import com.americanexpress.blockchain.maranhao.NotaryStrategy
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.node.ServiceHub

class PickNotaryStrategy(private val notaryIdentity: String) : NotaryStrategy {

    /**
     * Gets the notary with id passed in constructor
     * @param serviceHub ServiceHub
     * @return Party
     */
    @Suspendable
    override fun getNotary(serviceHub: ServiceHub): Party {
        val notaryX500Name = CordaX500Name.parse(notaryIdentity)
        val notaryParty = serviceHub.networkMapCache.getNotary(notaryX500Name)
        return notaryParty!!
    }
}
