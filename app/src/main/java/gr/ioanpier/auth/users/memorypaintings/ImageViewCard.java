package gr.ioanpier.auth.users.memorypaintings;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * Created by Ioannis on 14/9/2015.
 */
public class ImageViewCard extends ViewFlipper {

    private Drawable front;
    private final Context context;
    private Drawable back;
    private boolean frontIsShowing = true;

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

    public boolean isFrontShowing(){
        return frontIsShowing;
    }


}
