package gr.ioanpier.auth.users.memorypaintings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;


/**
 * A placeholder fragment containing the screen and the logic of the game.
 * <p/>
 * Thumbnail: A small version of an image, used for the game.
 * Image: The actual image, used for fullscreen viewing.
 * Description: A description of the image, visible when viewing the image in fullscreen.
 * <p/>
 * The thumbnails, images and descriptions are loaded from the app's respective folders.
 * <p/>
 * Depending on the level, there is a different number of cards that need to be matched.
 *
 * @author Ioannis Pierros (ioanpier@gmail.com)
 */
public class MainActivityFragment extends Fragment {

    private final static String LOG = MainActivityFragment.class.getCanonicalName();
    private final static String LEVEL_TAG = MainActivityFragment.class.getSimpleName();
    public final static String LANGUAGE = "Language selection";

    //These variables are used for the dynamic creation of the level. They are chosen so that their number is odd and the screen is filled evenly.
    private final int[] layoutsPerLevel = {2, 2, 3, 4, 4, 4};
    private final int[] cardsPerLevel = {4, 6, 12, 16, 20, 24};
    private int level;

    /**
     * Holds a key information for the state of the game. Refer to onCardFlipped for its usage.
     */
    private boolean cardFlipped = false;

    private final Object gameLogicLock = new Object();

    /**
     * The index of the card currently showing (used in conjunction with cardFlipped variable)
     */
    private int card;

    /**
     * Holds the pairs of cards, reflected on ImageViewCard[] cards variable. For example. the pair of the card cards[i] would be cards[pairs[i]]
     */
    private int[] pairs;
    /**
     * List of the indexes cards that have been found. Reflects the indexes of ImageViewCard[] cards.
     */
    private boolean[] found;
    /**
     * The number of cards present at the round. This number is calculated according to cardsPerLevel[level]
     */
    private static int numberOfCards;

    /**
     * Counts the number of cards that the player has found. This number is the indicator for when the round ends.
     */
    private int numberOfCardsFound = 0;

    /**
     * The index of the Drawable from the drawables or drawablesBig for the card.
     * Also used for the respective String description from drawablesDesc.
     */
    private int[] cardDrawablePairs;

    /**
     * Each StoredImage holds the path for the thumbnail and the image for viewing in fullscreen as well as a description for the image.
     */
    @SuppressWarnings("WeakerAccess")
    StoredImage[] storedDrawables;

    /**
     * Holds the ViewFlippers (Actually their extension, ImageViewCard).
     */
    private ImageViewCard[] cards;

    /**
     * Holds the map from the ImageViewCard[] cards hashCode to their index;
     */
    private final HashMap<Integer, Integer> cardsIndex = new HashMap<>();

    /**
     * Each BitmapWorkerTask is responsible for loading an image. It is instantiated as soon as the number of cards is known.
     * It is decreased onPostExecute of each AsyncTask. Functionality is disabled until every image is loaded.
     */
    private static int numberOfBitmapWorkerTasks = 0;
    /**
     * A lock for synchronizing the numberOfBitmapWorkerTasks variable. There are more than on BitmapWorkerTasks running at the same time
     * which means they could feasibly be completed at the same time, with unwanted results to the game flow.
     */
    private final static Object numberOfBitmapWorkerTasksLock = new Object();

    /**
     * Boolean used to prevent multiple cards being flipped at the same time, which breaks the game flow.
     * If it's true, the card will not be flipped.
     */
    private boolean stillAnimating = false;

    /**
     * The default colors used in case there aren't enough photos to fill the available cards for the level.
     */
    private final Integer[] colors = {
            R.color.color1,
            R.color.color2,
            R.color.color3,
            R.color.color4,
            R.color.color5,
            R.color.color6,
            R.color.color7,
            R.color.color8,
            R.color.color9,
            R.color.color10,
            R.color.color11,
            R.color.color12,
            R.color.color13,
            R.color.color14,
            R.color.color15,
            R.color.color16,
            R.color.color17,
            R.color.color18,
            R.color.color19,
            R.color.color20,
            R.color.color21,
            R.color.color22,
            R.color.color23,
            R.color.color24,
            R.color.color25,
            R.color.color26,
            R.color.color27,
            R.color.color28,
            R.color.color29,
            R.color.color30,
            R.color.color31,
            R.color.color32,
            R.color.color33,
            R.color.color34,
            R.color.color35,
            R.color.color36,
            R.color.color37,
            R.color.color38,
            R.color.color39,
            R.color.color40};

    /**
     * Callback interface.
     */
    public interface Callback {
        /**
         * Fired when the round has ended.
         */
        void roundEnded();
    }

    /**
     * Getter method for the level variable
     *
     * @return the current level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Getter method for the LEVEL_TAG variable
     *
     * @return the LEVEL_TAG String
     */
    public String getLevelTag() {
        return LEVEL_TAG;
    }

    /**
     * Default constructor
     */
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Language Locale
        String languageToLoad = getActivity().getIntent().getStringExtra(LANGUAGE); // your language
        if (languageToLoad != null) {
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());
        }


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Calculate the level and the number of cards.
        level = getActivity().getIntent().getIntExtra(LEVEL_TAG, 0);
        if (level >= layoutsPerLevel.length)
            level = layoutsPerLevel.length - 1;
        numberOfCards = cardsPerLevel[level];


        numberOfBitmapWorkerTasks = numberOfCards / 2;
        instantiateLoadingBar();

        if (isExternalStorageReadable()) {
            File[] files1 = getFilesFromDirectory(getActivity(), "Thumbnails", ".jpg", ".png");
            File[] files2 = getFilesFromDirectory(getActivity(), "Images", ".jpg", ".png");

            String descriptionsFolder = "Descriptions";
            Locale locale = getActivity().getResources().getConfiguration().locale;
            if (locale.getDisplayLanguage().equals(Locale.ENGLISH.getDisplayLanguage()))
                descriptionsFolder = descriptionsFolder.concat("_en");
            else
                descriptionsFolder = descriptionsFolder.concat("_pl");
            File[] files3 = getFilesFromDirectory(getActivity(), descriptionsFolder, ".txt");

            getDrawables(files1, files2, files3);
        } else {
            Log.e(LOG, "External storage wasn't readable");
            storedDrawables = null;
        }

        pairs = new int[numberOfCards];
        for (int i = 0; i < numberOfCards; i++) {
            pairs[i] = -1;
        }
        cards = new ImageViewCard[numberOfCards];

        //This is where the layout magic happens.
        LinearLayout linearLayout;
        int index;
        for (int i = 0; i < layoutsPerLevel[level]; i++) {
            //The layout consists of multiple vertical LinearLayout[s] positioned horizontally next to each other.
            linearLayout = new LinearLayout(getActivity());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            for (int j = 0; j < numberOfCards / layoutsPerLevel[level]; j++) {
                //Each LinearLayout has a number of ImageViewCard[s], each positioned evenly in inside the layout. The number depends on the level.
                //ImageViewCard is an extension of the ViewFlipper class with a built in flipCard method for flipping between 2 images and which also includes animation.
                ImageViewCard card = new ImageViewCard(getActivity());
                card.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
                ((LinearLayout.LayoutParams) card.getLayoutParams()).setMargins(16, 16, 16, 16);

                //SquareImageView is an extension of the ImageView class that ensures that the image is square.
                //Two are needed, one for the back of the image and one for the front.
                SquareImageView image1 = new SquareImageView(getActivity());
                image1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                SquareImageView image2 = new SquareImageView(getActivity());
                image2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                //Add the SquareImageView[s] to the ImageViewCard and subsequently that to the LinearLayout.
                card.addView(image1);
                card.addView(image2);
                index = i * numberOfCards / layoutsPerLevel[level] + j;
                linearLayout.addView(card);
                cardsIndex.put(card.hashCode(), index);

                //Set the back of the image.
                ((ImageView) card.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.black));

                //Save the ImageViewCard for later use.
                cards[index] = card;

            }
            //Add the LinearLayout to the rootView.
            ((LinearLayout) rootView.findViewById(R.id.parent)).addView(linearLayout);
        }

        //Assign a listener for every ImageViewCard.
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!stillAnimating && numberOfBitmapWorkerTasks == 0) {
                    int cardID = cardsIndex.get(view.hashCode());
                    stillAnimating = true;
                    synchronized (gameLogicLock) {
                        if (numberOfCardsFound < numberOfCards)
                            onCardFlipped(cardID);
                        else
                            onCardClicked(cardID);
                    }

                    //Add a delay before the listener can be activated again.
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void n) {
                            stillAnimating = false;
                        }
                    }.execute();

                } else if (numberOfBitmapWorkerTasks > 0) {
                    Toast.makeText(getActivity(), getString(R.string.loading_bar_message), Toast.LENGTH_SHORT).show();
                }

            }
        };

        for (int i = 0; i < numberOfCards; i++) {
            cards[i].setOnClickListener(onClickListener);
        }

        //Initialize
        found = new boolean[numberOfCards];
        for (int i = 0; i < numberOfCards; i++)
            found[i] = false;


        //Initialize
        int[] chosenDrawables = new int[numberOfCards / 2];
        cardDrawablePairs = new int[numberOfCards];

        //Initialize. Holds the index of every ImageViewCard in cards. Will later be used for the pairs.
        ArrayList<Integer> availablePairs = new ArrayList<>();
        for (int i = 0; i < numberOfCards; i++)
            availablePairs.add(i);
        Collections.shuffle(availablePairs);

        Random r = new Random();
        int pair1, pair2;
        BitmapWorkerTask bitmapWorkerTask;
        for (int i = 0; i < numberOfCards / 2; i++) {
            //Choose at random one of the available images. Make sure it's unique.
            int range;
            if (storedDrawables == null)
                range = colors.length;
            else
                range = storedDrawables.length;
            boolean unique = false;
            while (!unique) {
                unique = true;
                //If there are a lot of images, this should be changed (there will never be a lot)
                chosenDrawables[i] = r.nextInt(range);

                for (int j = 0; j < i; j++) {
                    if (chosenDrawables[i] == chosenDrawables[j])
                        unique = false;
                }
            }

            //availablePairs have already been shuffled, so just remove the first 2.
            pair1 = availablePairs.remove(0);
            pair2 = availablePairs.remove(0);

            cardDrawablePairs[pair1] = chosenDrawables[i];
            cardDrawablePairs[pair2] = chosenDrawables[i];

            //Assign the front of the ImageViewCard to the randomly chosen Drawable.
            ImageView imageView1, imageView2;
            String absolutePath;
            if (storedDrawables != null) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
                wm.getDefaultDisplay().getMetrics(displayMetrics);
                int screenWidth = displayMetrics.widthPixels / (layoutsPerLevel[level]);
                int screenHeight = displayMetrics.heightPixels / (cardsPerLevel[level] / layoutsPerLevel[level]);

                absolutePath = storedDrawables[chosenDrawables[i]].absolute_path_thumbnail;
                imageView1 = ((ImageView) cards[pair1].getChildAt(1));
                imageView2 = ((ImageView) cards[pair2].getChildAt(1));

                bitmapWorkerTask = new BitmapWorkerTask(screenWidth, screenHeight, imageView1, imageView2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    bitmapWorkerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, absolutePath);
                else
                    bitmapWorkerTask.execute();

            } else {

                (cards[pair1].getChildAt(1)).setBackgroundColor(ContextCompat.getColor(getActivity(), colors[chosenDrawables[i]]));
                (cards[pair2].getChildAt(1)).setBackgroundColor(ContextCompat.getColor(getActivity(), colors[chosenDrawables[i]]));
            }

            //Save the pairs.
            pairs[pair1] = pair2;
            pairs[pair2] = pair1;

        }

        return rootView;
    }

    /**
     * Called when a card is clicked, while the round hasn't ended. Contains the main game logic.
     *
     * @param cardID The index of the card that was clicked.
     */
    private void onCardFlipped(final int cardID) {
        if (!found[cardID]) {
            cards[cardID].flipCard();

            if (cards[cardID].getDisplayedChild() == 0) {
                //Log.v(LOG, "The card is already showing. Flip it back.");
                cardFlipped = false;

            } else if (!cardFlipped) {
                //Log.v(LOG, "There was no other card flipped, so flip this one and save its index.");
                card = cardID;
                cardFlipped = true;
            } else if (pairs[cardID] == card) {
                //Log.v(LOG, "A pair has been found!");
                cardFlipped = false;
                found[cardID] = true;
                found[pairs[cardID]] = true;
                numberOfCardsFound += 2;

                if (numberOfCardsFound == numberOfCards) {
                    ((MainActivityFragment.Callback) getActivity()).roundEnded();
                }

            } else {
                //Log.v(LOG, "This isn't a pair");
                //Wait a little so the player can see the card, before flipping both face down again.
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    public void onPostExecute(Void result) {
                        cards[card].flipCard();
                        cards[cardID].flipCard();
                    }
                }.execute();

                cardFlipped = false;
            }
        }
    }//onCardFlipped


    /**
     * Called when a card is clicked, after tha round has ended. Open the image on fullscreen, providing the respective description.
     *
     * @param cardID The index of the card that was clicked.
     */
    private void onCardClicked(final int cardID) {
        Intent intent = new Intent(getActivity(), ImageDetails.class);
        if (storedDrawables != null) {
            intent.putExtra("IMAGE_ID",
                    storedDrawables[cardDrawablePairs[cardID]].absolute_path_image);
            if (storedDrawables[cardDrawablePairs[cardID]].description == null) {
                //Log.v(LOG, "Description was null");
                intent.putExtra("IMAGE_DESC", "");
            } else {
                //Log.v(LOG, storedDrawables[cardDrawablePairs[cardID]].description);
                intent.putExtra("IMAGE_DESC", storedDrawables[cardDrawablePairs[cardID]].description);
            }
        } else {
            //Log.v(LOG, "Getting the associated color");
            intent.putExtra("COLOR_ID", colors[cardDrawablePairs[cardID]]);
        }

        startActivity(intent);
    }//onCardClicked

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * Returns a list of the File[s] located in the specified folder.
     *
     * @param context   The Context (getActivity())
     * @param albumName The name of the folder to search for
     * @param filters   The extensions of the files to search for. Will return everything if no filter is specified.
     * @return A list of the File[s] located in the specified folder with the specified extensions.
     */
    private File[] getFilesFromDirectory(Context context, String albumName, final String... filters) {
        // Get the directory for the app's private pictures directory.
        File directory = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!directory.mkdirs() && !directory.isDirectory()) {
            Log.e(LOG, "Directory not created");
        }


        @SuppressWarnings("UnnecessaryLocalVariable")
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {

                if (filters.length > 0) {
                    boolean flag = false;
                    int i = 0;
                    do {
                        if (s.contains(filters[i]))
                            flag = true;
                        i++;
                    } while (!flag && i < filters.length);

                    return flag;
                } else
                    return true;
                //If there are no filters, accept everything.

            }
        });
        return files;
    }

    /**
     * Takes the thumbnails, images, descriptions, links them together and stores them in the StoredImage storedDrawables variable.
     * For the thumbnails and images, only the absolute path is stored in order to save space. The actual images is extracted later and only the number that is required.
     * If there are no thumbnails, storedDrawables will be null.
     *
     * @param thumbnails   A File Array with thumbnails of the images.
     * @param images       A File Array with with the images for viewing in fullscreen.
     * @param descriptions A File Array with the descriptions for every image.
     */
    private void getDrawables(File[] thumbnails, File[] images, File[] descriptions) {

        //Create a HashMap with the name of the file and the absolute path of the thumbnail.
        HashMap<String, String> storedThumbnails = new HashMap<>();
        for (File thumbnail : thumbnails) {
            storedThumbnails.put(removeExtensionFromFilename(thumbnail.getName()), thumbnail.getAbsolutePath());
        }
        //Log.v(LOG, storedThumbnails.size() + " vs " + cardsPerLevel[level]);

        //If there aren't enough images to fill up all the available cards, return null.
        //This will results in the cards displaying the default colors.
        if (2 * storedThumbnails.size() < cardsPerLevel[level]) {
            storedDrawables = null;
            return;
        }

        //Create a HashMap with the name of the file and the absolute path of the image.
        HashMap<String, String> storedImages = new HashMap<>();
        for (File image : images) {
            storedImages.put(removeExtensionFromFilename(image.getName()), image.getAbsolutePath());
        }

        //Create a HashMap with the name of the file and the absolute path of the description.
        HashMap<String, String> storedDescriptions = new HashMap<>();
        StringBuilder desc;
        if (descriptions != null) {
            for (File description : descriptions) {
                desc = new StringBuilder();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(description));
                    String line;

                    while ((line = br.readLine()) != null) {
                        desc.append(line);
                        desc.append('\n');
                    }
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                storedDescriptions.put(removeExtensionFromFilename(description.getName()), desc.toString());
            }
        }

        storedDrawables = new StoredImage[storedThumbnails.size()];

        int i = 0;
        String path1, path2, description;
        //For every different thumbnail, search for the respective image and description and store it in a StoredImage.
        //If there is no image, the thumbnail will be used instead.
        //If there is no description, the description will be empty.
        for (String name : storedThumbnails.keySet()) {

            path1 = storedThumbnails.get(name);

            if (storedImages.containsKey(name))
                path2 = storedImages.get(name);
            else
                path2 = storedThumbnails.get(name);

            if (storedDescriptions.containsKey(name))
                description = storedDescriptions.get(name);
            else
                description = null;

            storedDrawables[i] = new StoredImage(name, path1, path2, description);
            i++;
        }

    }

    /**
     * Removes the extension from the name of the file (The last 4 characters).
     *
     * @param filename The full name of the file
     * @return The name of the file without the extension (the last 4 characters).
     */
    private String removeExtensionFromFilename(String filename) {
        return filename.substring(0, filename.length() - 4);
    }

    /**
     * Thumbnail: A small version of an image, used for the game.
     * Image: The actual image, used for fullscreen viewing.
     * Description: A description of the image, visible when viewing the image in fullscreen.
     * <p/>
     * The thumbnail and image actually contain the absolute path to the file and are later extracted.
     *
     * @author Ioannis Pierros (ioanpier@gmail.com)
     */
    public class StoredImage {

        public final String name;
        public final String absolute_path_thumbnail;
        public final String absolute_path_image;
        public final String description;

        public StoredImage(String name, String absolute_path_thumbnail, String absolute_path_image, String description) {
            this.name = name;
            this.absolute_path_thumbnail = absolute_path_thumbnail;
            this.absolute_path_image = absolute_path_image;
            this.description = description;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object img) {
            return img instanceof StoredImage && name.equals(((StoredImage) img).name);

        }
    }

    /**
     * Displays a ProgressDialog while the images are still being loaded.
     */
    private void instantiateLoadingBar() {

        AsyncTask<Void, Integer, Void> progressDialogAsync = new AsyncTask<Void, Integer, Void>() {
            private ProgressDialog progDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progDialog = new ProgressDialog(getActivity());
                progDialog.setMessage(getString(R.string.loading_bar_message));
                progDialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                boolean loading = true;
                while (loading) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                    }

                    synchronized (numberOfBitmapWorkerTasksLock) {
                        if (numberOfBitmapWorkerTasks == 0) {
                            loading = false;
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progDialog.dismiss();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            progressDialogAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            progressDialogAsync.execute();
    }

    private static class BitmapWorkerTask extends AsyncTask<String, Bitmap, Bitmap> {
        private final ArrayList<WeakReference<ImageView>> imageViewReferences;
        private String absoluteBitmapPath = "";
        private final int outWidth, outHeight;

        public BitmapWorkerTask(int reqWidth, int reqHeight, ImageView... imageViews) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReferences = new ArrayList<>();
            for (ImageView imageView : imageViews)
                imageViewReferences.add(new WeakReference<>(imageView));
            outWidth = reqWidth;
            outHeight = reqHeight;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            absoluteBitmapPath = params[0];
            return Utility.decodeSampledBitmapFromPath(absoluteBitmapPath, outWidth, outHeight);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            for (WeakReference<ImageView> imageViewReference : imageViewReferences)
                if (imageViewReference != null && bitmap != null) {
                    final ImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            synchronized (numberOfBitmapWorkerTasksLock) {
                numberOfBitmapWorkerTasks--;
            }

        }
    }


}
