# kotlin-quakes

A Kotlin library and associated applications for manipulating data from the
[USGS Earthquake Hazards Program data feeds][usgs].

## Library

This consists of three classes: `Quake`, `QuakeFeed` and `QuakeDataset`.
You can run the tests for these classes with
```shell
./gradlew :lib:test
```

`Quake` provides a significantly simplified representation of an earthquake
event, consisting solely of event time, longitude & latitude, depth and
magnitude. Other elements of the USGS data feeds are (for the moment)
ignored.

`QuakeFeed` represents one of the available CSV-based feeds. A feed is
selected by specifying its severity level and time period. For example, a
severity level of `"4.5"` will select a feed of events of magnitude 4.5
or higher, and a time period of `"week"` will select a feed of events
for the past 7 days:
```kotlin
val feed = QuakeFeed("4.5", "week")
```

`QuakeDataset` represents a dataset collected from a feed. This starts
off empty and must be populated by calling the `updateFrom()` method, with
a feed as an argument:
```kotlin
val dataset = QuakeDataset().apply { updateFrom(feed) }
```

`updateFrom()` can be invoked repeatedly thereafter, but note that each
invocation will replace existing data with the latest contents of the given
feed.

A dataset is somewhat list-like: you can query its size, test whether it
is empty or not, and access individual quake events by their position in
the dataset. You can also iterate over the contents of the dataset using a
for loop:
```kotlin
println("${dataset.size} quakes acquired")

val first = dataset[0]
```

You can also query a dataset to find the shallowest and deepest quakes,
the weakest and strongest quakes, mean quake depth and mean quake magnitude.
All of these queries return `null` if the dataset hasn't yet been populated
from a feed.
```kotlin
dataset.strongestQuake?.let {
    println("Strongest quake has a magnitude of ${it.magnitude}")
}
```

You can display the contents of a dataset in a plain text tabular format
like so:
```kotlin
println(dataset.asTable())
println(dataset.asTable(compareBy { it.depth }))
println(dataset.asTable(compareByDescending { it.magnitude }))
```
The first of these examples displays quakes sorted by ascending event time,
via a default `Comparator` object. You can easily plug in a different
comparator, using Kotlin's `compareBy` & `compareByDescending` functions,
as shown in the second and third examples.

## Applications

### quakeinfo

This terminal-based application extracts information from a USGS data feed.
It can print summary statistics for the captured data, print a table of
quake details, or write the captured data to a CSV file (or any combination
of those three operations). The application has a command line UI, implemented
with [Clikt][clikt].

For testing purposes, you can run the application from Gradle:
```shell
./gradlew :quakeinfo:run
```

This will supply a fixed set of command line arguments. You can specify
your own arguments like so:
```shell
./gradlew :quakeinfo:run --args="--summary 4.5 week"
```

A standalone distribution of the application can be created with
```shell
./gradlew :quakeinfo:distZip
```

### quakemap

This JavaFX application plots quake event locations on a map projection.
A tooltip is created for each plotted point, contain details of the
associated quake.

For testing purposes, you can run the application from Gradle:
```shell
./gradlew :quakemap:run
```

This will supply a fixed values for severity level and period as command
line arguments. You can specify your own arguments like so:
```shell
./gradlew :quakemap:run --args="4.5 week"
```

[usgs]: https://earthquake.usgs.gov/earthquakes/feed/
[clikt]: https://ajalt.github.io/clikt/
