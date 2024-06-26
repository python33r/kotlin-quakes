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

    "String representation shows level & period values" {
        feed.toString() shouldBe """QuakeFeed(level="4.5", period="week")"""
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
