package co.ericp.freewayforecast

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
        LatLngTest::class,
        RouteForecastSourceImplTest::class
)
class CoreTestSuite

