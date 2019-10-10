# Maranhão

Maranhão is a simple framework that aims at speeding up development using Corda DLT. It encapsulates the
boilerplate code, allowing developers to focus on spending time on the business functionality.


The framework breaks up workflow into steps. A simple implementation is also part of the framework. The templatized
imput allows for arbitrary parameters (ideally, a data class) and a number of abstract functions that need to be overridden

```kotlin
    @InitiatingFlow
    @StartableByRPC
    class Initiator(input: Loan) : SimpleMultiStepFlowInitiator<Loan>(input) {
        override fun getListOfSigners(): List<Party> = input.signers

        override fun getCommandData(): CommandData = ExampleContract.Commands.Request()

        override fun getState(): ContractState = ExampleState(input.amount, input.borrower,
                    input.lender, input.approver,
                    input.signers)

        override fun getStateId(): String = "com.americanexpress.blockchain.example.state.ExampleState"
    }
```

#### Simple workflow

To add processing of a new state, override 'initialProcessingStep' as follows:

```kotlin
    @InitiatingFlow
    @StartableByRPC
    class Initiator(input: Loan) : SimpleMultiStepFlowInitiator<Loan>(input) {
        @Suspendable override fun getListOfSigners(): List<Party> = listOf(input.lender, input.approver)
        @Suspendable override fun getCommandData(): CommandData = ExampleContract.Commands.Request()
        @Suspendable override fun getState(): ContractState = ExampleState(input.amount, input.lender, input.approver)
        @Suspendable override fun getStateId(): String = "com.americanexpress.blockchain.example.contract.ExampleContract"

        override var initialProcessingStep = object: SimpleFlowStep {
            override fun execute(ctx: FlowContext<SimpleFlowData>) {
                val initialState = ctx.sharedData!!.state as ExampleState
                ctx.sharedData!!.outputState = initialState.copy(interestRate = 0.05F)
            }
        }
    }

```

### Support for Observer (Witness) node

Provided by the `SimpleAcceptorWithWitness` class. To integrate, take the following steps:

1. Make your acceptor class an extension of `SimpleAcceptorWithWitnessNotifier`
2. Override the `getWitness()` function to return the witness (observer) party
3. Override the `preCall()` function, by implementing all functionality you would normally
do in the overridden `call()` function
4. Mark the witness acceptor with `@InitiatedBy(ReportToWitnessFlow::class)`. 
`ReportToWitnessFlow` class belongs to the framework. Sample usage in the 
[example](./example/src/main/kotlin/com.americanexpress.blockchain.example/flow/DummyPayoffLoan.kt) 
project

### Installation

Clone, then just run the command below. The script will install all required libraries: `./gradlew`
 

To run a full test, just run: `./gradlew test`
 
### Where the name (Maranhão) coming from?
 
 Corda is a river in the State of Maranhão, in northeastern Brazil. 
 We thought it was a cool name for a Corda framework :)
 
### Authors

* Andras L Ferenczi <andras.l.ferenczi@aexp.com> [andrasfe](https://github.com/andrasfe)
* Pancham Singh <pancham.singh@aexp.com> 

### Contributing

We welcome Your interest in the American Express Open Source Community on Github. 
Any Contributor to any Open Source Project managed by the American Express Open Source Community 
must accept and sign an Agreement indicating agreement to the terms below. 
Except for the rights granted in this Agreement to American Express and to recipients of software 
distributed by American Express, You reserve all right, title, and interest, if any, in and to 
Your Contributions. Please [fill out the Agreement](https://cla-assistant.io/americanexpress/maranhao).

Please feel free to open pull requests and see CONTRIBUTING.md for commit formatting details.

### License
Any contributions made under this project will be governed by the Apache License 2.0.

### Code of Conduct
This project adheres to the American Express Community Guidelines. 
By participating, you are expected to honor these guidelines.
