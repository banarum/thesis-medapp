package com.koenigmed.luomanager.util;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Thread.currentThread;


public class RxUtil {

    /**
     * Enum to pass in Observable instead of Void,
     * when no data passing is needed
     */
    public enum Irrelevant {
        INSTANCE
    }

    public static Disposable runWithDelay(Runnable action, long delayInMs) {
        return Observable.timer(delayInMs, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(l -> action.run());
    }

    public static Disposable runByIntervalDisposable(Runnable action, long delayInMs) {
        return Observable.interval(delayInMs, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(l -> action.run());
    }

    /**
     * Выводим в дебажный лог запись с указанием имени текущего потока
     *
     * @param tag            тэг записи
     * @param logDescription описание для вывода перед именем потока
     */
    public static void printCurrentThread(String tag, String logDescription) {
        Log.d(tag, logDescription + " on: " + currentThread().getName());
    }
}
