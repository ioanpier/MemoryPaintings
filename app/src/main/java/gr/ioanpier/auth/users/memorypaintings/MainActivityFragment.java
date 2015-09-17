package gr.ioanpier.auth.users.memorypaintings;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


/**
 * A placeholder fragment containing the screen and the logic of the game.
 * @author Ioannis Pierros (ioanpier@gmail.com)
 */
public class MainActivityFragment extends Fragment {

    private final static String LOG = "MainScreen";
    private final static String LEVEL_TAG = "LEVEL_TAG";

    //These variables are used for the dynamic creation of the level. They are chosen so their number is odd and the screen is filled evenly.
    private int layoutsPerLevel[] = {2, 2,  3, 4,4,4};
    private int cardsPerLevel[] = {4, 6,  12, 16,20,24};
    private int level;


    /**
     * Holds a key information for the state of the game. Refer to onCardFlipped for its usage.
     */
    private boolean cardFlipped = false;

    /**
     * The index of the card currently showing (used in conjuction with cardFlipped variable)
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
     * The Drawable[s] used for the front of ImageViewCard during the round.
     */
    private final int[] drawables =   {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f,
            R.drawable.red_big, R.drawable.blue_big, R.drawable.green_big, R.drawable.yellow_big, R.drawable.purple_big,
            R.drawable.orange_big, R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4, R.drawable.a5, R.drawable.a6,
            R.drawable.a7, R.drawable.a8, R.drawable.sidejob, R.drawable.monk, R.drawable.sunset, R.drawable.timetogo};

    /**
     * The Drawable[s] used for viewing the image fullscreen.
     */
    private final int[] drawablesBig ={R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f,
            R.drawable.red_big, R.drawable.blue_big, R.drawable.green_big, R.drawable.yellow_big, R.drawable.purple_big,
            R.drawable.orange_big, R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4, R.drawable.a5, R.drawable.a6,
            R.drawable.a7, R.drawable.a8, R.drawable.sidejob, R.drawable.monk, R.drawable.sunset, R.drawable.timetogo};

    /**
     * The String[s] for the description of the image.
     */
    private final String[] drawablesDesc = new String[drawables.length];

    /**
     * Holds the ViewFlippers (Actually their extension, ImageViewCard).
     */
    private ImageViewCard[] cards;

    /**
     * Callback interface.
     */
    public interface Callback{
        /**
         * Fired when the round has ended.
         */
        void roundEnded();
    }

    /**
     * Default constructor
     */
    public MainActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main2, container, false);

        //Calculate the level and the number of cards.
        level = getActivity().getIntent().getIntExtra(LEVEL_TAG, 0);
        if (level >= layoutsPerLevel.length)
            level = layoutsPerLevel.length - 1;
        numberOfCards = cardsPerLevel[level];

        pairs = new int[numberOfCards];
        for (int i = 0; i < numberOfCards; i++) {
            pairs[i] = -1;
        }
        cards = new ImageViewCard[numberOfCards];

        //This is where the layout magic happens.
        LinearLayout linearLayout;
        for (int i = 0; i < layoutsPerLevel[level]; i++) {
            //The layout consists of multiple vertical LinearLayout[s] positioned horizontally next to each other.
            linearLayout = new LinearLayout(getActivity());
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            //linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

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
                linearLayout.addView(card);

                //Set the back of the image.
                ((ImageView) card.getChildAt(0)).setImageDrawable(getResources().getDrawable(R.drawable.black));

                //Save the ImageViewCard for later use.
                cards[i * numberOfCards / layoutsPerLevel[level] + j] = card;

            }
            //Add the LinearLayout to the rootView.
            ((LinearLayout) rootView.findViewById(R.id.parent)).addView(linearLayout);
        }

        //Assign a listener for every ImageViewCard.
        for (int i = 0; i < numberOfCards; i++) {
            final int temp = i;
            cards[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (numberOfCardsFound < numberOfCards)
                        onCardFlipped(temp);
                    else
                        onCardClicked(temp);
                }
            });
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
        for (int i = 0; i < numberOfCards / 2; i++) {

            //Choose at random one of the available images. Make sure it's unique.
            boolean unique = false;
            while (!unique) {
                unique = true;
                //If there are a lot of images, this should be changed (there will never be a lot)
                chosenDrawables[i] = r.nextInt(drawables.length);
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
            ((ImageView) cards[pair1].getChildAt(1)).setImageDrawable(getResources().getDrawable(drawables[chosenDrawables[i]]));
            ((ImageView) cards[pair2].getChildAt(1)).setImageDrawable(getResources().getDrawable(drawables[chosenDrawables[i]]));

            //Save the pairs.
            pairs[pair1] = pair2;
            pairs[pair2] = pair1;

        }

        return rootView;
    }

    /**
     * Called when a card is clicked, while the round hasn't ended. Contains the main game logic.
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
                    ((Callback)getActivity()).roundEnded();
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
     * @param cardID The index of the card that was clicked.
     */
    private void onCardClicked(final int cardID) {
        Intent intent = new Intent(getActivity(), ImageDetails.class);
        intent.putExtra("IMAGE_ID", drawablesBig[cardDrawablePairs[cardID]]);
        if (drawablesDesc[cardDrawablePairs[cardID]]==null)
            intent.putExtra("IMAGE_DESC", getString(R.string.loremipsum));
        else
            intent.putExtra("IMAGE_DESC", drawablesDesc[cardDrawablePairs[cardID]]);
        startActivity(intent);
    }//onCardClicked

    /**
     * Getter method for the level variable
     * @return the current level
     */
    public int getLevel(){return level;}

    /**
     * Getter method for the LEVEL_TAGE variable
     * @return the LEVEL_TAG String
     */
    public String getLevelTag(){return LEVEL_TAG;}

}
