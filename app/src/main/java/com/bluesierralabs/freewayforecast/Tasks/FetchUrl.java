package com.bluesierralabs.freewayforecast.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Fetches data from url passed
 *
 * Created by timothy on 12/27/14.
 */
public class FetchUrl extends AsyncTask<String, Void, String>
{
    // Downloading data in non-ui thread
    @Override
    protected String doInBackground(String... url)
    {
        // For storing data from web service
        String data = "";

        Log.e("FetchUrl","starting doInBackground");

        try
        {
            // Fetching the data from web service
            data = downloadUrl(url[0]);
        } catch (Exception e)
        {
            Log.d("Background Task", e.toString());
        }

        Log.e("FetchUrl","finished doInBackground");
        return data;
    }

    // Executes in UI thread, after the execution of doInBackground()
    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);

        Log.e("FetchUrl","starting onPostExecute");

//        ParserTask parserTask = new ParserTask();
        FetchRoutesTask routesTask = new FetchRoutesTask();

        // Invokes the thread for parsing the JSON data
//        parserTask.execute(result);

        Log.e("FetchUrl","starting the routes task");
        routesTask.execute(result);
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException
    {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try
        {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e)
        {
            Log.d("Exception while downloading url", e.toString());
        } finally
        {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
