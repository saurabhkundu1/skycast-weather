package com.applify.skycast.airquality

import com.applify.skycast.R
import org.junit.Assert.assertEquals
import org.junit.Test

class AirQualityBandTest {
    @Test fun usesEuropeanAqiBandUpperLimits() {
        assertEquals(R.string.air_quality_band_good, airQualityBand(20))
        assertEquals(R.string.air_quality_band_fair, airQualityBand(21))
        assertEquals(R.string.air_quality_band_moderate, airQualityBand(41))
        assertEquals(R.string.air_quality_band_poor, airQualityBand(61))
        assertEquals(R.string.air_quality_band_very_poor, airQualityBand(81))
        assertEquals(R.string.air_quality_band_very_poor, airQualityBand(100))
        assertEquals(R.string.air_quality_band_extremely_poor, airQualityBand(101))
    }
}
