package com.example.sinoalice_timer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.CountDownTimer;
import android.widget.Toast;
import android.os.Vibrator;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;


@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {
    //int [] clock  = {07 ,  14, 16, 18, 20, 22}; // in military time and all mins are 30 sec
    int [] clock  = {01,02,03,04,07 ,  14, 16, 17, 18, 19, 20, 21, 22, 23, 24};
    int holder = 0;
    Handler mHandle = new Handler(); //handler allow us to loop the code over again but within a time period, like every 30 sec
    Handler bHandle = new Handler();
    boolean switcher = false; //switch for the button
    boolean ITswitcher = false;
    boolean clockSwitcher;
    CountDownTimer myCountDownTimer;
    MediaPlayer ring ;
    int minLeft;
    int hourLeft;
    int secLeft;
    int timeLeft;
    final int [] pattern = {100,300,300,300};
    private static final String TAG = MainActivity.class.getSimpleName();






    DateTimeFormatter timeFormatHour = DateTimeFormatter.ofPattern("HH");
    DateTimeFormatter timeFormatMin = DateTimeFormatter.ofPattern("mm");
    DateTimeFormatter timeFormatSec = DateTimeFormatter.ofPattern("ss");
    //idea for the project
    //1. when button is click the app is running/ when click again stop the app
    //2. when running check the clock if it in between a certain time (weapon exp) open sinoalice app
    //(we get the time is doing a and symbol for hour and min)
    //3. extra try to make this app run in the background


    //when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ring = MediaPlayer.create(this,R.raw.ping); //ring is the ping.mp4

        createNotificationChannel();
        //time = TimeZone.getDefault();
    }

    //button for one time notification
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void timer(View v){
        if (ITswitcher == true){
            findViewById(R.id.button).setBackgroundColor(Color.LTGRAY);
            ((Button)findViewById(R.id.button)).setText("OFF");
            mHandle.removeCallbacks(timer);
            bHandle.removeCallbacks(ITtimer);
            ITswitcher = false;
            myCountDownTimer.cancel();

        }
        //when button is 1st click run the code
        if (switcher == false){
            clock(); //countdown timer

            findViewById(R.id.timer).setBackgroundColor(Color.GREEN);
            ((Button)findViewById(R.id.timer)).setText("ON");
            switcher = true;
            timer.run();

            //when the mediaplayer is done, stop the handler and reset the button to an off state
            ring.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mHandle.removeCallbacks(timer);
                    switcher = false;
                    findViewById(R.id.timer).setBackgroundColor(Color.LTGRAY);
                    ((TextView)findViewById(R.id.textView)).setText("Time Left: _:__:__");
                    ((Button)findViewById(R.id.timer)).setText("OFF");
                    myCountDownTimer.cancel();
                }

            });


        }
        else{
            //when button is already click end the code
            findViewById(R.id.timer).setBackgroundColor(Color.LTGRAY);
            //ring.pause();
            mHandle.removeCallbacks(timer);
            //((TextView)findViewById(R.id.textView)).setText("stopped lol");
            ((Button)findViewById(R.id.timer)).setText("OFF");
            myCountDownTimer.cancel();
            switcher = false;
        }
    }

    private Runnable timer = new Runnable() {
        @Override
        public void run() {
            //gets the day/year/time every loop
           // Calendar calendar = Calendar.getInstance();
            //set the format for mins and hour
            /*//using the format to get min and hour as int for the loop.
            int min = Integer.parseInt(timeFormatMin.format(calendar.getTime()));
            int hour = Integer.parseInt(timeFormatHour.format(calendar.getTime()));*/

            ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
            int min = Integer.parseInt(zonedDateTime.format(timeFormatMin));
            int hour = Integer.parseInt(zonedDateTime.format(timeFormatHour));

            //checking the time and see if the current time match the array
            for (int i = 0; i < clock.length; i++){
                if (hour == clock[i]){
                    if(min >= 30    && min <= 59){
                        //launches the app if the app is downloaded and plays a audio file as a alarm

                        pogger();
                        ring.start();
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.nexon.sinoalice");
                        if (launchIntent != null) {
                            //ring.start();


                            //((Button)findViewById(R.id.timer)).setText("STOP");

                            startActivity(launchIntent);
                            //mHandle.removeCallbacks(timer);
                            //mHandle.removeCallbacksAndMessages(null);

                        } else {
                            Toast.makeText(MainActivity.this, "App/Package doesn't exist", Toast.LENGTH_LONG).show();
                        }
                        //end the code if the hour is the same as the array and its between 30 and 59 mins
                        //((TextView)findViewById(R.id.textView2).setText(String.valueOf("start"));
                        //mHandle.removeCallbacks(timer);
                        /*switcher = false;
                        ((Button)findViewById(R.id.timer)).setText("OFF");*/
                    }

                }
            }
            //loop everysec every 30 sec
            mHandle.postDelayed(this, 30000);
            //mHandle.postDelayed(this, 5000);

        }



    };



    //another button to keep going instead of stopping in one notifcation
    public void infinite_timer(View v){
        Log.d("VVVV", "hour is " + holder);
        if(ITswitcher == false){
            //if switchers was already on, turn it off
            if(switcher == true){
                ((Button)findViewById(R.id.timer)).setText("OFF");
                findViewById(R.id.timer).setBackgroundColor(Color.LTGRAY);
                //ring.pause();
                mHandle.removeCallbacks(timer); myCountDownTimer.cancel();
                switcher = false;
            }
            clockSwitcher = false;
            holder = -1;
            findViewById(R.id.button).setBackgroundColor(Color.GREEN);
            ((Button)findViewById(R.id.button)).setText("ON");
           // clock();
            ITtimer.run();
            ITswitcher = true;

        }
        //if switch is true
        else{
            //end code
            findViewById(R.id.button).setBackgroundColor(Color.LTGRAY);
            ((Button)findViewById(R.id.button)).setText("OFF");
            bHandle.removeCallbacks(ITtimer);
            ITswitcher = false;
            myCountDownTimer.cancel();
        }

    }


    private Runnable ITtimer = new Runnable() {
        @Override
        public void run() {
            //PROBLEM how to make the notifcation hit onces but keep going for the other times

            //get the current time
            ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
            int h = Integer.parseInt(zonedDateTime.format(timeFormatHour));
            int m = Integer.parseInt(zonedDateTime.format(timeFormatMin));
            //default holder is 0 for it to test the current hour and if holder != the current hour run the timer
            //if the current hour doesnt fit the holder(holder is alway updated), run the timer code
            if(holder != m ){
                timer.run();
                //if(clockSwitcher == false){
                   // clock();
                  //  clockSwitcher = true;
                }

            

            //when ring is played stop the runner and give the holder value the current hour
            ring.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    myCountDownTimer.cancel();
                    clockSwitcher = false;
                    mHandle.removeCallbacks(timer);
                        holderMover(m);
                    Log.d("KK", "hour is " + holder);
                }

            });
            //loop the code every 30 sec
            bHandle.postDelayed(this, 30000);
        }
    };

    private void holderMover(int h){ holder = h; }

    //this is the notification fuction
    private void pogger(){
        Log.d("BBK", "hour is " + holder);


        //makes when u click on the notification it will sent you to the app with the intent
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.nexon.sinoalice");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(launchIntent);
        PendingIntent resultPendingIntent =  stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        //creates the notifications

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ringA");
        builder.setSmallIcon(R.drawable.ic_baseline_access_alarms_24) ;//set the image
        builder.setContentTitle("Sinoalice Exp time :)"); //set the title
        builder.setContentText("It's time to grind for exp :)"); //set the text in the notification
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setContentIntent(resultPendingIntent);



        ; //set the intent when you click on the notification
        NotificationManagerCompat notifcationManger = NotificationManagerCompat.from(this);
        notifcationManger.notify(100, builder.build());



    }

    //create the channel for the notificication so we can
    private void createNotificationChannel() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "timer";
                String description = "Channel for alarm for when its time to play";
                //IMPORTANCE_HIGH if you want the notification to pop up on screen and play sound like a phonecall or snapchat
                int importance = NotificationManager.IMPORTANCE_HIGH;
                //int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("ringA", name, importance);
                //the below to show the notification on screen
                //   NotificationChannel channel = new NotificationChannel("ringA", name, NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription(description);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                NotificationManager Nm = getSystemService(NotificationManager.class);
                Nm.createNotificationChannel(channel);
            }

        }

        //timer till next event
        public void clock(){
            ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
            int min = Integer.parseInt(zonedDateTime.format(timeFormatMin));
            int hour = Integer.parseInt(zonedDateTime.format(timeFormatHour));
            int sec = Integer.parseInt(zonedDateTime.format(timeFormatSec));


            //this is for when it is pass 10pm est
            if (hour > 22){
                hour = 22 - hour;
            }

            for (int i  = 0; i < clock.length; i++){
                if (hour < clock[i] || (hour == clock[i] && min < 30) ){
                    if(hour+1 == clock[i] && min > 30){
                        hourLeft = 0;
                    }else{
                        hourLeft = (clock[i] - hour) * 3600;
                    }

                    if(min < 30){
                        minLeft = (30 - min) * 60;
                    }else{
                        minLeft = (60 - min + 30) * 60;
                    }

                    secLeft = 60 - sec;
                    timeLeft = hourLeft + minLeft + secLeft;

                    //if the 1st array to match the if statement will stop the code so we wont have to go to the whole arraylist
                    break;
                }
            }

            //the countdown timer till the next event using the data above in clock()
            myCountDownTimer = new CountDownTimer(timeLeft * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    long k = millisUntilFinished;
                    long hourL = k / 3600000;
                    k = millisUntilFinished - (hourL * 3600000);
                    long minL = k / 60000;
                    k = millisUntilFinished - (minL * 60000);


                    long secL = k;
                    if (minL >=10){
                        ((TextView)findViewById(R.id.textView)).setText(String.valueOf(String.format("Time left: %d:%d:%tS", hourL, minL, secL )));
                    }else{
                        ((TextView)findViewById(R.id.textView)).setText(String.valueOf(String.format("Time left: %d:0%d:%tS", hourL, minL, secL )));

                    }

                }

                @Override
                public void onFinish()
                {}
            }.start(); //start the clock
        }

    }