package com.bluesierralabs.freewayforecast.Helpers;

import com.squareup.otto.Bus;

/**
 * Created by timothy on 1/10/15.
 */
public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances
    }
}
