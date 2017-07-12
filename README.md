Akka Remoting Example
===

Demonstrating some remoting concepts

Simply three services

- `a`
- `b`
- `c`

With dependencies of

- `a` ⇒ `b`, `c`
- `b` ⇒ `a`, `c`
- `c` ⇒ `a`, `b`

### Run

```bash
sbt docker:publishLocal \
 && docker-compose up
```

### Output

Using the configuration that specifies dependencies and their locations the example resolves an actor ref for each of them.

Finally each boot sends a hello message to the services they depend on.

```plain
three_1  | [INFO] [07/12/2017 22:10:51.211] [main] [akka.remote.Remoting] Starting remoting
two_1    | [INFO] [07/12/2017 22:10:51.284] [main] [akka.remote.Remoting] Starting remoting
three_1  | [INFO] [07/12/2017 22:10:51.408] [main] [akka.remote.Remoting] Remoting started; listening on addresses :[akka.tcp://example@three:2553]
three_1  | [INFO] [07/12/2017 22:10:51.410] [main] [akka.remote.Remoting] Remoting now listens on addresses: [akka.tcp://example@three:2553]
one_1    | [INFO] [07/12/2017 22:10:51.415] [main] [akka.remote.Remoting] Starting remoting
two_1    | [INFO] [07/12/2017 22:10:51.477] [main] [akka.remote.Remoting] Remoting started; listening on addresses :[akka.tcp://example@two:2552]
two_1    | [INFO] [07/12/2017 22:10:51.479] [main] [akka.remote.Remoting] Remoting now listens on addresses: [akka.tcp://example@two:2552]
one_1    | [INFO] [07/12/2017 22:10:51.577] [main] [akka.remote.Remoting] Remoting started; listening on addresses :[akka.tcp://example@one:2551]
one_1    | [INFO] [07/12/2017 22:10:51.578] [main] [akka.remote.Remoting] Remoting now listens on addresses: [akka.tcp://example@one:2551]
three_1  | discover service at akka.tcp://example@one:2551/user/a
three_1  | discover service at akka.tcp://example@two:2552/user/b
two_1    | discover service at akka.tcp://example@one:2551/user/a
two_1    | discover service at akka.tcp://example@three:2553/user/c
one_1    | discover service at akka.tcp://example@two:2552/user/b
one_1    | discover service at akka.tcp://example@three:2553/user/c
two_1    | resolved a
two_1    | resolved c
three_1  | resolved a
three_1  | resolved b
one_1    | resolved b
one_1    | resolved c
two_1    | [INFO] [07/12/2017 22:10:52.717] [example-akka.actor.default-dispatcher-3] [akka.tcp://example@two:2552/user/b] hello, from c
one_1    | [INFO] [07/12/2017 22:10:52.718] [example-akka.actor.default-dispatcher-3] [akka.tcp://example@one:2551/user/a] hello, from b
one_1    | [INFO] [07/12/2017 22:10:52.718] [example-akka.actor.default-dispatcher-3] [akka.tcp://example@one:2551/user/a] hello, from c
three_1  | [INFO] [07/12/2017 22:10:52.719] [example-akka.actor.default-dispatcher-4] [akka.tcp://example@three:2553/user/c] hello, from a
three_1  | [INFO] [07/12/2017 22:10:52.715] [example-akka.actor.default-dispatcher-6] [akka.tcp://example@three:2553/user/c] hello, from b
two_1    | [INFO] [07/12/2017 22:10:52.719] [example-akka.actor.default-dispatcher-5] [akka.tcp://example@two:2552/user/b] hello, from a
```


### See

http://doc.akka.io/docs/akka/snapshot/scala/remoting.html
