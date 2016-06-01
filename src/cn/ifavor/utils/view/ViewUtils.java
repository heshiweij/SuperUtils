package cn.ifavor.utils.view;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class ViewUtils {
	
	public static void bind(Activity activity) {
		try {
			bindActivity(activity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void bind(Object obj,  View v) {
		try {
			bindView(obj,v);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void unbind(Activity activity) {
		try {
			unbindActivity(activity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 绑定Activity
	 * @param activity
	 * @throws Exception
	 */
	private static void bindActivity( Activity activity) throws Exception {
		Class clazz = activity.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields){
			f.setAccessible(true);
			ViewInject viewInject = f.getAnnotation(ViewInject.class);
			if (viewInject != null){
				int resId = viewInject.value();
				View view = activity.findViewById(resId);
				f.set(activity, view);
			}
		}
		
		Method[] methods = clazz.getDeclaredMethods();
		for (Method m : methods){
			m.setAccessible(true);
			OnClick onClick = m.getAnnotation(OnClick.class);
			if (onClick != null){
				int[] resId = onClick.value();
				
				// 设置点击事件
				setOnClick(activity, resId, m);
			}
		}
	}
	
	/**
	 * 设置点击事件Activity
	 * @param activity
	 * @param resIds
	 * @param m
	 */
	private static void setOnClick(final Activity activity, int[] resIds, final Method m) {
		for (int i = 0; i < resIds.length; i++){
			View view = activity.findViewById(resIds[i]);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try {
						Class<?>[] parameterTypes = m.getParameterTypes();
						
						if (parameterTypes.length > 0 && parameterTypes[0] == View.class){
							m.invoke(activity, v);
						} else {
							m.invoke(activity);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		
	}

	/**
	 * 解绑Activity
	 * @param activity
	 * @throws Exception
	 */
	public static void unbindActivity(Activity activity) throws Exception {
		Class clazz = activity.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields){
			f.setAccessible(true);
			ViewInject viewInject = f.getAnnotation(ViewInject.class);
			if (viewInject != null){
				int resId = viewInject.value();
				View view = activity.findViewById(resId);
				f.set(activity, null);
			}
		}
	}
	
	/**
	 * 绑定 ViewHolder
	 * @param obj
	 * @param view
	 */
	private static void bindView(Object obj, View v) throws Exception{
		Class clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields){
			f.setAccessible(true);
			ViewInject viewInject = f.getAnnotation(ViewInject.class);
			if (viewInject != null){
				int resId = viewInject.value();
				View view = v.findViewById(resId);
				f.set(obj, view);
			}
		}
		
		Method[] methods = clazz.getDeclaredMethods();
		for (Method m : methods){
			m.setAccessible(true);
			OnClick onClick = m.getAnnotation(OnClick.class);
			if (onClick != null){
				int[] resId = onClick.value();
				
				// 设置点击事件
				setOnClick(obj, v, resId, m);
			}
		}
	}

	/**
	 * 设置点击事件Object
	 * @param obj 
	 * @param v
	 * @param resId
	 * @param m
	 */
	private static void setOnClick(final Object obj, View v, int[] resIds, final Method m) {
		for (int i = 0; i < resIds.length; i++){
			View view = v.findViewById(resIds[i]);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					try {
						Class<?>[] parameterTypes = m.getParameterTypes();
						
						if (parameterTypes.length > 0 && parameterTypes[0] == View.class){
							m.invoke(obj, v);
						} else {
							m.invoke(obj);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
}
