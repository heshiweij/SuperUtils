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
	
	/* �ź��������ڲ������� */
	private Semaphore mSemaphonePoolThreadHander = new Semaphore(0);
	
	/* ͼƬ�ڴ���� */
	private LruCache<String, Bitmap> mLruCache;
	
	/* �̳߳� */
	private ExecutorService mThreadPool;

	/* �̳߳���Ĭ�ϵ��߳����� */
	private static final int DEFAULT_THREAD_COUNT = 1;

	protected static final int LOAD_IMAGE = 0;
	
	/* ��̨��ѯ�߳� */
	private Thread mPoolThread;
	
	/* ������̨��ѯ�̵߳�handler */
	private Handler mPoolThreadHandler;
	
	private LinkedList<Runnable> mTaskQueue;
	
	/* ���߳�handler */
	private Handler mUiHandler;
	
	private ImageLoader(){
		// ��ʼ���̳߳�
		mThreadPool = Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);
		
		mTaskQueue = new LinkedList<Runnable>();
		
		// ��ʼ����̨��ѯ�߳�
		mPoolThread = new Thread(){
			@Override
			public void run() {
				Looper.prepare();
				mPoolThreadHandler = new Handler(){
					
					@Override
					public void handleMessage(Message msg) {
						// �ڴ˴���������Ӷ�����ȡ���������̳߳�ִ��
						// TODO: Ĭ��FILO
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
		
		// ��ʼ�� LruCache
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

		// ����tag����ֹ��ʾ����
		imageView.setTag(path);
		
		if (mUiHandler == null){
			mUiHandler = new Handler(new Callback() {
				
				@Override
				public boolean handleMessage(Message msg) {
					// ��UI�߳��м���ͼƬ
					if (msg.what == LOAD_IMAGE){
						ImageHolder holder = (ImageHolder) msg.obj;
						
						String myUrl = holder.getPath();
						ImageView myImageView = holder.getImageView();
						Bitmap myBitmap = holder.getBitmap();

						if ( myImageView.getTag() != null && myImageView.getTag().equals(myUrl) ){
							// ����ͼƬ
							myImageView.setImageBitmap(myBitmap);
						}
					}
					
					return true;
				}
			});
		}

		// ��ȥ LruCache ��ѯ�Ƿ����ͼƬ
		Bitmap bitmap = getBitmapFromLruCache(path);
		
		if (bitmap != null){
			sendToUIHandler(path, imageView, bitmap);
			return;
		} 
		
		// ��������ߴ��̼���ͼƬ
		// ��һ��������������������
		mTaskQueue.add(new Runnable() {
			
			@Override
			public void run() {
				// 1. ��ȡImageView�Ŀ�ߡ���ͼƬѹ���Ŀ��
				ImageSize size = ImageHelper.getImageSize(imageView);
				
				// 2. ѹ��ͼƬ
				Bitmap bitmap = decodeSampledBitmapFromDisk(size, path);
				
				// 3. ��ʾͼƬ
				sendToUIHandler(path, imageView, bitmap);
				
				// 4. �����ڴ滺��
				addBitmapFromLruCache(path, bitmap);
				
				return;
			}
		});
		// �ڶ���������֪ͨ����ѯ�̣߳����������񽻸��̳߳�ִ��
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
		// ����Ϊtrue������ͼƬ���������ڴ棨ֻ��ȡ�ߴ絽options��
		options.inJustDecodeBounds = true;
		// ��ͼƬ�����ص��ڴ棬ֱ�ӻ�ò�����options
		BitmapFactory.decodeFile(path, options); 
		options.inSampleSize = ImageHelper.computeSampleSize(options, size.getWidth(), size.getHeight());
		// ��������false������ʱ��ͼƬ�ŵ��ڴ�
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
