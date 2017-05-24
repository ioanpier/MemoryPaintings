package gr.ioanpier.auth.users.memorypaintings;
/*
Copyright {2016} {Ioannis Pierros (ioanpier@gmail.com)}

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (((MainActivityFragment) getSupportFragmentManager().getFragments().get(0)).getLevel() == 0 ){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.instructions)
                    .setNegativeButton(getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .setMessage(getString(R.string.instructions_message));

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public void roundEnded() {
        Button nextLevelButton = (Button) findViewById(R.id.nextLevelButton);
        Button replayLevelButton = (Button) findViewById(R.id.replayLevelButton);

        nextLevelButton.setVisibility(View.VISIBLE);
        replayLevelButton.setVisibility(View.VISIBLE);

        final int level = ((MainActivityFragment) getSupportFragmentManager().getFragments().get(0)).getLevel();
        final String LEVEL_TAG = ((MainActivityFragment) getSupportFragmentManager().getFragments().get(0)).getLevelTag();
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
        final int level = ((MainActivityFragment) getSupportFragmentManager().getFragments().get(0)).getLevel();
        final String LEVEL_TAG = ((MainActivityFragment) getSupportFragmentManager().getFragments().get(0)).getLevelTag();

        //Returns the player to the first level or terminates the game if he is already there.
        if (level != 0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(LEVEL_TAG, 0);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
