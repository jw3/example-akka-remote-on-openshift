Akka Remoting Example
===

Demonstrating some remoting concepts, initially focusing on service discovery.

The example aims to show a moderately naive discovery example with the aim of being leveraged as a reference implementation.

### Scenario

Simply three services

- `a`
- `b`
- `c`

With dependencies of

- `a` ⇒ `b`, `c`
- `b` ⇒ `a`, `c`
- `c` ⇒ `a`, `b`

Note that the Boots intentionally aim to inject chaos by

- sending the lookup requests prior to starting service
- pausing for a second after sending the requests and prior to starting the service

This exercises the backoff and somewhat simulates the conditions at large system startup.

### Run

```bash
sbt docker:publishLocal \
 && docker-compose up
```

### Output

Using the configuration that specifies dependencies and their locations the example resolves an actor ref for each of them.

Finally each boot sends a hello message to the services they depend on.

```plain
b_1  | [INFO] [07/14/2017 15:55:16.461] [main] [akka.remote.Remoting] Starting remoting
c_1  | [INFO] [07/14/2017 15:55:16.527] [main] [akka.remote.Remoting] Starting remoting
a_1  | [INFO] [07/14/2017 15:55:16.603] [main] [akka.remote.Remoting] Starting remoting
b_1  | [INFO] [07/14/2017 15:55:16.665] [main] [akka.remote.Remoting] Remoting started; listening on addresses :[akka.tcp://example@b:2552]
b_1  | [INFO] [07/14/2017 15:55:16.667] [main] [akka.remote.Remoting] Remoting now listens on addresses: [akka.tcp://example@b:2552]
c_1  | [INFO] [07/14/2017 15:55:16.734] [main] [akka.remote.Remoting] Remoting started; listening on addresses :[akka.tcp://example@c:2553]
c_1  | [INFO] [07/14/2017 15:55:16.736] [main] [akka.remote.Remoting] Remoting now listens on addresses: [akka.tcp://example@c:2553]
b_1  | [INFO] [07/14/2017 15:55:16.778] [example-akka.actor.default-dispatcher-5] [akka.tcp://example@b:2552/user/service-discovery-a/backoff/resolver] resolving service at akka.tcp://example@a:2551/user/a
b_1  | [INFO] [07/14/2017 15:55:16.780] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@b:2552/user/service-discovery-c/backoff/resolver] resolving service at akka.tcp://example@c:2553/user/c
a_1  | [INFO] [07/14/2017 15:55:16.821] [main] [akka.remote.Remoting] Remoting started; listening on addresses :[akka.tcp://example@a:2551]
a_1  | [INFO] [07/14/2017 15:55:16.825] [main] [akka.remote.Remoting] Remoting now listens on addresses: [akka.tcp://example@a:2551]
c_1  | [INFO] [07/14/2017 15:55:16.838] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@c:2553/user/service-discovery-b/backoff/resolver] resolving service at akka.tcp://example@b:2552/user/b
c_1  | [INFO] [07/14/2017 15:55:16.845] [example-akka.actor.default-dispatcher-5] [akka.tcp://example@c:2553/user/service-discovery-a/backoff/resolver] resolving service at akka.tcp://example@a:2551/user/a
a_1  | [INFO] [07/14/2017 15:55:16.942] [example-akka.actor.default-dispatcher-4] [akka.tcp://example@a:2551/user/service-discovery-c/backoff/resolver] resolving service at akka.tcp://example@c:2553/user/c
a_1  | [INFO] [07/14/2017 15:55:16.943] [example-akka.actor.default-dispatcher-16] [akka.tcp://example@a:2551/user/service-discovery-b/backoff/resolver] resolving service at akka.tcp://example@b:2552/user/b
a_1  | [WARN] [07/14/2017 15:55:16.993] [example-akka.actor.default-dispatcher-16] [akka.tcp://example@a:2551/user/service-discovery-c/backoff/resolver] failed to resolve ServiceDef(c,example,c,2553)
c_1  | [WARN] [07/14/2017 15:55:16.997] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@c:2553/user/service-discovery-a/backoff/resolver] failed to resolve ServiceDef(a,example,a,2551)
c_1  | [WARN] [07/14/2017 15:55:17.003] [example-akka.actor.default-dispatcher-17] [akka.tcp://example@c:2553/user/service-discovery-b/backoff/resolver] failed to resolve ServiceDef(b,example,b,2552)
b_1  | [WARN] [07/14/2017 15:55:17.003] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@b:2552/user/service-discovery-a/backoff/resolver] failed to resolve ServiceDef(a,example,a,2551)
b_1  | [WARN] [07/14/2017 15:55:17.003] [example-akka.actor.default-dispatcher-5] [akka.tcp://example@b:2552/user/service-discovery-c/backoff/resolver] failed to resolve ServiceDef(c,example,c,2553)
a_1  | [WARN] [07/14/2017 15:55:17.009] [example-akka.actor.default-dispatcher-16] [akka.tcp://example@a:2551/user/service-discovery-b/backoff/resolver] failed to resolve ServiceDef(b,example,b,2552)
c_1  | [INFO] [07/14/2017 15:55:18.014] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@c:2553/user/service-discovery-a/backoff/resolver] resolving service at akka.tcp://example@a:2551/user/a
a_1  | [INFO] [07/14/2017 15:55:18.018] [example-akka.actor.default-dispatcher-3] [akka.tcp://example@a:2551/user/service-discovery-c/backoff/resolver] resolving service at akka.tcp://example@c:2553/user/c
c_1  | [INFO] [07/14/2017 15:55:18.025] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@c:2553/user/service-discovery-b/backoff/resolver] resolving service at akka.tcp://example@b:2552/user/b
b_1  | [INFO] [07/14/2017 15:55:18.027] [example-akka.actor.default-dispatcher-3] [akka.tcp://example@b:2552/user/service-discovery-a/backoff/resolver] resolving service at akka.tcp://example@a:2551/user/a
b_1  | [INFO] [07/14/2017 15:55:18.027] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@b:2552/user/service-discovery-c/backoff/resolver] resolving service at akka.tcp://example@c:2553/user/c
a_1  | [INFO] [07/14/2017 15:55:18.028] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@a:2551/user/service-discovery-b/backoff/resolver] resolving service at akka.tcp://example@b:2552/user/b
a_1  | [WARN] [07/14/2017 15:55:18.030] [example-akka.actor.default-dispatcher-17] [akka.tcp://example@a:2551/user/service-discovery-c/backoff/resolver] failed to resolve ServiceDef(c,example,c,2553)
b_1  | [WARN] [07/14/2017 15:55:18.039] [example-akka.actor.default-dispatcher-4] [akka.tcp://example@b:2552/user/service-discovery-c/backoff/resolver] failed to resolve ServiceDef(c,example,c,2553)
a_1  | [WARN] [07/14/2017 15:55:18.040] [example-akka.actor.default-dispatcher-16] [akka.tcp://example@a:2551/user/service-discovery-b/backoff/resolver] failed to resolve ServiceDef(b,example,b,2552)
c_1  | [WARN] [07/14/2017 15:55:18.042] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@c:2553/user/service-discovery-b/backoff/resolver] failed to resolve ServiceDef(b,example,b,2552)
a_1  | [INFO] [07/14/2017 15:55:18.044] [example-akka.actor.default-dispatcher-16] [akka.tcp://example@a:2551/user/a] hello, from c
a_1  | [INFO] [07/14/2017 15:55:18.051] [example-akka.actor.default-dispatcher-3] [akka.tcp://example@a:2551/user/a] hello, from b
a_1  | [INFO] [07/14/2017 15:55:20.047] [example-akka.actor.default-dispatcher-3] [akka.tcp://example@a:2551/user/service-discovery-c/backoff/resolver] resolving service at akka.tcp://example@c:2553/user/c
c_1  | [INFO] [07/14/2017 15:55:20.054] [example-akka.actor.default-dispatcher-3] [akka.tcp://example@c:2553/user/service-discovery-b/backoff/resolver] resolving service at akka.tcp://example@b:2552/user/b
b_1  | [INFO] [07/14/2017 15:55:20.057] [example-akka.actor.default-dispatcher-3] [akka.tcp://example@b:2552/user/service-discovery-c/backoff/resolver] resolving service at akka.tcp://example@c:2553/user/c
a_1  | [INFO] [07/14/2017 15:55:20.058] [example-akka.actor.default-dispatcher-3] [akka.tcp://example@a:2551/user/service-discovery-b/backoff/resolver] resolving service at akka.tcp://example@b:2552/user/b
c_1  | [INFO] [07/14/2017 15:55:20.073] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@c:2553/user/c] hello, from b
b_1  | [INFO] [07/14/2017 15:55:20.080] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@b:2552/user/b] hello, from c
c_1  | [INFO] [07/14/2017 15:55:20.091] [example-akka.actor.default-dispatcher-18] [akka.tcp://example@c:2553/user/c] hello, from a
b_1  | [INFO] [07/14/2017 15:55:20.102] [example-akka.actor.default-dispatcher-3] [akka.tcp://example@b:2552/user/b] hello, from a
```

The stacktraces were snipped for brevity.


### See

http://doc.akka.io/docs/akka/snapshot/scala/remoting.html
