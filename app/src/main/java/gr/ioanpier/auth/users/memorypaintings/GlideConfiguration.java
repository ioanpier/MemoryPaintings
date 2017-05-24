package gr.ioanpier.auth.users.memorypaintings;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by Ioannis on 3/9/2017.
 */

public class GlideConfiguration implements GlideModule {

    //https://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en
    //appears to make images look better
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
    }
}
