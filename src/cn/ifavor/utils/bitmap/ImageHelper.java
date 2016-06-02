package cn.ifavor.utils.bitmap;

import java.lang.reflect.Field;

import android.graphics.BitmapFactory;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class ImageHelper {
	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
	    int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
	    int roundedSize;
	    if (initialSize <= 8) {
	        roundedSize = 1;
	        while (roundedSize < initialSize) {
	            roundedSize <<= 1;
	        }
	    } else {
	        roundedSize = (initialSize + 7) / 8 * 8;
	    }
	    return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
	    double w = options.outWidth;
	    double h = options.outHeight;
	    int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
	    int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
	    if (upperBound < lowerBound) {
	        // return the larger one when there is no overlapping zone.
	        return lowerBound;
	    }
	    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
	        return 1;
	    } else if (minSideLength == -1) {
	        return lowerBound;
	    } else {
	        return upperBound;
	    }
	}
	
	public static ImageSize getImageSize(ImageView imageView) {
		// 屏幕宽高
		int screenWidth = imageView.getContext().getResources().getDisplayMetrics().widthPixels;
		int screenHeight = imageView.getContext().getResources().getDisplayMetrics().heightPixels;
		
		int width = imageView.getWidth();
		if (width <= 0){
			LayoutParams lp = imageView.getLayoutParams();
			width = lp.width;
		}
		if (width <= 0){
			// 由于 getMaxWidth 只能在api16支持，所以需要通过反射去兼容 mMaxWidth
			width = ImageHelper.getReflectVal(imageView, "mMaxWidth");
		}
		if (width <= 0){
			width = screenWidth;
		}
		
		int height = imageView.getHeight();
		if (height <= 0){
			LayoutParams lp = imageView.getLayoutParams();
			height = lp.height;
		}
		if (height <= 0){
			// 由于 getMaxHeight 只能在api16支持，所以需要通过反射去兼容 mMaxHeight
			height = ImageHelper.getReflectVal(imageView, "mMaxHeight");
		}
		if (height <= 0){
			height = screenHeight;
		}
		
		return new ImageSize(width, height);
	}

	private static int getReflectVal(ImageView imageView, String fieldName) {
		int val = 0;
		Class<ImageView> clazz = ImageView.class;
		try {
			Field field = clazz.getDeclaredField("fieldName");
			field.setAccessible(true);
			
			val = field.getInt(imageView);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return val;
	}
}
