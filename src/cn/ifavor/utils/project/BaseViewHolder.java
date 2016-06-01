package cn.ifavor.utils.project;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import cn.ifavor.utils.view.OnClick;
import cn.ifavor.utils.view.ViewInject;
import cn.ifavor.utils.view.ViewUtils;
import cn.ifavor.viewutils.R;


public  class BaseViewHolder {
	private View contentView;
	
	@ViewInject(R.id.tv1)
	private TextView textview1;
	@ViewInject(R.id.tv2)
	private TextView textview2;
	@ViewInject(R.id.tv3)
	private TextView textview3;

	private Context context;

	public BaseViewHolder(Context context) {
		this.context = context;
		contentView = View.inflate(context, R.layout.activity_view, null);
		ViewUtils.bind(this, contentView);
		
	}
	
	public void setData(){
		textview1.setText("newData01");
		textview2.setText("newData02");
		textview3.setText("newData03");
	};
	
	@OnClick({R.id.btn1, R.id.btn2})
	private void buttonClick(View view) {
		String text1 = textview1.getText().toString();
		String text2 = textview2.getText().toString();
		String text3 = textview3.getText().toString();

		Toast.makeText(context,
				String.format("%s -- %s -- %s", text1, text2, text3),
				Toast.LENGTH_SHORT).show();
		
		System.out.println(view);
	}


	public View getContentView() {
		return contentView;
	}
	
}
