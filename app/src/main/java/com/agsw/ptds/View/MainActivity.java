package com.agsw.ptds.View;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.agsw.ptds.Controller.MainController;
import com.agsw.ptds.Controller.MainInterface;
import com.agsw.ptds.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // view objects
    ImageView logoImg, statusImg;
    TextView statusTxt;
    Button actionButton;
    MainInterface controller;

    // notification related objects
    NotificationCompat.Builder notifyBuilder;
    NotificationManager notifyManager;
    int notifyID = 001;
    long[] vibPattern = {50,1000,100,1000,50,1000,100,1000,
                         50,1000,100,1000,50,1000,100,1000,
                         50,1000,100,1000,50,1000,100,1000,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("PackageGuard");

        // init
        statusImg = (ImageView) findViewById(R.id.statusImg);
        logoImg = (ImageView) findViewById(R.id.imageView);
        statusTxt = (TextView) findViewById(R.id.textView);
        actionButton = (Button) findViewById(R.id.button);
        actionButton.setOnClickListener(this);

        notifyBuilder =
                new NotificationCompat.Builder(this);
        notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // launch controller
        controller = new MainController(this);
    }

    public void setLogoImg(Bitmap img) {
        logoImg.setImageBitmap(img);
    }

    public void setStatusText(String value)
    {
        statusTxt.setText(value);
    }

    public void updateStatusIcon(int resID)
    {
        if (statusImg != null)
        statusImg.setImageResource(resID);
    }

    public void setStatusColor(int c)
    {
        statusTxt.setTextColor(c);
    }

    public void setActionText(String val)
    {
        actionButton.setText(val);
    }

    public void showNotification(String title, String text, int drawable, boolean alarmSound)
    {
        // build the notificaiton
        notifyBuilder.setSmallIcon(drawable)
                .setContentTitle(title)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setCategory(Notification.CATEGORY_STATUS)

                .setContentText(text);

        if (alarmSound) {
            MediaPlayer mp= MediaPlayer.create(this, R.raw.alarm);
            mp.start();
            notifyBuilder.setVibrate(vibPattern).setPriority(Notification.PRIORITY_MAX);;
        }

        // issue it
        notifyManager.notify(notifyID++, notifyBuilder.build());
    }

    /**
     * Show the loading animation where the logo wobbles and mark springs out the screen
     */
    public void loadingAnimation()
    {
        // take out status img and wait till its over
        Animation markAnim = AnimationUtils.loadAnimation(this, R.anim.spin_down_out);
        markAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                statusImg.setVisibility(View.INVISIBLE);
                wobbleLogo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        statusImg.startAnimation(markAnim);
    }

    /**
     * Helper Logo Wobbling Method
     */
    private void wobbleLogo()
    {
        //start wobbling
        Animation logoAnim = AnimationUtils.loadAnimation(this, R.anim.zoom_inout);
        logoImg.startAnimation(logoAnim);
    }

    /**
     * Stop the loading animation where the logo is wobbling and mark shows up
     */
    public void stopLoadingAnimation()
    {
        Animation markAnim = AnimationUtils.loadAnimation(this, R.anim.spin_up_in);
        markAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                statusImg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                logoImg.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        statusImg.startAnimation(markAnim);
    }

    @Override
    public void onClick(View view) {
        controller.userRequestedAction();
    }

    public void enableAction() {
        actionButton.setEnabled(true);
    }
    public void disableAction()
    {
        actionButton.setEnabled(false);
    }

    public void makeToast(final String message) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message,
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
