package sample;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import cn.ifavor.utils.bitmap.ImageLoader;
import cn.ifavor.utils.view.OnClick;
import cn.ifavor.utils.view.ViewInject;
import cn.ifavor.utils.view.ViewUtils;
import cn.ifavor.viewutils.R;

public class BitmapUtilsActivity extends Activity{
	@ViewInject(R.id.image)
	ImageView mImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bitmap);
		ViewUtils.bind(this);
	}
	
	@OnClick(R.id.btn_load)
	public void load(View view){
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "123lalala.jpg";
		ImageLoader.display(path, mImageView);
	}
}
