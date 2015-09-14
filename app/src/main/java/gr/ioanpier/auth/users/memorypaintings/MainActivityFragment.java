package gr.ioanpier.auth.users.memorypaintings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private boolean cardFlipped=false;
    private int card;
    private int[] pairs;

    private final int[] drawables = {R.drawable.red, R.drawable.blue,R.drawable.green,R.drawable.purple,R.drawable.yellow,R.drawable.orange};
    private int[] chosenDrawables;
    private ImageViewCard[] cards;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        pairs = new int[6];
        for (int i = 0; i < 6; i++) {
            pairs[i]=-1;
        }

        LinearLayout layout1 = (LinearLayout) rootView.findViewById(R.id.layout1);


        cards = new ImageViewCard[6];
        cards[0] = (ImageViewCard) layout1.findViewById(R.id.card1);
        cards[1] = (ImageViewCard) layout1.findViewById(R.id.card2);
        cards[2] = (ImageViewCard) layout1.findViewById(R.id.card3);

        LinearLayout layout2 = (LinearLayout) rootView.findViewById(R.id.layout2);

        cards[3] = (ImageViewCard) layout2.findViewById(R.id.card4);
        cards[4] = (ImageViewCard) layout2.findViewById(R.id.card5);
        cards[5] = (ImageViewCard) layout2.findViewById(R.id.card6);

        for (int i = 0; i < 6; i++) {
            final int temp = i;
            cards[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardClicked(temp);
                }
            });
        }

        Random r = new Random();

        chosenDrawables = new int[3];


        //choose 3 cards

        ArrayList<Integer> availablePairs= new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            availablePairs.add(i);
        }
        Collections.shuffle(availablePairs);



        int pair1,pair2;
        for (int i = 0; i < 3; i++) {
            boolean unique = false;
            while (!unique){
                unique=true;
                chosenDrawables[i]=drawables[r.nextInt(drawables.length)];
                for (int j = 0; j < i; j++) {
                    if (chosenDrawables[i]==chosenDrawables[j])
                        unique=false;
                }
            }


            pair1=availablePairs.remove(0);
            pair2=availablePairs.remove(0);

            cards[pair1].setImageDrawable(getResources().getDrawable(chosenDrawables[i]));
            cards[pair2].setImageDrawable(getResources().getDrawable(chosenDrawables[i]));

            pairs[pair1]=pair2;
            pairs[pair2]=pair1;

        }

        System.out.println("All Drawables");
        for (int i = 0; i <drawables.length ; i++) {
            System.out.println(drawables[i]);

        }

        System.out.println("Chosen Drawables");
        for (int i = 0; i < 3; i++) {
            System.out.println(chosenDrawables[i]);
        }

        System.out.println("Pairs");
        for (int i = 0; i < 6; i++) {
            System.out.println(pairs[i]);
        }


        return rootView;
    }

    private void cardClicked(int cardID){
        System.out.println("Card " + cardID + " was clicked!");
        cards[cardID].flipCard();
        if (!cardFlipped){
            card = cardID;
        }else if (pairs[cardID]==card){
            Toast.makeText(getActivity(), "Pair found!", Toast.LENGTH_LONG).show();
            cardFlipped=false;
            //You found a pair!
            //idea: show the image in fullscreen with few details
            //check if the game has ended.
        }else{
            Toast.makeText(getActivity(), "Wrong pair!", Toast.LENGTH_LONG).show();
            cardFlipped=false;
            //This isn't a pair
            //flip both face down

            cards[card].flipCard();
            cards[cardID].flipCard();

        }
    }


}
