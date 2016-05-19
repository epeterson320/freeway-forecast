package co.ericp.freewayforecast

import org.junit.Test
import java.lang.Math.PI
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.*

class LatLngTest {
    @Test fun distances() {
        val northPole = LatLon(90.0, 0.0)
        val southPole = LatLon(-90.0, 0.0)
        val eq0lon = LatLon(0.0, 0.0)
        val eq90lon = LatLon(0.0, 90.0)

        val c = LatLon.r * 2 * PI // Earth's circumference

        assertThat("North pole to South",
                LatLon.dist(northPole, southPole),
                `is`(c / 2))
        assertThat("North pole to equator",
                LatLon.dist(northPole, eq0lon),
                `is`(c / 4))
        assertThat("90 Degrees around equator",
                LatLon.dist(eq0lon, eq90lon),
                `is`(c / 4))
    }
}
