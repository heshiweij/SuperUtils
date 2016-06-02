package sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import cn.ifavor.utils.db.DBUtils;
import cn.ifavor.utils.view.OnClick;
import cn.ifavor.utils.view.ViewInject;
import cn.ifavor.utils.view.ViewUtils;
import cn.ifavor.viewutils.R;

public class DBUtilsActivity extends Activity{
	
	@ViewInject(R.id.btn_query)
	private Button mBtnQuery;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_db);
		ViewUtils.bind(this);
		
		
	}
	
	@OnClick(R.id.btn_query)
	public void query(){
		Filter filter = new Filter();
		filter.setAddr("ZheJiang");
		filter.setAge(10);
		filter.setEmail("137233130@qq.com, heshiweij@163.com");
		
		String query = DBUtils.query(filter);
		System.out.println(query);
	}
	
	@Override
	protected void onDestroy() {
		ViewUtils.unbind(this);
	}
}
