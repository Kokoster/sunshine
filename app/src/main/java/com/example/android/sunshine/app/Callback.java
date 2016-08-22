package com.example.android.sunshine.app;

import android.net.Uri;

/**
 * A callback interface that all activities containing this fragment must
 * implement. This mechanism allows activities to be notified of item
 * selections.
 */
public interface Callback {
    public void onItemSelected(Uri dateUri);
}
