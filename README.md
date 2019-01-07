# replay-kt
[![Build Status](https://travis-ci.com/cubeee/replay-kt.svg?branch=master)](https://travis-ci.com/cubeee/replay-kt)
[![GitHub release](https://img.shields.io/github/release/cubeee/replay-kt.svg)](https://github.com/cubeee/replay-kt/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

replay-kt is a [Rocket League](https://www.rocketleague.com/) replay file parser written in [Kotlin](https://kotlinlang.org/).

replay-kt is the first JVM-based replay parser and was made out of necessity to 
fill the need of a fast and easily utilized parser for JVM-based projects.
The parser heavily uses two common parsers [tfausak/rattletrap](https://github.com/tfausak/rattletrap) and [jjbott/RocketLeagueReplayParser](https://github.com/jjbott/RocketLeagueReplayParser)
as reference and couldn't have been written without them.

## Performance

Performance is an important part of replay-kt. Replays can be expected to be parsed in 150ms or less on decent hardware.

Memory usage and performance benchmarking has not been done yet.

## Using replay-kt

Releases can be accessed on GitHub and through [jitpack.io](https://jitpack.io/private#cubeee/replay-kt/) for build tools.

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.cubeee.replay-kt:replay-kt:0.1.5'
}
```

Parsing replays can be done by using one of the many static methods provided in the ``Replay`` class:
````kotlin
import com.x7ff.parser.replay.Replay

val replay = Replay.parse(bytes)
````

## Todo
* JSON encoding
* Simple HTTP server for PaaS (Parser as a Service)
  * Options for filtering certain parts of the output
* Unit tests and/or replays to test against
* Resource usage/performance benchmarks

## Known bugs
* Vector values are reading wrong - help appreciated
* Non-Steam platform unique id's may be giving weird values

## Contributing

The best way to contribute is to send us [pull requests](https://help.github.com/articles/about-pull-requests/).