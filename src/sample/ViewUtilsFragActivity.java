package sample;

import cn.ifavor.utils.view.ViewUtils;
import cn.ifavor.viewutils.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

public class ViewUtilsFragActivity extends FragmentActivity{
	
	FrameLayout mFl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		
		mFl = ViewUtils.findById(this, R.id.fl_container);
		
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.fl_container, new MyFragment());
		transaction.commit();
	}
}
