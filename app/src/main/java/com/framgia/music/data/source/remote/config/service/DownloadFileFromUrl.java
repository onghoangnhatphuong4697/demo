package com.framgia.music.data.source.remote.config.service;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import com.framgia.music.R;
import com.framgia.music.screen.OnDownloadListener;

/**
 * Created by Admin on 3/24/2018.
 */

public class DownloadFileFromUrl {

    private static final String FILE_MP3 = ".mp3";
    private long mIdDownload;
    private OnDownloadListener mDownloadListener;
    private String mFileName;

    public DownloadFileFromUrl(Context context, OnDownloadListener downloadListener) {
        mDownloadListener = downloadListener;
        registerBroadcastReceive(context);
    }

    private void registerBroadcastReceive(Context context) {
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(onCompleteReceive, intentFilter);
    }

    public void startDownload(Context context, String url, String fileName) {
        mFileName = fileName + FILE_MP3;
        DownloadManager mDownloadManager =
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, mFileName);
        mIdDownload = mDownloadManager.enqueue(request);
    }

    private BroadcastReceiver onCompleteReceive = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (referenceId == mIdDownload) {
                mDownloadListener.onDownloadSuccess(mFileName + " " + context.getResources()
                        .getString(R.string.add_to_the_download_list));
                context.unregisterReceiver(onCompleteReceive);
            }
        }
    };
}
