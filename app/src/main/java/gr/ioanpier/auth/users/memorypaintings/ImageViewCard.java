package gr.ioanpier.auth.users.memorypaintings;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Ioannis on 14/9/2015.
 */
public class ImageViewCard extends ImageView {

    private Drawable front;
    private Drawable back;
    private boolean frontIsShowing = true;

    public ImageViewCard(Context context) {
        super(context);
    }

    public ImageViewCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageDrawable(Drawable drawable){
        front = drawable;
        super.setImageDrawable(front);
    }

    public void setImageDrawableFront(Drawable drawable){
        front = drawable;
        frontIsShowing=true;
        super.setImageDrawable(front);
    }

    public void setImageDrawableBack(Drawable drawable){
        back = drawable;
    }

    public void flipCard(){
        if (frontIsShowing){
            super.setImageDrawable(back);
            frontIsShowing=false;
        }else{
            super.setImageDrawable(front);
            frontIsShowing=true;
        }
    }


}
