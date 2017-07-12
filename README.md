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
a_1  | [INFO] [07/12/2017 22:38:49.827] [main] [akka.remote.Remoting] Starting remoting
c_1  | [INFO] [07/12/2017 22:38:49.833] [main] [akka.remote.Remoting] Starting remoting
b_1  | [INFO] [07/12/2017 22:38:49.932] [main] [akka.remote.Remoting] Starting remoting
c_1  | [INFO] [07/12/2017 22:38:50.029] [main] [akka.remote.Remoting] Remoting started; listening on addresses :[akka.tcp://example@c:2553]
c_1  | [INFO] [07/12/2017 22:38:50.032] [main] [akka.remote.Remoting] Remoting now listens on addresses: [akka.tcp://example@c:2553]
a_1  | [INFO] [07/12/2017 22:38:50.039] [main] [akka.remote.Remoting] Remoting started; listening on addresses :[akka.tcp://example@a:2551]
a_1  | [INFO] [07/12/2017 22:38:50.041] [main] [akka.remote.Remoting] Remoting now listens on addresses: [akka.tcp://example@a:2551]
b_1  | [INFO] [07/12/2017 22:38:50.120] [main] [akka.remote.Remoting] Remoting started; listening on addresses :[akka.tcp://example@b:2552]
b_1  | [INFO] [07/12/2017 22:38:50.122] [main] [akka.remote.Remoting] Remoting now listens on addresses: [akka.tcp://example@b:2552]
c_1  | discover service at akka.tcp://example@a:2551/user/a
a_1  | discover service at akka.tcp://example@b:2552/user/b
c_1  | discover service at akka.tcp://example@b:2552/user/b
a_1  | discover service at akka.tcp://example@c:2553/user/c
b_1  | discover service at akka.tcp://example@a:2551/user/a
b_1  | discover service at akka.tcp://example@c:2553/user/c
c_1  | resolved a
c_1  | resolved b
b_1  | resolved a
b_1  | resolved c
a_1  | resolved b
a_1  | resolved c
b_1  | [INFO] [07/12/2017 22:38:51.339] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@b:2552/user/b] hello, from c
a_1  | [INFO] [07/12/2017 22:38:51.339] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@a:2551/user/a] hello, from c
b_1  | [INFO] [07/12/2017 22:38:51.340] [example-akka.actor.default-dispatcher-3] [akka.tcp://example@b:2552/user/b] hello, from a
a_1  | [INFO] [07/12/2017 22:38:51.339] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@a:2551/user/a] hello, from b
c_1  | [INFO] [07/12/2017 22:38:51.340] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@c:2553/user/c] hello, from a
c_1  | [INFO] [07/12/2017 22:38:51.340] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@c:2553/user/c] hello, from b
```


### See

http://doc.akka.io/docs/akka/snapshot/scala/remoting.html
