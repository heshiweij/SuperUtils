package sample;

import cn.ifavor.utils.view.OnClick;
import cn.ifavor.utils.view.ViewInject;
import cn.ifavor.utils.view.ViewUtils;
import cn.ifavor.viewutils.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MyFragment extends Fragment {
	@ViewInject(R.id.tv1)
	private TextView textview1;
	@ViewInject(R.id.tv2)
	private TextView textview2;
	@ViewInject(R.id.tv3)
	private TextView textview3;
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = View.inflate(getContext(), R.layout.fragment_view, null);
		ViewUtils.bind(this, view);
		
		return view;
	}
	
	@OnClick({R.id.btn1, R.id.btn2})
	public void startClick(){
		String text1 = textview1.getText().toString();
		String text2 = textview2.getText().toString();
		String text3 = textview3.getText().toString();

		Toast.makeText(getContext(),
				String.format("%s -- %s -- %s", text1, text2, text3),
				Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ViewUtils.unbind(this);
	}
}
