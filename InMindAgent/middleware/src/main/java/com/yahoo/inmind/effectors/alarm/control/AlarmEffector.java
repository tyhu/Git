package com.yahoo.inmind.effectors.alarm.control;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.AlarmClock;

import com.yahoo.inmind.commons.control.Constants;
import com.yahoo.inmind.commons.control.Util;
import com.yahoo.inmind.commons.rules.model.DecisionRule;
import com.yahoo.inmind.effectors.generic.control.EffectorDataReceiver;
import com.yahoo.inmind.effectors.generic.control.EffectorObserver;
import com.yahoo.inmind.effectors.alarm.model.AlarmVO;
import com.yahoo.inmind.services.generic.control.ServiceLocator;
import com.yahoo.inmind.services.calendar.model.CalendarEventVO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by oscarr on 9/29/15.
 */
public class AlarmEffector extends EffectorObserver {

    private Context context;

    public AlarmEffector(Handler handler, Context context) {
        super(handler, context);
        this.actions.add(Constants.ACTION_SET_ALARM);
        this.context = context;
    }

    public void setRepeatingAlarm( AlarmVO alarmVO ){
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent( mContext, EffectorDataReceiver.class );
        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, alarmVO.getTriggerAtMillis(),
                alarmVO.getIntervalMillis(), pi);
    }

    public void cancelAlarm( AlarmVO alarmVO ){
        Intent intent = new Intent(mContext, EffectorDataReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void setAlarm( Intent intent ){
        DecisionRule decisionRule = ServiceLocator.getInstance(context).getDecisionRule(intent.
                getIntExtra(Constants.DECISION_RULE_ELEMENT, 0));
        Intent i = new Intent( AlarmClock.ACTION_SET_ALARM );
//        i.putExtra(AlarmClock.EXTRA_HOUR, 9);
//        i.putExtra(AlarmClock.EXTRA_MINUTES, 37);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public void setAlarm( CalendarEventVO calendarEventVO, Date date ){
        Intent i = new Intent( AlarmClock.ACTION_SET_ALARM );
        String[] dateString = Util.formatDate( date, "TIME" ).split(":");
        i.putExtra( AlarmClock.EXTRA_MESSAGE, calendarEventVO.getDescription() );
        i.putExtra(AlarmClock.EXTRA_HOUR, dateString[0]);
        i.putExtra( AlarmClock.EXTRA_MINUTES, dateString[1] );
        ArrayList days = new ArrayList();
        days.add( Util.getDateField(date, Calendar.DAY_OF_WEEK) );
        i.putExtra( AlarmClock.EXTRA_DAYS, days );
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }


    public void playRingtone(Integer ringtoneType){
        ringtoneType = ringtoneType == null? RingtoneManager.TYPE_NOTIFICATION : ringtoneType;
        Uri notification = RingtoneManager.getDefaultUri( ringtoneType );
        MediaPlayer mp = MediaPlayer.create( mContext, notification);
        mp.start();
    }
}
