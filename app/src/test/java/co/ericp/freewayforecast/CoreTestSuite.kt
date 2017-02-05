package co.ericp.freewayforecast

import co.ericp.freewayforecast.routeForecast.GeoCalculatorTest
import co.ericp.freewayforecast.routeForecast.RouteForecastSourceImplTest
import co.ericp.freewayforecast.routes.GoogleMapsRouteSourceTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
        GeoCalculatorTest::class,
        RouteForecastSourceImplTest::class,
        GoogleMapsRouteSourceTest::class
)
class CoreTestSuite

