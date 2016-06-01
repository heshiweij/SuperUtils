# SuperUtils

轻量级、简易型Android开发框架，含：IOC、DB 的原理

Simple Android development framework, including the principle of DB and IOC. 

# Sample

## Bind view for activty

### Activity

View


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
			// must after setContentView
			ViewUtils.bind(this);
			
		}
	
		@Override
		protected void onDestroy() {
			super.onDestroy();
			// release references
			ViewUtils.unbind(this);
		}
	}



onClick
	
single

	@OnClick(R.id.btn1)
	private void buttonClick(View view) {
		
	}
	
multiple
	
	@OnClick({R.id.btn1, R.id.btn2})
	private void buttonClick(View view) {
		
	}


### ViewHolder

	public  class BaseViewHolder {
		private View contentView;
		
		@ViewInject(R.id.tv1)
		private TextView textview1;
		@ViewInject(R.id.tv2)
		private TextView textview2;
		@ViewInject(R.id.tv3)
		private TextView textview3;
	
		public BaseViewHolder(Context context) {
			contentView = View.inflate(context, R.layout.activity_view, null);
			// bind object with view
			ViewUtils.bind(this, contentView);
			
		}
	
	
		public View getContentView() {
			return contentView;
		}
		
	}


## DB

Custom your Javabean first

	@Table("student")
	public class Filter {
		@Column("id")
		private int id;
		
		@Column("name")
		private String name;
		
		@Column("addr")
		private String addr;
		
		@Column("age")
		private int age;
		
		@Column("email")
		private String email;
	
		public int getId() {
			return id;
		}
	
		// setter and getter
		
	}

and then, create sql by DBUtils

	Filter filter = new Filter();
	filter.setAddr("ZheJiang");
	filter.setAge(10);
	filter.setEmail("137233130@qq.com, heshiweij@163.com");
	
	String query = DBUtils.query(filter);
	System.out.println(query);

you can get: 

	select * from student where 1=1  and addr='ZheJiang' and email in ( '137233130@qq.com',' heshiweij@163.com' )  and age=10