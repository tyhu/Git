/**
@author: denzil
 */

package com.yahoo.inmind.services.activity.control;


import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.aware.Aware_Preferences;
import com.aware.Locations;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.yahoo.inmind.comm.ar.model.ActivityRecognitionEvent;
import com.yahoo.inmind.commons.control.Constants;
import com.yahoo.inmind.services.generic.control.AwareServiceWrapper;
import com.yahoo.inmind.services.generic.control.GenericService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class ActivityRecognitionService extends GenericService {

    public ActivityRecognitionService(){
        super("ActivityRecognitionService", ActivityRecognitionService.class.getPackage().getName());
        if( actions.isEmpty() ) {
            this.actions.add(Constants.ACTION_GOOGLE_ACTIVITY_RECOGNITION);
            this.actions.add(Locations.ACTION_AWARE_LOCATIONS);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity mostProbable = result.getMostProbableActivity();
            JSONArray activities = new JSONArray();
            List<DetectedActivity> otherActivities = result.getProbableActivities();
            for(DetectedActivity activity : otherActivities ) {
                try {
                    JSONObject item = new JSONObject();
                    item.put("activity", getActivityName(activity.getType()));
                    item.put("confidence", activity.getConfidence());
                    activities.put(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Plugin.confidence = mostProbable.getConfidence();
            Plugin.activityType = mostProbable.getType();
            String activity_name = getActivityName(Plugin.activityType);

            ContentValues data = new ContentValues();
            data.put(Provider.Google_Activity_Recognition_Data.TIMESTAMP, System.currentTimeMillis());
            data.put(Provider.Google_Activity_Recognition_Data.DEVICE_ID, AwareServiceWrapper.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
            data.put(Provider.Google_Activity_Recognition_Data.ACTIVITY_NAME, activity_name);
            data.put(Provider.Google_Activity_Recognition_Data.ACTIVITY_TYPE, Plugin.activityType);
            data.put(Provider.Google_Activity_Recognition_Data.CONFIDENCE, Plugin.confidence);
            data.put(Provider.Google_Activity_Recognition_Data.ACTIVITIES, activities.toString());

            getContentResolver().insert(Provider.Google_Activity_Recognition_Data.CONTENT_URI, data);

            if ( Plugin.DEBUG ) {
            	Log.d(Plugin.TAG, "User is: " + activity_name + " (conf:" + Plugin.confidence + ")");
            }

            ActivityRecognitionEvent event = new ActivityRecognitionEvent();
            event.setActivityType(Plugin.activityType);
            event.setConfidence(Plugin.confidence);
            mb.send( event );
        }
    }

    public static String getActivityName(int type) {
        switch (type) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
            case DetectedActivity.RUNNING:
                return "running";
            case DetectedActivity.WALKING:
                return "walking";
            default:
                return "unknown";
        }
    }

    @Override
    public void doAfterBind() {

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


}
