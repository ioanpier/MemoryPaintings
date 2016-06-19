package gr.ioanpier.auth.users.memorypaintings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * A class that extends ViewFlipper to include a method for flipping with a standard animation.
 * @author Ioannis Pierros (ioanpier@gmail.com)
 */
public class ImageViewCard extends ViewFlipper {

    private final Context context;

    public ImageViewCard(Context context) {
        super(context);
        this.context = context;
    }

    public ImageViewCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    public void flipCard(){
        if (getDisplayedChild() == 0){
            setInAnimation(context, R.anim.in_from_left);
            setOutAnimation(context, R.anim.out_to_right);
            showNext();
        }else{
           setInAnimation(context, R.anim.in_from_right);
           setOutAnimation(context, R.anim.out_to_left);
           showPrevious();
        }
    }

}
