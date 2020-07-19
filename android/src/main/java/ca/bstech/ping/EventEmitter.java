package ca.bstech.ping;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class EventEmitter implements Consumer<WritableMap> {
    private ReactApplicationContext reactContext;

    public EventEmitter(ReactApplicationContext reactContext) {
        this.reactContext = reactContext;
    }

    private void sendEvent(String eventName, Object params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @Override
    public void accept(WritableMap result) {
        sendEvent(Constants.PING_EVENT, result);
    }

    public void emitExceptionMessage(String eventName, Throwable e) {
        WritableMap map = Arguments.createMap();
        map.putString("type", eventName);
        map.putString("raw", e.getMessage());
        sendEvent(eventName, map);
    }

    public void emitStartMessage() {
        WritableMap map = Arguments.createMap();
        map.putString("type", Constants.PING_START);
        accept(map);
    }

    public void emitStopMessage() {
        WritableMap map = Arguments.createMap();
        map.putString("type", Constants.PING_END);
        accept(map);
    }
}
