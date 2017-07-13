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
b_1  | [INFO] [07/13/2017 00:42:14.607] [main] [akka.remote.Remoting] Starting remoting
a_1  | [INFO] [07/13/2017 00:42:14.639] [main] [akka.remote.Remoting] Starting remoting
c_1  | [INFO] [07/13/2017 00:42:14.798] [main] [akka.remote.Remoting] Starting remoting
a_1  | [INFO] [07/13/2017 00:42:14.875] [main] [akka.remote.Remoting] Remoting started; listening on addresses :[akka.tcp://example@a:2551]
a_1  | [INFO] [07/13/2017 00:42:14.878] [main] [akka.remote.Remoting] Remoting now listens on addresses: [akka.tcp://example@a:2551]
b_1  | [INFO] [07/13/2017 00:42:14.906] [main] [akka.remote.Remoting] Remoting started; listening on addresses :[akka.tcp://example@b:2552]
b_1  | [INFO] [07/13/2017 00:42:14.911] [main] [akka.remote.Remoting] Remoting now listens on addresses: [akka.tcp://example@b:2552]
c_1  | [INFO] [07/13/2017 00:42:15.055] [main] [akka.remote.Remoting] Remoting started; listening on addresses :[akka.tcp://example@c:2553]
c_1  | [INFO] [07/13/2017 00:42:15.056] [main] [akka.remote.Remoting] Remoting now listens on addresses: [akka.tcp://example@c:2553]
b_1  | discover service at akka.tcp://example@a:2551/user/a
b_1  | discover service at akka.tcp://example@c:2553/user/c
a_1  | discover service at akka.tcp://example@c:2553/user/c
a_1  | discover service at akka.tcp://example@b:2552/user/b
c_1  | discover service at akka.tcp://example@a:2551/user/a
c_1  | discover service at akka.tcp://example@b:2552/user/b
a_1  | [INFO] [07/13/2017 00:42:16.222] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@a:2551/user/a] hello, from b
c_1  | [INFO] [07/13/2017 00:42:16.222] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@c:2553/user/c] hello, from b
c_1  | [INFO] [07/13/2017 00:42:16.224] [example-akka.actor.default-dispatcher-4] [akka.tcp://example@c:2553/user/c] hello, from a
a_1  | [INFO] [07/13/2017 00:42:16.225] [example-akka.actor.default-dispatcher-16] [akka.tcp://example@a:2551/user/a] hello, from c
b_1  | [INFO] [07/13/2017 00:42:16.226] [example-akka.actor.default-dispatcher-2] [akka.tcp://example@b:2552/user/b] hello, from c
b_1  | [INFO] [07/13/2017 00:42:16.227] [example-akka.actor.default-dispatcher-3] [akka.tcp://example@b:2552/user/b] hello, from a
```


### See

http://doc.akka.io/docs/akka/snapshot/scala/remoting.html
