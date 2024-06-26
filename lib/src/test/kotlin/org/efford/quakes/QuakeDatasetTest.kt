package org.efford.quakes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

const val TOLERANCE = 0.000001

@Suppress("LongLine")
const val dummyData = """time,latitude,longitude,depth,mag,magType,nst,gap,dmin,rms,net,id,updated,place,type,horizontalError,depthError,magError,magNst,status,locationSource,magSource
2024-06-24T13:36:06.753Z,-21.9489,-179.5316,588.529,5.1,mb,93,49,4.76,0.74,us,us7000muc2,2024-06-24T14:01:12.040Z,"Fiji region",earthquake,10.66,6.404,0.024,559,reviewed,us,us
2024-06-24T09:55:02.164Z,-7.093,129.9983,103.228,5.3,mww,92,28,2.555,1.36,us,us7000mua7,2024-06-24T10:45:55.291Z,"Kepulauan Babar, Indonesia",earthquake,7.37,6.582,0.103,9,reviewed,us,us
2024-06-24T08:03:38.359Z,-14.6086,167.2485,156.689,6.3,mww,74,32,6.549,0.86,us,us7000mu8s,2024-06-24T08:29:26.993Z,"51 km NNE of Port-Olry, Vanuatu",earthquake,8.95,6.191,0.033,90,reviewed,us,us
2024-06-24T05:36:08.316Z,40.9706,84.2612,10,4.6,mb,86,87,4.261,0.81,us,us7000mu88,2024-06-24T07:06:52.040Z,"138 km SE of Kuqa, China",earthquake,8.06,1.908,0.063,75,reviewed,us,us
2024-06-24T05:04:35.566Z,12.3058,125.46,30.312,4.8,mb,52,72,11.715,0.8,us,us7000mu87,2024-06-24T05:48:45.040Z,"7 km NE of Arteche, Philippines",earthquake,11.02,5.597,0.066,71,reviewed,us,us"""

@Suppress("unused")
class QuakeDatasetTest: StringSpec({
    isolationMode = IsolationMode.InstancePerTest

    val empty = QuakeDataset()

    val dummyFeed = mock<QuakeFeed> {
        on { read() } doReturn dummyData
    }

    val quakes = QuakeDataset().apply { update(dummyFeed) }

    "Dataset is empty before updating" {
        withClue("Empty?") { empty.isEmpty() shouldBe true }
        withClue("Size") { empty.size shouldBe 0 }
    }

    "Cannot access individual quakes in an empty dataset" {
        shouldThrow<IndexOutOfBoundsException> {
            empty[0]
        }
    }

    "No shallowest quake for an empty dataset" {
        empty.shallowestQuake shouldBe null
    }

    "No deepest quake for an empty dataset" {
        empty.deepestQuake shouldBe null
    }

    "No weakest quake for an empty dataset" {
        empty.weakestQuake shouldBe null
    }

    "No strongest quake for an empty dataset" {
        empty.strongestQuake shouldBe null
    }

    "No mean depth for an empty dataset" {
        empty.meanDepth shouldBe null
    }

    "No mean magnitude for an empty dataset" {
        empty.meanMagnitude shouldBe null
    }

    "Dataset can be updated from a feed" {
        withClue("Empty?") { quakes.isEmpty() shouldBe false }
        withClue("Size") { quakes.size shouldBe 5 }
    }

    "Can access individual quakes in a dataset" {
        withClue("Lon") { quakes[0].longitude shouldBe -179.5316 plusOrMinus TOLERANCE }
        withClue("Lat") { quakes[0].latitude shouldBe -21.9489 plusOrMinus TOLERANCE }
        withClue("Mag") { quakes[0].magnitude shouldBe 5.1 plusOrMinus TOLERANCE }
        withClue("Depth") { quakes[0].depth shouldBe 588.529 plusOrMinus TOLERANCE }
    }

    "Shallowest quake found successfully" {
        with(quakes.shallowestQuake) {
            withClue("Depth") { this?.depth?.shouldBe(10.0)?.plusOrMinus(TOLERANCE) }
            withClue("Lon") { this?.longitude?.shouldBe(84.2612)?.plusOrMinus(TOLERANCE) }
            withClue("Lat") { this?.latitude?.shouldBe(40.9706)?.plusOrMinus(TOLERANCE) }
        }
    }

    "Deepest quake found successfully" {
        with(quakes.deepestQuake) {
            withClue("Depth") { this?.depth?.shouldBe(588.529)?.plusOrMinus(TOLERANCE) }
            withClue("Lon") { this?.longitude?.shouldBe(-179.5316)?.plusOrMinus(TOLERANCE) }
            withClue("Lat") { this?.latitude?.shouldBe(-21.9489)?.plusOrMinus(TOLERANCE) }
        }
    }

    "Weakest quake found successfully" {
        with(quakes.weakestQuake) {
            withClue("Mag") { this?.magnitude?.shouldBe(4.6)?.plusOrMinus(TOLERANCE) }
            withClue("Lon") { this?.longitude?.shouldBe(84.2612)?.plusOrMinus(TOLERANCE) }
            withClue("Lat") { this?.latitude?.shouldBe(40.9706)?.plusOrMinus(TOLERANCE) }
        }
    }

    "Strongest quake found successfully" {
        with(quakes.strongestQuake) {
            withClue("Mag") { this?.magnitude?.shouldBe(6.3)?.plusOrMinus(TOLERANCE) }
            withClue("Lon") { this?.longitude?.shouldBe(167.2485)?.plusOrMinus(TOLERANCE) }
            withClue("Lat") { this?.latitude?.shouldBe(-14.6086)?.plusOrMinus(TOLERANCE) }
        }
    }

    "Mean depth computed correctly" {
        quakes.meanDepth?.shouldBe(177.7516)?.plusOrMinus(TOLERANCE)
    }

    "Mean magnitude computed correctly" {
        quakes.meanMagnitude?.shouldBe(5.22)?.plusOrMinus(TOLERANCE)
    }
})
