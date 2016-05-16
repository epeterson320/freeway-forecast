package co.ericp.freewayforecast

import co.ericp.freewayforecast.routes.Route
import co.ericp.freewayforecast.routes.Step
import org.junit.Test

class RouteForecastSourceImplTest {
    val minute = 1000L * 60L
    val anyLatLng = LatLng(0.0, 0.0)
    val anyLoc = Location("Any Location", anyLatLng)

    /*
    r1 s1 60m  0 0  1 0.75  2 1
    r1 s2 60m  2 1  3 0.75  4 0
    r2 s1 50m  0 0  2 0
    r2 s2 50m  2 0  4 0

    r1 exp pts at 0 0  2 1  4 0
    r2 exp pts at 0 0  2 0  4 0*/
    @Test fun weatherPointLocations1() {
        //val r1s1 = Step
        //val leg
        //val route1 = Route(4, 120 * minute, 0, 0, anyLatLng, anyLatLng, anyLoc)
    }

    /*
    r1 s1 25m 0 0  0 1
    r1 s2 30m 0 1  0 2  0 3  0 4
    r1 s3 35m 0 4  0 5
    r1 s4 20m 0 5  0 6
    r1 s5 15m 0 6  0 7
    r1 s6 25m 0 7  0 8

    r1 exp pts at 0 0  0 3.5  0 5.5  0 8*/
    @Test fun weatherPointLocations2() {

    }
}
