package co.ericp.freewayforecast

import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.Math.PI

class GeoCalculatorTest {
    val northPole = Location(90.0, 0.0)
    val southPole = Location(-90.0, 0.0)
    val eq0lon = Location(0.0, 0.0)
    val eq90lon = Location(0.0, 90.0)

    val c = GeoCalculator.r * 2 * PI // Earth's circumference

    @Test fun distances() {
        val n2s = GeoCalculator.dist(northPole, southPole)
        assertEquals("North pole to South", n2s, c / 2, 2.0)

        val n2e = GeoCalculator.dist(northPole, eq0lon)
        assertEquals("North pole to equator", n2e, c / 4, 2.0)

        val e2e90 = GeoCalculator.dist(eq0lon, eq90lon)
        assertEquals("90 Degrees around equator", e2e90, c / 4, 2.0)
    }

    @Test fun travel() {
        val eqTo45Dist = c / 8
        val eq2n = GeoCalculator.travel(northPole, eq0lon, eqTo45Dist)
        assertEquals("Equator to north pole latitude", eq2n.lat, 45.0, 0.05)
    }
}
