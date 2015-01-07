package dominando.android.ex21_http;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton mInstance = null;
    // Fila de execução
    private RequestQueue mRequestQueue;
    // Image Loader
    private ImageLoader mImageLoader;

    private VolleySingleton(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(this.mRequestQueue, new BitmapCache(50));
    }

    public static VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return this.mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return this.mImageLoader;
    }

    public class BitmapCache extends LruCache implements ImageLoader.ImageCache {
        public BitmapCache(int maxSize) {
            super(maxSize);
        }
        @Override
        public Bitmap getBitmap(String url) {
            return (Bitmap)get(url);
        }
        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            put(url, bitmap);
        }
    }
}

