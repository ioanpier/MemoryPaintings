package gr.ioanpier.auth.users.memorypaintings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * An extension of ImageView that ensures the image will be square by overiding the onMeasure method.
 */
public class SquareImageView extends ImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);

    }



}
