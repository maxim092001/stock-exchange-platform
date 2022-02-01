# Stock exchange platform

The general idea is to create a platform with realtime information about stocks and functionality to buy this stocks as a test. This can help students/people who are scared to invest their own money to test how market actually works.

Project is fully opensource and free-to-use.


<a href="https://typelevel.org/cats/"><img src="https://raw.githubusercontent.com/typelevel/cats/c23130d2c2e4a320ba4cde9a7c7895c6f217d305/docs/src/main/resources/microsite/img/cats-badge.svg" height="40px" align="right" alt="Cats friendly" /></a>
## Status

Right now auth and simple get/add information about stock added.

## Roadmap

* Connect with [Alphavantage](https://www.alphavantage.co/) for realtime stock prices support
* Frontend written probably in [Elm](https://elm-lang.org/)
* Migrate to [LogStage](https://izumi.7mind.io/logstage/index.html)

## Technologies
* [Scala](https://www.scala-lang.org/) - main backend language. FP only 🚀
* [Cats](https://typelevel.org/cats) - FP abstraction library
* [Cats effect](https://typelevel.org/cats-effect/) - FP effects in scala
* Some other cats libraries from [typelevel](https://typelevel.org/) (log4cats etc)
* [Derevo](https://github.com/tofu-tf/derevo) - multiple instance derivation from Haskell.
* [Newtype](https://github.com/estatico/scala-newtype) - newtype abstraction for Scala. Much better than AnyVal
* [Monocle](https://github.com/optics-dev/Monocle) - optics
* [Http4s](https://http4s.org/) - minimal HTTP library for Scala, for building REST services
* [Refined](https://github.com/fthomas/refined) - type-level predicates
* [Circe](https://circe.github.io/circe/) - JSON library
* [Ciris](https://cir.is/) - configuration loading library
* [Skunk](https://tpolecat.github.io/skunk/) - Easy postgres communication
* [Redis4cats](https://github.com/profunktor/redis4cats) - Redis client

