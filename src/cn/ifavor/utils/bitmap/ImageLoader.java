package cn.ifavor.utils.bitmap;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

@SuppressLint("HandlerLeak")
@SuppressWarnings("unused")
public class ImageLoader {
	private static volatile ImageLoader mInstance; 
	
	/* 信号量，用于操作并发 */
	private Semaphore mSemaphonePoolThreadHander = new Semaphore(0);
	
	/* 图片内存管理 */
	private LruCache<String, Bitmap> mLruCache;
	
	/* 线程池 */
	private ExecutorService mThreadPool;

	/* 线程池中默认的线程数量 */
	private static final int DEFAULT_THREAD_COUNT = 1;

	protected static final int LOAD_IMAGE = 0;
	
	/* 后台轮询线程 */
	private Thread mPoolThread;
	
	/* 关联后台轮询线程的handler */
	private Handler mPoolThreadHandler;
	
	private LinkedList<Runnable> mTaskQueue;
	
	/* 主线程handler */
	private Handler mUiHandler;
	
	private ImageLoader(){
		// 初始化线程池
		mThreadPool = Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);
		
		mTaskQueue = new LinkedList<Runnable>();
		
		// 初始化后台轮询线程
		mPoolThread = new Thread(){
			@Override
			public void run() {
				Looper.prepare();
				mPoolThreadHandler = new Handler(){
					
					@Override
					public void handleMessage(Message msg) {
						// 在此处理，将任务从队列中取出，交给线程池执行
						// TODO: 默认FILO
						Runnable runnable = mTaskQueue.removeLast();
						if (runnable != null){
							mThreadPool.execute(runnable);
						}
						
					}
				};
				
				mSemaphonePoolThreadHander.release();
				Looper.loop();
			}
		};
		mPoolThread.start();
		
		// 初始化 LruCache
		int maxSize = (int) (Runtime.getRuntime().maxMemory() / 8);
		mLruCache = new LruCache<String, Bitmap>(maxSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}
	
	public static ImageLoader getSingle(){
		if (mInstance == null){
			synchronized (ImageLoader.class) {
				if (mInstance == null){
					mInstance = new ImageLoader();
				}
				
			}
		}
		return mInstance;
	}
	
	public void loadImage(final String path, final ImageView imageView){

		// 设置tag，防止显示错乱
		imageView.setTag(path);
		
		if (mUiHandler == null){
			mUiHandler = new Handler(new Callback() {
				
				@Override
				public boolean handleMessage(Message msg) {
					// 在UI线程中加载图片
					if (msg.what == LOAD_IMAGE){
						ImageHolder holder = (ImageHolder) msg.obj;
						
						String myUrl = holder.getPath();
						ImageView myImageView = holder.getImageView();
						Bitmap myBitmap = holder.getBitmap();

						if ( myImageView.getTag() != null && myImageView.getTag().equals(myUrl) ){
							// 加载图片
							myImageView.setImageBitmap(myBitmap);
						}
					}
					
					return true;
				}
			});
		}

		// 先去 LruCache 查询是否存在图片
		Bitmap bitmap = getBitmapFromLruCache(path);
		
		if (bitmap != null){
			sendToUIHandler(path, imageView, bitmap);
			return;
		} 
		
		// 从网络或者磁盘加载图片
		// 第一步：将任务添加任务队列
		mTaskQueue.add(new Runnable() {
			
			@Override
			public void run() {
				// 1. 获取ImageView的宽高、即图片压缩的宽高
				ImageSize size = ImageHelper.getImageSize(imageView);
				
				// 2. 压缩图片
				Bitmap bitmap = decodeSampledBitmapFromDisk(size, path);
				
				// 3. 显示图片
				sendToUIHandler(path, imageView, bitmap);
				
				// 4. 加入内存缓存
				addBitmapFromLruCache(path, bitmap);
				
				return;
			}
		});
		// 第二步：发送通知给轮询线程，让它把任务交给线程池执行
		try {
			sendToExecutors();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void display(final String path, final ImageView imageView){
		getSingle().loadImage(path, imageView);
	}
	
	private synchronized void sendToExecutors() throws Exception {
		if (mPoolThreadHandler == null){
			mSemaphonePoolThreadHander.acquire();
		}
		
		mPoolThreadHandler.sendEmptyMessage(0);
	}
	
	private void sendToUIHandler(final String path, final ImageView imageView,
			Bitmap bitmap) {
		Message message = Message.obtain();
		message.what = LOAD_IMAGE;
		ImageHolder holder = new ImageHolder(path, imageView, bitmap);
		message.obj = holder;
		mUiHandler.sendMessage(message);
	}

	protected Bitmap decodeSampledBitmapFromDisk(ImageSize size, String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 设置为true，加载图片但不放入内存（只获取尺寸到options）
		options.inJustDecodeBounds = true;
		// 将图片不加载到内存，直接获得参数到options
		BitmapFactory.decodeFile(path, options); 
		options.inSampleSize = ImageHelper.computeSampleSize(options, size.getWidth(), size.getHeight());
		// 重新设置false，解析时将图片放到内存
		options.inJustDecodeBounds = false;

		Bitmap decodeFile = BitmapFactory.decodeFile(path, options);
		return decodeFile;
	}

	private Bitmap getBitmapFromLruCache(String key) {
		return mLruCache.get(key);
	}
	
	private void addBitmapFromLruCache(String key, Bitmap bitmap){
		if (mLruCache.get(key) != null && bitmap != null){
			mLruCache.put(key, bitmap);
		}
	}
	
}
