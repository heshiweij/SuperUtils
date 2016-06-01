package cn.ifavor.utils.project;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import cn.ifavor.utils.view.OnClick;
import cn.ifavor.utils.view.ViewInject;
import cn.ifavor.utils.view.ViewUtils;
import cn.ifavor.viewutils.R;

public class ViewUtilsActivity extends Activity {

	@ViewInject(R.id.tv1)
	private TextView textview1;
	@ViewInject(R.id.tv2)
	private TextView textview2;
	@ViewInject(R.id.tv3)
	private TextView textview3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		ViewUtils.bind(this);
		
	}

	@OnClick({R.id.btn1, R.id.btn2})
	private void buttonClick(View view) {
		String text1 = textview1.getText().toString();
		String text2 = textview2.getText().toString();
		String text3 = textview3.getText().toString();

		Toast.makeText(this,
				String.format("%s -- %s -- %s", text1, text2, text3),
				Toast.LENGTH_SHORT).show();
		
		System.out.println(view);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ViewUtils.unbind(this);
	}
}
