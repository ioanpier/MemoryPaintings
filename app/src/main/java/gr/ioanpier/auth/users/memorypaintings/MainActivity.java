package gr.ioanpier.auth.users.memorypaintings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements  MainActivityFragment.Callback{

    private Button nextLevelButton;
    private Button replayLevelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nextLevelButton = (Button)findViewById(R.id.nextLevelButton);
        replayLevelButton = (Button)findViewById(R.id.replayLevelButton);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void roundEnded() {
        nextLevelButton.setVisibility(View.VISIBLE);
        replayLevelButton.setVisibility(View.VISIBLE);

        final int level = ((MainActivityFragment)getSupportFragmentManager().getFragments().get(0)).getLevel();
        final String LEVEL_TAG = ((MainActivityFragment)getSupportFragmentManager().getFragments().get(0)).getLevelTag();
        final Context context = this;

        nextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(LEVEL_TAG, level + 1);
                startActivity(intent);
                finish();
            }
        });

        replayLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(LEVEL_TAG, level);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        final int level = ((MainActivityFragment)getSupportFragmentManager().getFragments().get(0)).getLevel();
        final String LEVEL_TAG = ((MainActivityFragment)getSupportFragmentManager().getFragments().get(0)).getLevelTag();
        if (level!=0)
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(LEVEL_TAG, 0);
            startActivity(intent);
            finish();
        }
        else{
            super.onBackPressed();
        }
    }
}
