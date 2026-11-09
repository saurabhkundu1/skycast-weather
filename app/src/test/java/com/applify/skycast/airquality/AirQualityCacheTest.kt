package com.applify.skycast.airquality

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AirQualityCacheTest {
    @Test fun keepsFiveNewestPlaceEntriesAndRoundTripsEachPlace() {
        val caches = (1..6).associate { "place$it" to AirQualityReading(it, it.toDouble(), it.toLong()) }
        val restored = decodeAirQualityCaches(encodeAirQualityCaches(caches))
        assertEquals(setOf("place2", "place3", "place4", "place5", "place6"), restored.keys)
        assertEquals(6, restored.getValue("place6").europeanAqi)
        assertFalse(restored.getValue("place6").isStale)
    }

    @Test fun migratesOldSinglePlaceCache() {
        val restored = decodeAirQualityCaches("""{"location":"home","fetchedAt":100,"europeanAqi":12,"pm25":3.5}""")
        assertEquals(12, restored.getValue("home").europeanAqi)
        assertEquals(3.5, restored.getValue("home").pm25, 0.0)
    }

    @Test fun malformedPlaceEntryDoesNotDiscardOtherCachedPlaces() {
        val restored = decodeAirQualityCaches(
            """{"home":{"fetchedAt":100,"europeanAqi":12,"pm25":3.5},"broken":{"europeanAqi":"bad"}}"""
        )
        assertEquals(setOf("home"), restored.keys)
        assertEquals(12, restored.getValue("home").europeanAqi)
    }

    @Test fun staleFallbackRejectsExpiredAndFutureReadings() {
        val fresh = AirQualityReading(10, 2.0, 1_000)
        assertTrue(staleAirQualityCache(fresh, 1_001)?.isStale == true)
        assertNull(staleAirQualityCache(fresh, 1_000 + 24 * 60 * 60 * 1000 + 1))
        assertNull(staleAirQualityCache(fresh, 999))
    }
}
