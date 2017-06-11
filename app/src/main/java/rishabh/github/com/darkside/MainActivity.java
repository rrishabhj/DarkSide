package rishabh.github.com.darkside;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class MainActivity extends AppCompatActivity {

    int prog = 0;

    MyService mService;
    TextView textView;
    SeekBar seekBar;
    Intent intent;
    Button btnColor;
    Boolean status = false, mBound = false;
    private static final int CUSTOM_OVERLAY_PERMISSION_REQUEST_CODE = 101;
    private boolean isShowOverlayPermission = false;

    ColorPickerView colorPickerView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getOverlayPermission();
        colorPickerView = (ColorPickerView) findViewById(R.id.color_picker_view);
        btnColor = (Button) findViewById(R.id.btnColor);
        intent = new Intent(this, MyService.class);

        textView = (TextView) findViewById(R.id.textView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        prog = seekBar.getProgress();
        textView.setText("Brightness Level (" + prog + "%)");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                prog = seekBar.getProgress();
                textView.setText("Brightness Level (" + prog + "%)");

                if (status) {
                    mService.setColorProgress(prog);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void openColorPickerDialog(View view){

        ColorPickerDialogBuilder
            .with(MainActivity.this)
            .setTitle("Choose color")
            .initialColor(Color.LTGRAY)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setOnColorSelectedListener(new OnColorSelectedListener() {
                @Override
                public void onColorSelected(int selectedColor) {
                    Toast.makeText(MainActivity.this,"onColorSelected: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_LONG).show();
                }
            })
            .setPositiveButton("ok", new ColorPickerClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                    //changeBackgroundColor(selectedColor);
                    btnColor.setBackgroundColor(selectedColor);
                    if (status) {
                        mService.setColor(selectedColor);
                        btnColor.setBackgroundColor(selectedColor);
                    }
                }
            })
            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            })
            .build()
            .show();
    }

    private void getOverlayPermission() {

        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, CUSTOM_OVERLAY_PERMISSION_REQUEST_CODE);

    }

    public void buttonSwitch(View view) {
        Button buttonSwitch = (Button) view;
        if (buttonSwitch.getText().toString().equals("START")) {
            buttonSwitch.setText("STOP");

            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            startService(intent);
            status = true;
        } else if (buttonSwitch.getText().toString().equals("STOP")) {

            buttonSwitch.setText("START");

            unbindService(serviceConnection);
            stopService(intent);
            seekBar.setProgress(0);
            textView.setText("Brightness Level (0%)");
            status = false;
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MyService.LocalBinder binder = (MyService.LocalBinder) iBinder;
            mService = binder.getService();
            mBound = true;
        }

        @Override public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    @Override @TargetApi(Build.VERSION_CODES.M)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CUSTOM_OVERLAY_PERMISSION_REQUEST_CODE) {
            isShowOverlayPermission= false;
        }
    }
}