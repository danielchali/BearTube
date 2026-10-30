package org.schabi.newpipe.info_list.dialog;

import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import org.schabi.newpipe.extractor.stream.StreamInfoItem;

public class StreamDialogEntry {

    @StringRes
    public final int resource;
    @DrawableRes
    public final int iconRes;
    @NonNull
    public final StreamDialogEntryAction action;

    public StreamDialogEntry(@StringRes final int resource,
                             @NonNull final StreamDialogEntryAction action) {
        this(resource, 0, action);
    }

    public StreamDialogEntry(@StringRes final int resource,
                             @DrawableRes final int iconRes,
                             @NonNull final StreamDialogEntryAction action) {
        this.resource = resource;
        this.iconRes = iconRes;
        this.action = action;
    }

    public String getString(@NonNull final Context context) {
        return context.getString(resource);
    }

    public interface StreamDialogEntryAction {
        void onClick(Fragment fragment, StreamInfoItem infoItem);
    }
}
