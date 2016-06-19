package gr.ioanpier.auth.users.memorypaintings;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * An class with various utility methods. They were written for the specific application but are more general, so with no or minor adjustments
 * they can be used by other applications as well.
 */
class Utility {

    /**
     * Calculates a sample size value that is a power of two based on a target width and height of a Bitmap
     *
     * @param options   The Bitmap Options that hold the width and height of the Bitmap
     * @param reqWidth  The width of the ImageView that will hold the Bitmap
     * @param reqHeight The height of the ImageView that will hold the Bitmap
     * @return The sample size that should be used.
     */
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            inSampleSize++;
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Decodes a Bitmap after resampling it appropriately.
     *
     * @param absoluteBitmapPath The absolute path of the Bitmap in the storage.
     * @param reqWidth           The width of the ImageView that will hold the Bitmap
     * @param reqHeight          The height of the ImageView that will hold the Bitmap
     * @return The decoded Bitmap
     */
    public static Bitmap decodeSampledBitmapFromPath(String absoluteBitmapPath,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(absoluteBitmapPath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        //if (options.inSampleSize == 1 && forceResample)
        //    options.inSampleSize = 2;
        //options.inSampleSize++;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(absoluteBitmapPath, options);
    }


}
