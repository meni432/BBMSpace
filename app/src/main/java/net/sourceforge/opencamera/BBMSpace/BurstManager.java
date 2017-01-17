package net.sourceforge.opencamera.BBMSpace;

import android.location.Location;
import android.telephony.SmsManager;
import android.util.Log;

import net.sourceforge.opencamera.MyDebug;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by meni on 13/01/17.
 */
public class BurstManager {
    public static final String TAG = BurstManager.class.getCanonicalName();
    private static BurstManager ourInstance = new BurstManager();

    public static final int INIT_VAL = -999;

    private long mFirstTimestampCapture;
    private long mLastTimeCapture;

    private long mBurstInterval;
    private long mBurstSlowTimerPref;
    private long mEndTripTimerPref;
    private int mBurstDivider;

    private long photoCounter;

    private boolean initialValues;
    private boolean slowMode;

    //SMS
    private long lastSmsSend;
    public  String smsBasePhoneNumber = "+972546450344";
    public  long smsSendIntervalNumber = 120000;
    //Location
    private Location lastLocation;

    //Battery
    private double lastBatteySnampshot;

    public static BurstManager getInstance() {
        return ourInstance;
    }

    private BurstManager() {
        cleanManager();
    }

    public boolean continueBurst() {
        if (!initialValues) {
            if (MyDebug.LOG)
                Log.d(TAG, "invalid initial values");
        }
        photoCounter++;
        long timestamp = System.currentTimeMillis();
        if (mFirstTimestampCapture == INIT_VAL) {
            mFirstTimestampCapture = timestamp;
        }
        if (mFirstTimestampCapture + mBurstSlowTimerPref < timestamp && !slowMode) {
            if (MyDebug.LOG)
                Log.d(TAG, "slow burst divider ");
            slowMode = true;
            mBurstInterval /= mBurstDivider;
        }
        if (mEndTripTimerPref + mFirstTimestampCapture < timestamp) {
            endTrip();
            return false;
        }
        if (lastSmsSend == INIT_VAL || timestamp - lastSmsSend > smsSendIntervalNumber){
            if (MyDebug.LOG)
                Log.d(TAG, "send find me sms");
            lastSmsSend = timestamp;
            sendFindMeSMS();
        }
        mLastTimeCapture = timestamp;

        return true;
    }

    public long getBurstInterval() {
        return mBurstInterval;
    }

    public void setmEndTripTimerPref(long mEndTripTimerPref) {
        this.mEndTripTimerPref = mEndTripTimerPref;
        checkInputs();
    }

    public void setBurstInterval(long mBrustInterval) {
        this.mBurstInterval = mBrustInterval;
        checkInputs();
    }

    public void setBurstSlowTimerPref(long mBrustSlowTimerInterval) {
        this.mBurstSlowTimerPref = mBrustSlowTimerInterval;
        checkInputs();
    }

    public void setBurstDivider(int mBrustDivider) {
        if (mBurstDivider > 1) {
            this.mBurstDivider = mBrustDivider;
        }else {
            this.mBurstDivider = 1;
        }
        checkInputs();
    }

    public void checkInputs() {
        if (mBurstInterval != INIT_VAL
                && mBurstDivider != INIT_VAL
                && mBurstSlowTimerPref != INIT_VAL
                && mEndTripTimerPref != INIT_VAL) {
            initialValues = true;
        } else {
            initialValues = false;
        }
    }

    public void cleanManager() {
        initialValues = false;
        mFirstTimestampCapture = INIT_VAL;
        mLastTimeCapture = INIT_VAL;
        mBurstInterval = INIT_VAL;
        mBurstSlowTimerPref = INIT_VAL;
        mBurstDivider = INIT_VAL;
        mEndTripTimerPref = INIT_VAL;
        lastSmsSend = INIT_VAL;
        lastBatteySnampshot = INIT_VAL;
        photoCounter = 0;
        slowMode = false;
    }

    public double getLastBatteySnampshot() {
        return lastBatteySnampshot;
    }

    public void setLastBatteySnampshot(double lastBatteySnampshot) {
        this.lastBatteySnampshot = lastBatteySnampshot;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public void endTrip() {
        sendFindMeSMS();
    }

    public void sendFindMeSMS() {
        StringBuilder stringBuilder = new StringBuilder();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm:ss");
        stringBuilder.append("INFO: ");
        stringBuilder.append(" time: " + parser.format(date));
        if (lastLocation != null) {
            stringBuilder.append(" {lat "+ lastLocation.getLatitude() + " , lot " + lastLocation.getLongitude() + " } ");
        }
        if (lastBatteySnampshot != INIT_VAL) {
            stringBuilder.append(" bat{ "+lastBatteySnampshot + " } ");
        }
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(smsBasePhoneNumber, null, stringBuilder.toString(), null, null);

        Log.d(TAG, "send sms: "+stringBuilder.toString());

    }

    public String getSmsBasePhoneNumber() {
        return smsBasePhoneNumber;
    }

    public void setSmsBasePhoneNumber(String smsBasePhoneNumber) {
        this.smsBasePhoneNumber = smsBasePhoneNumber;
    }

    public long getSmsSendIntervalNumber() {
        return smsSendIntervalNumber;
    }

    public void setSmsSendIntervalNumber(long smsSendIntervalNumber) {
        this.smsSendIntervalNumber = smsSendIntervalNumber;
    }
}
