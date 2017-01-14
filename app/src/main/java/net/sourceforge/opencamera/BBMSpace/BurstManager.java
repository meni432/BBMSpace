package net.sourceforge.opencamera.BBMSpace;

import android.util.Log;

import net.sourceforge.opencamera.MyDebug;

/**
 * Created by meni on 13/01/17.
 */
public class BurstManager {
    public static final String TAG = BurstManager.class.getCanonicalName();
    private static BurstManager ourInstance = new BurstManager();

    public static final int INIT_VAL = -999;

    long mFirstTimestampCapture;
    long mLastTimeCapture;

    long mBurstInterval;
    long mBurstSlowTimerPref;
    long mEndTripTimerPref;
    int mBurstDivider;

    long photoCounter;

    boolean initialValues;
    boolean slowMode;

    public static BurstManager getInstance() {
        return ourInstance;
    }

    private BurstManager() {
        initialValues = false;
        mFirstTimestampCapture = INIT_VAL;
        mLastTimeCapture = INIT_VAL;
        mBurstInterval = INIT_VAL;
        mBurstSlowTimerPref = INIT_VAL;
        mBurstDivider = INIT_VAL;
        mEndTripTimerPref = INIT_VAL;
        photoCounter = 0;
        slowMode = false;
    }

    public boolean continueBrust() {
        if (!initialValues){
            if (MyDebug.LOG)
                Log.d(TAG, "invalid initial values");
        }
        photoCounter++;
        long timestamp = System.currentTimeMillis();
        if (mFirstTimestampCapture == -1) {
            mFirstTimestampCapture = timestamp;
        } else {
            mLastTimeCapture = timestamp;
        }

        if (mBurstSlowTimerPref + timestamp > mFirstTimestampCapture && !slowMode) {
            slowMode = true;
            mBurstInterval /= mBurstDivider;
        }

        if (mEndTripTimerPref + timestamp > mFirstTimestampCapture) {
            return false;
        }

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
        this.mBurstDivider = mBrustDivider;
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
}
