package org.moocology.ocr;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import org.acra.ACRA;
import org.acra.ErrorReporter;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.EmailIntentSender;

import io.fabric.sdk.android.Fabric;


@ReportsCrashes(
//        formKey = "", // will not be used
        mailTo = "acra.mail@yandex.ru",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
 )


public class MyApplication extends Application {
    private static GoogleAnalytics analytics;
    private static Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-65595807-1"); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);



        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        ACRA.getErrorReporter().setReportSender(new EmailIntentSender(getApplicationContext()));
        ErrorReporter.getInstance().checkReportsOnApplicationStart();
//        Crashlytics
        Fabric.with(this, new Crashlytics());


    }
    public static Tracker tracker() {
        return tracker;
    }
    public static GoogleAnalytics analytics() {
        return analytics;
    }

}
