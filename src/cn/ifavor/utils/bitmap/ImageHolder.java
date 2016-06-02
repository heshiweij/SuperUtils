package cn.ifavor.utils.bitmap;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageHolder {
	private String path;
	private ImageView imageView;
	private Bitmap bitmap;
	
	public ImageHolder(String path, ImageView imageView, Bitmap bitmap) {
		this.path = path;
		this.imageView = imageView;
		this.bitmap = bitmap;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public ImageView getImageView() {
		return imageView;
	}
	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
}
