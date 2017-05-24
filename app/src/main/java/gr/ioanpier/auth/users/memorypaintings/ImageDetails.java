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
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Simple Activity that displays and image in fullscreen along with a description.
 * According to the layout, the image is resized to fit without changing the aspect ratio.
 *
 * @author Ioannis Pierros (ioanpier@gmail.com)
 */
public class ImageDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);
        String imagePath;
        imagePath = getIntent().getStringExtra("IMAGE_ID");
        ImageView imageView = ((ImageView)(findViewById(R.id.image)));



        if (imagePath==null){
            ((findViewById(R.id.image))).setBackgroundColor(ContextCompat.getColor(this, getIntent().getIntExtra("COLOR_ID", R.color.black)));
        }else if (!imagePath.isEmpty()){
            String drawableDesc = getIntent().getStringExtra("IMAGE_DESC");
            ((TextView)(findViewById(R.id.text))).setText(drawableDesc);

            imageView.setImageBitmap(null);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            //WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
            WindowManager wm = getWindowManager();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels / 2;
            int screenHeight = displayMetrics.heightPixels / 2;

            imageView.setImageBitmap(Utility.decodeSampledBitmapFromPath(imagePath, screenWidth, screenHeight));
        }else {
            int color = getIntent().getIntExtra("COLOR_ID", R.color.black);
            imageView.setBackgroundColor(ContextCompat.getColor(this, color));

        }

        @SuppressWarnings("UnusedAssignment")
        PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
    }

}
