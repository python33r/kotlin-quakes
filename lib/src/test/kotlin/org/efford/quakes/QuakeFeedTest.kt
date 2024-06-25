package org.efford.quakes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@Suppress("unused")
class QuakeFeedTest: StringSpec({
    val feed = QuakeFeed("4.5", "week")

    "Can create a feed with level & period" {
        withClue("Level") { feed.level shouldBe "4.5" }
        withClue("Period") { feed.period shouldBe "week" }
    }

    "String representation is the source URL" {
        feed.toString() shouldBe "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_week.csv"
    }

    "Exception if severity level is invalid" {
        withClue("Empty string") {
            shouldThrow<IllegalArgumentException> { QuakeFeed("", "week") }
        }
        withClue("Level=\"4.6\"") {
            shouldThrow<IllegalArgumentException> { QuakeFeed("4.6", "week") }
        }
    }

    "Exception if time period is invalid" {
        withClue("Empty string") {
            shouldThrow<IllegalArgumentException> { QuakeFeed("4.5", "") }
        }
        withClue("Period=\"year\"") {
            shouldThrow<IllegalArgumentException> { QuakeFeed("4.5", "year") }
        }
    }
})
