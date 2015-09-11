package gr.ioanpier.auth.users.memorypaintings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private boolean cardFlipped;
    private int card;
    private int[] pairs;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        pairs = new int[6];

        LinearLayout layout1 = (LinearLayout) rootView.findViewById(R.id.layout1);

        ImageView card1 = (ImageView) layout1.findViewById(R.id.card1);
        ImageView card2 = (ImageView) layout1.findViewById(R.id.card2);
        ImageView card3 = (ImageView) layout1.findViewById(R.id.card3);

        LinearLayout layout2 = (LinearLayout) rootView.findViewById(R.id.layout2);

        ImageView card4 = (ImageView) layout1.findViewById(R.id.card4);
        ImageView card5 = (ImageView) layout1.findViewById(R.id.card5);
        ImageView card6 = (ImageView) layout1.findViewById(R.id.card6);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardClicked(1);
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardClicked(2);
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardClicked(3);
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardClicked(4);
            }
        });

        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardClicked(5);
            }
        });

        card6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardClicked(6);
            }
        });





        return rootView;
    }

    private void cardClicked(int cardID){
        flipCard(cardID);
        if (!cardFlipped){
            card = cardID;
        }else if (pairs[cardID]==card){
            //You found a pair!
            //idea: show the image in fullscreen with few details
            //check if the game has ended.
        }else{
            //This isn't a pair
            //flip both face down

        }
    }

    private void flipCard(int cardID){

    }


}
