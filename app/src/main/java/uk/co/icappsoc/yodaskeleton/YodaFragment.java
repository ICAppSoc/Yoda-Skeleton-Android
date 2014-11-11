package uk.co.icappsoc.yodaskeleton;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Converts English to Yodish via network connection to YodaSpeak.
 */
public class YodaFragment extends Fragment {
    /** The tag that will be used for logging. */
    private final String LOG_TAG = "YodaFragment";

    /* Called when the UI is ready to be created. You return the root View for this Fragment. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get reference to the English input field.
        final EditText englishText = (EditText) rootView.findViewById(R.id.english_text);

        // TODO get reference to a button
        // TODO add OnClickListener to our button
        // TODO convert text in our EditText to Yodish on button click

        return rootView;
    }

    /**
     * Converts English to Yodish.<p>
     * Don't fret about the contents of this method, just take a look at its inputs & outputs.
     */
    public String convertToYodish(String englishIn){
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the Yodish response as a string.
        String yodishOut = null;

        try {
            // Construct the URL for the YodaSpeak query.
            // The API takes a single parameter, named 'sentence'.
            Uri builtUri = Uri.parse("https://yoda.p.mashape.com/yoda").buildUpon()
                    .appendQueryParameter("sentence", englishIn)
                    .build();
            URL url = new URL(builtUri.toString());

            // Create the request to YodaSpeak, and open the connection.
            // The API requires a header with a Mashape key such that it can track users.
            // Ideally, use your own; if everyone uses mine, it may be restricted and / or I may
            // revoke the key in the future.
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("X-Mashape-Key",
                    "F9v1zCq18Pmshx40bfpKvhiEYzmPp1Zw4najsnVsMGMy1R6VOR");
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String.
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            yodishOut = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code did not manage to get in touch with Yoda, there's no point in
            // displaying the output.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return yodishOut;
    }
}
