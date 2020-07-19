package ca.bstech.ping;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;

import java.util.LinkedHashMap;
import java.util.Map;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class BSTPingModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private HandlerThread handlerThread = new HandlerThread("BSTPingThread");
    private HandlerThread cleanupThread = new HandlerThread("BSTPingCleanupThread");

//    private final ReactApplicationContext reactContext;

    private final EventEmitter eventEmitter;

    public BSTPingModule(ReactApplicationContext reactContext) {
        super(reactContext);
//        this.reactContext = reactContext;
        reactContext.addLifecycleEventListener(this);
        handlerThread.start();
        cleanupThread.start();
        eventEmitter = new EventEmitter(reactContext);
//        reactContext.getPackageManager();
    }

    @Override
    public String getName() {
        return Constants.BSTPing;
    }

    @ReactMethod
    public void startAsync(final String domainName, ReadableMap params, ReadableMap pingOptions, final Promise promise) {
        try {
            if (domainName == null || domainName.length() == 0) {
                throw new IllegalArgumentException("domainName");
            }

            final Map<String, String> options = new LinkedHashMap<>();
            if (pingOptions != null) {
                ReadableMapKeySetIterator iter = pingOptions.keySetIterator();
                while (iter.hasNextKey()) {
                    String key = iter.nextKey();
                    String value = pingOptions.getString(key);
                    options.put(key, value);
                }
            }
            final Integer durationSeconds = params.hasKey("durationSeconds") ?
                    params.getInt("durationSeconds") : null;
            final int reportIntervalSeconds = params.hasKey("reportIntervalSeconds") ?
                    params.getInt("reportIntervalSeconds") : 30;
            Handler mHandler = new Handler(handlerThread.getLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        eventEmitter.emitStartMessage();
                        PingUtil.ping(domainName, options, durationSeconds,
                                reportIntervalSeconds, new PingResultParser(eventEmitter));
                        eventEmitter.emitStopMessage();
                    } catch (RuntimeException e) {
                        Log.e(Constants.BSTPing, Constants.E_RUNTIME_EXCEPTION, e);
                        eventEmitter.emitExceptionMessage(Constants.E_RUNTIME_EXCEPTION, e);
                    }
                }
            });
            promise.resolve(null);
        } catch (IllegalArgumentException e) {
            promise.reject(Constants.E_INVALID_PARAM, e);
        } catch (RuntimeException e) {
            promise.reject(Constants.E_RUNTIME_EXCEPTION, e);
        }
    }

    @Override
    public void onHostResume() {
        // Activity `onResume`
    }

    @Override
    public void onHostPause() {
        // Activity `onPause`
    }

    @ReactMethod
    public void stopAsync(final Promise promise) {
        try {
            Handler mHandler = new Handler(cleanupThread.getLooper());
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        PingUtil.stop();
                        if ( promise != null ) {
                            promise.resolve(null);
                        }
                    } catch(RuntimeException e) {
                        Log.e(Constants.BSTPing, "Stop ping process encounter exception", e);
                        if ( promise != null ) {
                            promise.reject(Constants.E_RUNTIME_EXCEPTION, e);
                        }
                    }
                }
            });
        } catch (RuntimeException e) {
            Log.e(Constants.BSTPing, "Stop ping encounter exception", e);
            if ( promise != null ) {
                promise.reject(Constants.E_RUNTIME_EXCEPTION, e);
            }
        }
    }

    @Override
    public void onHostDestroy() {
        stopAsync(null);
    }
}
