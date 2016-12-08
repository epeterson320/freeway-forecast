package co.ericp.freewayforecast

import org.junit.Assert.assertEquals
import org.junit.Test

class GeoCalculatorTest {
    val km = 1000.0
    val kmPerMi = 1.60934
    val northPole = Location(90.0, 0.0)
    val southPole = Location(-90.0, 0.0)
    val eq0lon = Location(0.0, 0.0)
    val eq90lon = Location(0.0, 90.0)

    val equatorialC = 40075.017 * km
    val meridionalC = 40007.860 * km

    @Test fun bigDistances() {
        val calcN2S = GeoCalculator.dist(northPole, southPole)
        val expN2S = meridionalC / 2
        assertEquals("North pole to South", expN2S, calcN2S, 1 * km)

        val calcN2E = GeoCalculator.dist(northPole, eq0lon)
        val expN2E = meridionalC / 4
        assertEquals("North pole to equator", expN2E, calcN2E, 1 * km)

        val calcE2e90 = GeoCalculator.dist(eq0lon, eq90lon)
        val expE2e90 = equatorialC / 4
        assertEquals("90 Degrees around equator", expE2e90, calcE2e90, 1 * km)
    }

    @Test fun smallDistances() {
        val sperryville = Location(38.659090, -78.230307) // Lee Hwy & Sperryville Pike
        val culpeper = Location(38.476555, -77.994917) // Sperryville Pike & Main

        val calcDist = GeoCalculator.dist(sperryville, culpeper)

        val expDist = 19.6 * kmPerMi * km // Google Maps route
        val delta = 5 * km // Big delta because it's not a straight line

        assertEquals("Sperryville to Culpeper", expDist, calcDist, delta)
    }

    @Test fun travel() {
        val eqTo45Dist = meridionalC / 8
        val eq2n = GeoCalculator.travel(northPole, eq0lon, eqTo45Dist)
        assertEquals("Equator to north pole latitude", 45.0, eq2n.lat, 0.15)
    }
}
