package com.rifkiystark.goletfilm.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rifkiystark.goletfilm.R;
import com.rifkiystark.goletfilm.model.Films;

import java.util.concurrent.ExecutionException;

import static com.rifkiystark.goletfilm.database.DatabaseContract.CONTENT_URI;

public class StackRemoteFactoryViews implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private int mAppWidgetId;
    Cursor favoriteCursor;


    public StackRemoteFactoryViews(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();
        favoriteCursor = mContext.getContentResolver().query(CONTENT_URI,null,null,null,null);
        Binder.restoreCallingIdentity(identityToken);

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return favoriteCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final Films films = getItem(position);
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),R.layout.widget_item);
        try {
            Bitmap preview = Glide.with(mContext)
                    .asBitmap()
                    .load(films.getImg())
                    .apply(new RequestOptions().fitCenter())
                    .submit()
                    .get();
            remoteViews.setImageViewBitmap(R.id.imageView , preview);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        remoteViews.setTextViewText(R.id.tvTitle,films.getTitle());
        Bundle extras = new Bundle();
        extras.putInt(FavoriteStackWidget.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        remoteViews.setOnClickFillInIntent(R.id.imageView, fillInIntent);
        return remoteViews;
    }

    private Films getItem(int i) {
        if(!favoriteCursor.moveToPosition(i)){
            throw new IllegalStateException("Position Invalid");

        }
        return new Films(favoriteCursor);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
