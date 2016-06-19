package gr.ioanpier.auth.users.memorypaintings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class WelcomeScreen extends Activity {

    private int englishButtonHashCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        Button english = (Button) findViewById(R.id.english);
        Button polish = (Button) findViewById(R.id.polish);

        englishButtonHashCode = english.hashCode();

        english.setOnClickListener(languageClickListener);
        polish.setOnClickListener(languageClickListener);

    }

    private final Button.OnClickListener languageClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            int hashCode = view.hashCode();
            String language;
            if (hashCode == englishButtonHashCode)
                language = "en";
            else
                language = "pl";

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(MainActivityFragment.LANGUAGE, language);
            startActivity(intent);


        }
    };
}
