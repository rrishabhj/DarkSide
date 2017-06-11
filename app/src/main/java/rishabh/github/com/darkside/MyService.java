package rishabh.github.com.darkside;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by Jindal on 7/2/2016.
 */
public class MyService extends Service {
    NotificationManager nm;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    int progress=0;
    LinearLayout oView;


    public class LocalBinder extends Binder {
        MyService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MyService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        oView = new LinearLayout(this);
        oView.setBackgroundColor(Color.argb(progress,255,0,0)); // The translucent red color0x88ff0000

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        wm.addView(oView, params);


        Log.i("tag","onstartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("tag","onBind");

        return mBinder;
    }

    /** method for clients */
    public void setColor(int prog) {
        progress=prog;
        oView.setBackgroundColor(Color.argb(progress,255,0,0)); // The translucent red color0x88ff0000

    }


    @Override
    public void onCreate() {
        super.onCreate();
        Intent i=new Intent(this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(MyService.this,0,i,0);

        Notification notification=new Notification.Builder(MyService.this)
                .setTicker("tickertacker")
                .setContentTitle("Night MOde")
                .setContentText("Click to change brightness")
                .setPriority(Notification.PRIORITY_LOW)
                .setSmallIcon(R.drawable.abc1)
                .setContentIntent(pendingIntent).getNotification();
        notification.flags=Notification.FLAG_NO_CLEAR;
        nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        nm.notify(0,notification);



        Log.i("tag","oncreate");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("tag","onDestroy");
        if(oView!=null){
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.removeView(oView);
            nm.cancel(0);

        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("tag","onunbind");

        return false;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i("tag","onReBind");

        super.onRebind(intent);
    }
}
