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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class WelcomeScreen extends Activity {

    private static final String LOG = "WelcomeScreen: ";
    public static String IMAGES_PATH;
    public static String DESCRIPTIONS_PATH;

    private static int numOfTasks = 13;
    private final static Object numOfTasksLock = new Object();
    private int englishButtonHashCode;
    private int screenWidth;
    private int screenHeight;

    @SuppressWarnings("FieldCanBeLocal")
    private final int waitTime = 1000; //milliseconds
    @SuppressWarnings("FieldCanBeLocal")
    private final int loadingBarWaitTime = 500; //milliseconds
    private int imageToLoad = 0;

    private int[] imagesInt = {
            R.drawable.a1,
            R.drawable.a2,
            R.drawable.a3,
            R.drawable.a4,
            R.drawable.a5,
            R.drawable.a6,
            R.drawable.a7,
            R.drawable.a8,
            R.drawable.a9,
            R.drawable.a10,
            R.drawable.a11,
            R.drawable.a12,
            R.drawable.a13
    };

    private String[] imagesName = {
            "a1",
            "a2",
            "a3",
            "a4",
            "a5",
            "a6",
            "a7",
            "a8",
            "a9",
            "a10",
            "a11",
            "a12",
            "a13"
    };

    private Integer[] imagesDesc = {
            R.string.a1,
            R.string.a2,
            R.string.a3,
            R.string.a4,
            R.string.a5,
            R.string.a6,
            R.string.a7,
            R.string.a8,
            R.string.a9,
            R.string.a10,
            R.string.a11,
            R.string.a12,
            R.string.a13
    };

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        Button english = (Button) findViewById(R.id.english);
        //Button polish = (Button) findViewById(R.id.polish);

        englishButtonHashCode = english.hashCode();

        english.setOnClickListener(languageClickListener);
        //polish.setOnClickListener(languageClickListener);

        //Create the directories that will hold the images and the descriptions.
        //Android Studio warns of potential null pointer exception when calling mkdirs
        //I haven't found such a case, but to be sure I check it and make it ./pictures/images, ./pictures/descriptions if necessary.
        File directory = getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);
        if (directory == null) {
            directory = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Images");
        }
        if (!directory.mkdirs() && !directory.isDirectory()) {
            Log.e(LOG, "Directory not created");
        }
        IMAGES_PATH = directory.getPath();

        directory = getExternalFilesDir(
                "Descriptions");
        if (directory == null) {
            directory = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Descriptions");
        }
        if (!directory.mkdirs() && !directory.isDirectory()) {
            Log.e(LOG, "Directory not created");
        }
        DESCRIPTIONS_PATH = directory.getPath();


        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels / 2;
        screenHeight = displayMetrics.heightPixels / 2;
        numOfTasks = imagesInt.length;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("firstTime", true)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();
            saveDescriptions();
            new WaitForLoad(this).execute();
            saveImages();
        }

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

    private void saveImages() {
        if (imageToLoad == imagesInt.length) {
            Toast.makeText(getApplicationContext(), "Images have finished loading", Toast.LENGTH_SHORT).show();
            findViewById(R.id.wait_prompt).setVisibility(View.GONE);
            return;
        }
        Log.v(LOG, LOG + "Saving images");

        if (savingImage) {
            Log.v(LOG, LOG + "Another image is already being extracted. Wait " + waitTime + " ms.");
            new AsyncTask<Integer, Void, Void>() {
                @Override
                protected Void doInBackground(Integer... params) {
                    try {
                        Thread.sleep(params[0]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    saveImages();
                }
            }.execute(waitTime);
            return;
        }
        savingImage = true;
        Log.v(LOG, LOG + "Extracting image " + imageToLoad);
        Glide.with(this)
                .load(imagesInt[imageToLoad])
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(screenWidth, screenHeight) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        Log.v(LOG, LOG + "On resource ready");
                        Bundle args = new Bundle();
                        args.putParcelable(PARCELABLE_BITMAP, bitmap);
                        getLoaderManager().restartLoader(0, args, new LoaderManager.LoaderCallbacks<Void>() {
                            @Override
                            public Loader<Void> onCreateLoader(int id, Bundle args) {
                                Log.v(LOG, LOG + "onCreateLoader");
                                Bitmap bitmap = args.getParcelable(PARCELABLE_BITMAP);
                                return new SaveInBackground(getApplicationContext(), bitmap, imagesName[imageToLoad] + ".jpg");
                            }

                            @Override
                            public void onLoadFinished(Loader loader, Void data) {
                                Log.v(LOG, LOG + "onLoadFinished");
                                imageToLoad++;
                                savingImage = false;
                                saveImages();
                            }

                            @Override
                            public void onLoaderReset(Loader loader) {
                                Log.v(LOG, LOG + "onLoaderReset");
                            }
                        });

                    }
                });


    }

    private void saveDescriptions() {
        for (int i = 0; i < imagesDesc.length; i++) {
            FileOutputStream outStream;

            File directory = new File(DESCRIPTIONS_PATH);

            File file = new File(directory, imagesName[i] + ".txt");
            Log.v(LOG, LOG + file.getAbsolutePath());

            outStream = null;
            try {
                outStream = new FileOutputStream(file);
                outStream.write(getResources().getString(imagesDesc[i]).getBytes());
                Log.v(LOG, LOG + "Description " + i + "was saved successfully");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.v(LOG, LOG + "Description " + i + "file not found");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (outStream != null)
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


        }
    }

    private static final String PARCELABLE_BITMAP = "PARCELABLE_BITMAP";
    private boolean savingImage = false;

    private class WaitForLoad extends AsyncTask<Void, Void, Void> {
        Context context;
        boolean loading;
        ProgressDialog dialog;

        WaitForLoad(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage("Please wait");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(imagesInt.length);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            loading = true;
            while (loading) {
                try {
                    Thread.sleep(loadingBarWaitTime);
                } catch (InterruptedException e) {
                }
                synchronized (numOfTasksLock) {
                    dialog.setProgress(imagesInt.length - numOfTasks);
                    if (numOfTasks == 0) {
                        loading = false;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            TextView tv = (TextView) findViewById(R.id.wait_prompt);
            tv.setVisibility(View.GONE);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(MainActivityFragment.LANGUAGE, "en");
            startActivity(intent);
        }
    }

    private static class SaveInBackground extends AsyncTaskLoader<Void> {
        private Bitmap bitmap;
        private String filename;

        SaveInBackground(Context c, Bitmap bitmap, String filename) {
            super(c);
            Log.v(LOG, LOG + "SaveInBackground constructor");
            this.bitmap = bitmap;
            this.filename = filename;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Override
        public Void loadInBackground() {
            Log.v(LOG, LOG + "SaveInBackground loadInBackground");
            FileOutputStream outStream;
            File directory = new File(IMAGES_PATH);
            File file = new File(directory, filename);
            Log.v(LOG, LOG + "Path: " + file.getAbsolutePath());

            outStream = null;
            try {
                outStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (outStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                synchronized (numOfTasksLock) {
                    Log.v(LOG, LOG + "Bitmap compressed " + numOfTasks);
                }

                //update the gallery
                final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                final Uri contentUri = Uri.fromFile(file);
                scanIntent.setData(contentUri);
                getContext().sendBroadcast(scanIntent);


                try {
                    outStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(LOG, LOG + "stream didn't open");
            }
            synchronized (numOfTasksLock) {
                numOfTasks--;
            }
            Log.v(LOG, LOG + "Saving image has finished.");
            return null;
        }
    }

}
