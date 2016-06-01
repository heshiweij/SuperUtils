package cn.ifavor.utils.project;

import android.app.Activity;
import android.os.Bundle;

public class ViewUtilsHolderActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		BaseViewHolder holder = new BaseViewHolder(this);
		setContentView(holder.getContentView());
		holder.setData();
		
	}
}
