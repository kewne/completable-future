---
layout: base
---
# Understanding the `CompletionStage` interface

The documentation for the [`CompletionStage`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletionStage.html)
 interface describes instances as _possibly_ asynchronous computations.
This means that a `CompletionStage` can refer to a computation that:

* is executing asynchronously and is yet to complete
* has executed asynchronously and has already completed
* executed synchronously and has already completed

An example of the last case would be calling
 `CompletableFuture#completedFuture`, presumably because
 no asynchronous computation is required (hence the _possibly_).

This is similar to the `Future` class.
The difference is in the interface's methods, that
allow "hooking" logic to execute "automatically" when
the computation associated with the future completes.

As an example:

{% highlight java %}
CompletionStage<String> a = getUserName();
CompletionStage<String> b = a.thenApply(String::toUpperCase);
{% endhighlight %}

This code first calls the `getUserName()`, which appears to
launch an asynchronous computation to fetch a name, `a`.
Then, the [`CompletionStage#thenApply`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletionStage.html#thenApply-java.util.function.Function-)
 method is called on `a`,
which produces a derived asynchronous computation `b` which
produces the result of `a` but with `String#toUpperCase` applied.
This transformation automatically occurs when `a` completes and,
after the call to `String#toUpperCase`, `b` completes as well.

A striking feature of `CompletionStage` is its lack of methods
for performing a blocking wait for the computation's result.
This is possible with it's main implementation, `CompletableFuture` (although not recommended).

## How the results of computations propagate

A computation can essentially end in two ways:

1. Successful completion, in which case the result is passed
 to downstream success or catch-all callbacks;
2. Exceptional completion, in which an exception is passed to
downstream exception or catch-all callbacks.

An example of a success callback was already shown above when a
call to `CompletionStage#thenApply` was called: the
`Function` passed into it will only be called if `a`'s computation
completes successfully.

If an exception is thrown by the computation, exception callbacks will be called, which
can be created using [`CompletionStage#exceptionally`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletionStage.html#exceptionally-java.util.function.Function-).
As an example:

{% highlight java %}
// SuccessAndExceptionalTest#showSuccessCallbacksNotCalledOnException
CompletionStage<String> a = getUserName()
     .whenComplete((r, t) -> { throw new NullPointerException(); })
     .thenAccept(r -> LOGGER.info("Great Success with user {}", r) )
     .exceptionally(t -> { LOGGER.warn("Exception!", t); return null; });
{% endhighlight %}

Will only print `Exception!` along with the exception's stack trace.
By contrast, removing the exception throwing line:

{% highlight java %}
// SuccessAndExceptionalTest#showExceptionCallbacksNotCalledOnSuccess
CompletionStage<String> a = getUserName()
        .thenAccept(r -> LOGGER.info("Great success with {}", r))
        .exceptionally(t -> { LOGGER.info("Exception!", t); return null; });
{% endhighlight %}

will print "Great success with kewne".

Finally, some methods like ['CompletionStage#handle`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletionStage.html#handle-java.util.function.BiFunction-)
receive callbacks that will get executed regardless of whether the
computation completed successfully:

{% highlight java %}
// SuccessAndExceptionalTest#completionMethodsCalledOnSuccess
// prints "Still called"
CompletionStage<String> a = getUserName()
        .thenAccept(r -> LOGGER.info("Great success!"))
        .whenComplete((r, t) -> LOGGER.info("Still called"));
{% endhighlight %}

 or exceptionally:

{% highlight java %}
// SuccessAndExceptionalTest#completionMethodsCalledOnException
// prints "Still called"
CompletionStage<String> a =getUserName()
        .whenComplete((r, t) -> {
            throw new NullPointerException();
        })
        .thenAccept(r -> LOGGER.info("Great success!"))
        .whenComplete((r, t) -> LOGGER.info("Still called"));
{% endhighlight %}

## How to derive a computation

Nearly all methods of `CompletionStage` produce a derived
computation.
These are explained below.

### [`thenApply`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletionStage.html#thenApply-java.util.function.Function-)

The `thenApply` method (and variants) performs a simple map operation
using the `java.util.Function` passed as parameter.
This `Function` will be called (on success) with the original
computation's result.

### [`thenAccept`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletionStage.html#thenAccept-java.util.function.Consumer-)

The `thenAccept` method (and variants) calls the `java.util.Consumer`
 passed as parameter, effectively "consuming" it.
This derivation will produce a `CompletionStage<Void>`, meaning
any derived computations will receive `Void` as a result.
