package org.efford.quakes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import kotlinx.datetime.Clock

@Suppress("unused")
class QuakeTest: StringSpec({
    val time = Clock.System.now()

    "Exception if longitude < -180 degrees" {
        shouldThrow<IllegalArgumentException> {
            Quake(time, -180.5, 0.0, 5.0, 5.0)
        }
    }

    "Exception if longitude > 180 degrees" {
        shouldThrow<IllegalArgumentException> {
            Quake(time, 180.5, 0.0, 5.0, 5.0)
        }
    }

    "Exception if latitude < -90 degrees" {
        shouldThrow<IllegalArgumentException> {
            Quake(time, 0.0, -90.5, 5.0, 5.0)
        }
    }

    "Exception if latitude > 90 degrees" {
        shouldThrow<IllegalArgumentException> {
            Quake(time, 0.0, 90.5, 5.0, 5.0)
        }
    }

    "Exception if depth is invalid" {
        shouldThrow<IllegalArgumentException> {
            Quake(time, 0.0, 0.0, -1.0, 5.0)
        }
    }

    "Exception if magnitude is invalid" {
        shouldThrow<IllegalArgumentException> {
            Quake(time, 0.0, 0.0, 5.0, 0.0)
        }
        shouldThrow<IllegalArgumentException> {
            Quake(time, 0.0, 0.0, 5.0, -1.0)
        }
    }
})
