package com.github.anrimian.fragmentnavigationstacktestapp;

import android.app.Application;

import com.github.anrimian.acrareportdialog.AcraReportDialog;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.config.ConfigurationBuilder;

import ch.acra.acra.BuildConfig;

/**
 * Created on 20.10.2017.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            AcraReportDialog.setupCrashDialog(this);
        }
    }
}
