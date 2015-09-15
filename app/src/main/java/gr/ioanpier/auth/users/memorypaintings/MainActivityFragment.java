package gr.ioanpier.auth.users.memorypaintings;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final static String LOG = "MainScreen";

    private boolean cardFlipped = false;
    private int card;
    private int[] pairs;
    private boolean[] found;
    private final static int numberOfCards = 6;
    private int numberOfCardsFound = 0;

    private final int[] drawables = {R.drawable.red, R.drawable.blue, R.drawable.green, R.drawable.purple, R.drawable.yellow, R.drawable.orange};
    private int[] chosenDrawables;
    private ImageViewCard[] cards;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        pairs = new int[numberOfCards];
        for (int i = 0; i < numberOfCards; i++) {
            pairs[i] = -1;
        }

        LinearLayout layout1 = (LinearLayout) rootView.findViewById(R.id.layout1);


        cards = new ImageViewCard[numberOfCards];
        cards[0] = (ImageViewCard) layout1.findViewById(R.id.card1);
        cards[1] = (ImageViewCard) layout1.findViewById(R.id.card2);
        cards[2] = (ImageViewCard) layout1.findViewById(R.id.card3);

        LinearLayout layout2 = (LinearLayout) rootView.findViewById(R.id.layout2);

        cards[3] = (ImageViewCard) layout2.findViewById(R.id.card4);
        cards[4] = (ImageViewCard) layout2.findViewById(R.id.card5);
        cards[5] = (ImageViewCard) layout2.findViewById(R.id.card6);

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

            //cards[i].setBackgroundImage(getResources().getDrawable(R.drawable.black));
            //cards[i].setBackground(getResources().getDrawable(R.drawable.black));
           ((ImageView) cards[i].getChildAt(0)).setImageDrawable(getResources().getDrawable(R.drawable.black));
        }

        //((ImageView) rootView.findViewById(R.id.card4child0)).setImageDrawable(getResources().getDrawable(R.drawable.black));

        found = new boolean[numberOfCards];
        for (int i = 0; i < numberOfCards; i++) {
            found[i] = false;
        }

        Random r = new Random();

        chosenDrawables = new int[numberOfCards/2];


        //choose 3 cards

        ArrayList<Integer> availablePairs = new ArrayList<>();
        for (int i = 0; i < numberOfCards; i++) {
            availablePairs.add(i);
        }
        Collections.shuffle(availablePairs);


        int pair1, pair2;
        for (int i = 0; i < numberOfCards/2 ; i++) {
            boolean unique = false;
            while (!unique) {
                unique = true;
                chosenDrawables[i] = drawables[r.nextInt(drawables.length)];
                for (int j = 0; j < i; j++) {
                    if (chosenDrawables[i] == chosenDrawables[j])
                        unique = false;
                }
            }


            pair1 = availablePairs.remove(0);
            pair2 = availablePairs.remove(0);

            ((ImageView) cards[pair1].getChildAt(1)).setImageDrawable(getResources().getDrawable(chosenDrawables[i]));
            ((ImageView) cards[pair2].getChildAt(1)).setImageDrawable(getResources().getDrawable(chosenDrawables[i]));

            cards[pair1].flipCard();
            cards[pair2].flipCard();
            cards[pair1].flipCard();
            cards[pair2].flipCard();

            pairs[pair1] = pair2;
            pairs[pair2] = pair1;

        }

       // ((ImageView) rootView.findViewById(R.id.card4child1)).setImageDrawable(getResources().getDrawable(R.drawable.red));

        System.out.println("All Drawables");
        for (int drawable : drawables) {
            System.out.println(drawable);

        }

        System.out.println("Chosen Drawables");
        for (int i = 0; i < numberOfCards/2 ; i++) {
            System.out.println(chosenDrawables[i]);
        }

        System.out.println("Pairs");
        for (int i = 0; i < numberOfCards; i++) {
            System.out.println(pairs[i]);
        }

        System.out.println("Child count");
        for (int i = 0; i < numberOfCards; i++) {
            System.out.println(cards[i].getChildCount());
        }

        return rootView;
    }

    private void onCardFlipped(final int cardID) {
        Log.v(LOG, "ID of card clicked: " + cardID);
        if (found[cardID]) {
            Log.v(LOG, "card has already been found");
            //Show info about the painting
        } else if (cards[cardID].getDisplayedChild()==1) {
            Log.v(LOG, "The card is already showing");
            //Flip it back
            cardFlipped=false;
            cards[cardID].flipCard();
        } else if (!cardFlipped) {
            Log.v(LOG, "No other card was flipped");
            card = cardID;
            cardFlipped = true;
            cards[cardID].flipCard();
        } else if (pairs[cardID] == card) {
            Log.v(LOG, "pair found");
            cards[cardID].flipCard();
            cardFlipped = false;
            found[cardID]=true;
            found[pairs[cardID]]=true;
            numberOfCardsFound+=2;
            //You found a pair!
            //idea: show the image in fullscreen with few details
            //check if the game has ended.
        } else {
            Log.v(LOG, "wrong pair");
            //Flip the card so the player can see it
            cards[cardID].flipCard();


            //This isn't a pair
            //flip both face down
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
    }//onCardFlipped

    private void onCardClicked(final int cardID){
        Toast.makeText(getActivity(), "Game is over!", Toast.LENGTH_SHORT).show();
    }//onCardClicked

}
