package com.yahoo.inmind.services.generic.control;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aware.Accelerometer;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Battery;
import com.aware.Locations;
import com.google.android.gms.location.LocationRequest;
import com.yahoo.inmind.commons.control.Constants;
import com.yahoo.inmind.comm.generic.model.MBRequest;
import com.yahoo.inmind.comm.battery.model.BatterySensorEvent;
import com.yahoo.inmind.services.activity.control.ActivityRecognitionService;
import com.yahoo.inmind.services.location.control.LocationService;
import com.yahoo.inmind.services.location.view.LocationSettings;

/**
 * Created by oscarr on 8/6/15.
 */
public class AwareServiceWrapper extends GenericService {

    public AwareServiceWrapper(){
        super("AwareServiceWrapper", null);
        Intent awareIntent = new Intent(mContext, Aware.class);
        mContext.startService(awareIntent);
        if( actions.isEmpty() ) {
            this.actions.add(Locations.ACTION_AWARE_LOCATIONS);
            this.actions.add(Battery.ACTION_AWARE_BATTERY_CHARGING);
            this.actions.add(Battery.ACTION_AWARE_BATTERY_DISCHARGING);
            this.actions.add(Battery.ACTION_AWARE_BATTERY_LOW);
            this.actions.add(Battery.ACTION_AWARE_PHONE_SHUTDOWN);
            this.actions.add(Accelerometer.ACTION_AWARE_ACCELEROMETER);
            this.actions.add(Constants.ACTION_GOOGLE_ACTIVITY_RECOGNITION);
        }
    }

    @Override
    public void start() {
        super.start();
        startPlugin(mContext, ActivityRecognitionService.class.getPackage().getName());
        startPlugin(mContext, LocationService.class.getPackage().getName());
        setSetting(mContext, Aware_Preferences.STATUS_BATTERY, true);
    }


    @Override
    public void doAfterBind() {

    }

    public static String getSetting(Context mContext, String setting) {
        return Aware.getSetting(mContext, setting);
    }

    public static void setSetting(Context context, String key, Object value) {
        Aware.setSetting(context, getPreference( key ), value);
        context.sendBroadcast( new Intent(Aware.ACTION_AWARE_REFRESH) );
    }

    private static String getPreference(String plugin){
        if( plugin.equals( "ACCELEROMETER" ) ){
            return Aware_Preferences.STATUS_ACCELEROMETER;
        }
        return plugin;
    }

    public static void startPlugin(Context mContext, MBRequest request) {
        Aware.startPlugin(mContext, getPackageName(request));
    }

    public static void stopPlugin(Context mContext, MBRequest request) {
        Aware.stopPlugin(mContext, getPackageName(request));
    }

    public static void startPlugin(Context mContext, String packageName) {
        Aware.startPlugin(mContext, packageName);
    }

    public static void stopPlugin(Context mContext, String packageName) {
        Aware.stopPlugin( mContext, packageName );
    }

    //TODO: finish it
    private static String getPackageName( MBRequest request ){
        String name = (String) request.get(Constants.SERVICE_NAME);
        if( name.equalsIgnoreCase(Constants.SERVICE_LOCATION) ){
            name = LocationService.class.getPackage().getName();
        }else if( name.equalsIgnoreCase(Constants.SERVICE_AR) ){
            name = ActivityRecognitionService.class.getPackage().getName();
        }
        return name;
    }

    public static void stopPlugins() {
        setSetting(mContext, Aware.ACTION_AWARE_STOP_PLUGINS, true);
    }

    public static void stopSensors() {
        setSetting(mContext, Aware.ACTION_AWARE_STOP_SENSORS, true);
    }


    public void onEvent( BatterySensorEvent event){
        if(event.getAction().equals(Battery.ACTION_AWARE_BATTERY_CHARGING)
                || event.getAction().equals(Battery.ACTION_AWARE_BATTERY_CHARGING_AC)
                || event.getAction().equals(Battery.ACTION_AWARE_BATTERY_CHARGING_USB)){
            Log.e("battery", "Charging");
            AwareServiceWrapper.setSetting(mContext, LocationSettings.ACCURACY_GOOGLE_FUSED_LOCATION, LocationRequest.PRIORITY_HIGH_ACCURACY);
            mContext.sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
        }else if(event.getAction().equals(Battery.ACTION_AWARE_BATTERY_DISCHARGING)){
            Log.e("battery","Discharging");
            AwareServiceWrapper.setSetting(mContext, LocationSettings.ACCURACY_GOOGLE_FUSED_LOCATION, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            mContext.sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
        }else if(event.getAction().equals(Battery.ACTION_AWARE_BATTERY_CHARGING)){
            Log.e("battery","Low");
            AwareServiceWrapper.setSetting(mContext, LocationSettings.ACCURACY_GOOGLE_FUSED_LOCATION, LocationRequest.PRIORITY_LOW_POWER);
            mContext.sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
        }else if(event.getAction().equals(Battery.ACTION_AWARE_PHONE_SHUTDOWN)){
            Log.e("battery","No power");
            AwareServiceWrapper.setSetting(mContext, LocationSettings.ACCURACY_GOOGLE_FUSED_LOCATION, LocationRequest.PRIORITY_NO_POWER);
            mContext.sendBroadcast(new Intent(Aware.ACTION_AWARE_REFRESH));
        }
    }


    @Override
    public void onDestroy(){
        if( mContext == null ){
            mContext = getApplicationContext();
        }
        if( mContext != null ) {
            stopPlugins();
            stopSensors();
            mContext.stopService(new Intent(mContext, Aware.class));
        }
        super.onDestroy();
    }

}
