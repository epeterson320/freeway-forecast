package co.ericp.freewayforecast

import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.Math.PI

class LatLngTest {
    @Test fun distances() {
        val northPole = Location(90.0, 0.0)
        val southPole = Location(-90.0, 0.0)
        val eq0lon = Location(0.0, 0.0)
        val eq90lon = Location(0.0, 90.0)

        val c = Location.r * 2 * PI // Earth's circumference

        val n2s = Location.dist(northPole, southPole).toDouble()
        assertEquals("North pole to South", n2s, c / 2, 2.0)

        val n2e = Location.dist(northPole, eq0lon).toDouble()
        assertEquals("North pole to equator", n2e, c / 4, 2.0)

        val e2e90 = Location.dist(eq0lon, eq90lon).toDouble()
        assertEquals("90 Degrees around equator", e2e90, c / 4, 2.0)
    }
}
