package com.vitorarrais.spotify_streamer.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vitorarrais.spotify_streamer.App;
import com.vitorarrais.spotify_streamer.R;

/**
 * Represents the launcher screen with a simple search field.
 */
public class HomeActivity extends AppCompatActivity {

    /**
     * The TextView with the app name, superior.
     */
    TextView mHomeAppNameAbove;

    /**
     * The TextView with the app name, inferior.
     */
    TextView mHomeAppNameBelow;

    /**
     * The search field.
     */
    EditText mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mHomeAppNameAbove = (TextView) findViewById(R.id.home_app_name_above);
        mHomeAppNameBelow = (TextView) findViewById(R.id.home_app_name_below);

        // define font to app name displayed on the home screen
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Black.ttf");
        mHomeAppNameAbove.setTypeface(typeFace);
        mHomeAppNameBelow.setTypeface(typeFace);

        mSearch = (EditText) findViewById(R.id.search_editText);

        mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            /*if (actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_GO
                    || actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_NEXT) {*/

                // verify whether something was typed in the search field
                if (mSearch.getText().length()==0) {
                    Toast.makeText(HomeActivity.this, R.string.non_empty, Toast.LENGTH_LONG).show();
                } else {
                    // start the main activity
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(App.EXTRA_STRING_NAME_TAG, mSearch.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

}
