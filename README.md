# clj-rest-api

[![CircleCI](https://circleci.com/gh/lagenorhynque/clj-rest-api.svg?style=shield)](https://circleci.com/gh/lagenorhynque/clj-rest-api)
[![codecov](https://codecov.io/gh/lagenorhynque/clj-rest-api/branch/master/graph/badge.svg)](https://codecov.io/gh/lagenorhynque/clj-rest-api)

An example REST API based on [Pedestal](https://github.com/pedestal/pedestal) & [Duct](https://github.com/duct-framework/duct).

This project shares the data models with [lagenorhynque/aqoursql](https://github.com/lagenorhynque/aqoursql), an example GraphQL API.

## Developing

### Prerequisites

- [Java (JDK)](http://openjdk.java.net/)
    - `java -version` >= 8 (1.8.0)
- [Leiningen](https://leiningen.org/)
- [Docker](https://www.docker.com/)

### Setup

When you first clone this repository, run:

```sh
$ lein duct setup
```

This will create files for local configuration, and prep your system
for the project.

### Database

```sh
# Start local DB
$ docker-compose up -d
# Import DB schema
$ mysql -h127.0.0.1 -P3316 -uroot -proot aqoursql < sql/ddl/aqoursql.sql
$ mysql -h127.0.0.1 -P3317 -uroot -proot aqoursql_test < sql/ddl/aqoursql.sql
# Seed DB
$ mysql -h127.0.0.1 -P3316 -uroot -proot aqoursql < sql/dml/seed.sql
```

### Development environment

To begin developing, start with a REPL.

```sh
$ lein repl
```

With [rebel-readline](https://github.com/bhauman/rebel-readline):

```sh
$ lein rebel
```

Then load the development environment.

```clojure
user=> (dev)
:loaded
```

Run `go` to prep and initiate the system.

```clojure
dev=> (go)
:duct.server.http.jetty/starting-server {:port 9999}
:initiated
```

By default this creates a web server at <http://localhost:9999>.

When you make changes to your source files, use `reset` to reload any
modified files and reset the server.

```clojure
dev=> (reset)
:reloading (...)
:resumed
```

Run `halt` to halt the system.

```clojure
dev=> (halt)
:halted
```

### Production build & run

```sh
$ lein uberjar
$ DATABASE_URL="..." java -jar target/clj-rest-api.jar
```

### Testing

Testing is fastest through the REPL, as you avoid environment startup
time.

```clojure
dev=> (test)
...
```

But you can also run tests through Leiningen.

```sh
$ lein test
```

with [cloverage](https://github.com/cloverage/cloverage):

```sh
$ lein test-coverage
# Open the coverage report
$ open target/coverage/index.html
```

### Linting ([`cljfmt check`](https://github.com/weavejester/cljfmt), [`eastwood`](https://github.com/jonase/eastwood), [`kibit`](https://github.com/jonase/kibit))

```sh
$ lein lint
```

### API Documentation ([Codox](https://github.com/weavejester/codox))

```sh
$ lein codox
$ open target/codox/index.html
```
