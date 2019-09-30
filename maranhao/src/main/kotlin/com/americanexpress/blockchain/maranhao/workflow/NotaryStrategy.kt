/**
 * Copyright 2019 American Express Travel Related Services Company, Inc.
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

package com.americanexpress.blockchain.maranhao

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.identity.Party
import net.corda.core.node.ServiceHub

/**
 * strategies for selecting notaries for transactions
 */

interface NotaryStrategy {

    /**
     * Strategy can vary. The simplest one is to pick the first available notary,
     * but is expected to have flexibility in choosing the notary
     *
     * @param serviceHub ServiceHub
     * @return Party
     */
    @Suspendable fun getNotary(serviceHub : ServiceHub): Party
}
