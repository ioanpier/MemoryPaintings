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
