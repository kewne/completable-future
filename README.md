# completable-future

Java 8 has introduced the `CompletableFuture` class, which implements
the (also new) `CompletionStage` interface.

The class is meant to aid in asynchronous processing and contains a set
of promise-like methods.
However, the class also contains several similarly named methods that
can make it confusing to understand when they should be used.

## Understanding the `CompletionStage` interface

The documentation for the `CompletionStage` interface describes
instances as _possibly_ asynchronous computations.
This means that a `CompletionStage` can refer to a computation that:

* is executing asynchronously and is yet to complete
* has executed asynchronously and has already completed
* executed synchronously and has already completed

`CompletableFuture`, the only implementation of `CompletionStage` in
the JDK, 
