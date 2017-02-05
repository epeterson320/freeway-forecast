package co.ericp.freewayforecast

import android.content.Context
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks.*
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.LocationServices
import io.reactivex.Observable

object GoogleApiClientSource {

    var cachedObs: Observable<GoogleApiClient>? = null

    fun getClient(context: Context): Observable<GoogleApiClient> {
        cachedObs?.apply {
            return this
        }
        return getNewClientRx(context).apply {
            cachedObs = this
            this.doOnTerminate { cachedObs = null }
        }
    }

    fun getNewClientRx(context: Context): Observable<GoogleApiClient> {
        return Observable.create<GoogleApiClient> { emitter ->

            val client: GoogleApiClient = GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .build()

            val callbacks = object : ConnectionCallbacks {
                override fun onConnected(p0: Bundle?) {
                    emitter.onNext(client)
                    client.unregisterConnectionCallbacks(this)
                }

                override fun onConnectionSuspended(cause: Int) {
                    when (cause) {
                        CAUSE_NETWORK_LOST -> emitter.onError(Error())
                        CAUSE_SERVICE_DISCONNECTED -> emitter.onError(Error())
                    }
                    client.unregisterConnectionCallbacks(this)
                }
            }

            val listener = object : OnConnectionFailedListener {
                override fun onConnectionFailed(result: ConnectionResult) {
                    emitter.onError(Error())
                    client.unregisterConnectionFailedListener(this)
                }
            }

            client.registerConnectionCallbacks(callbacks)
            client.registerConnectionFailedListener(listener)

            emitter.setCancellable {
                client.disconnect()
                client.unregisterConnectionCallbacks(callbacks)
                client.unregisterConnectionFailedListener(listener)
            }

            client.connect()
        }.share()
    }
}