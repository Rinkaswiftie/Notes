package com.rinka.notes;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

import com.rinka.notes.providers.ExecutorProvider;

import java.util.concurrent.ExecutorService;

public class NotesApp extends Application {
    private static final ExecutorService executorService = ExecutorProvider.getOrInitializeExecutor();
    private static final Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

    public static ExecutorService getExecutorService() {
        return executorService;
    }
    public static Handler getMainThreadHandler() {
        return mainThreadHandler;
    }
}
