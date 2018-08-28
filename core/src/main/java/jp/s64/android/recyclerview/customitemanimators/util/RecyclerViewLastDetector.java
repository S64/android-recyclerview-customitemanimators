package jp.s64.android.recyclerview.customitemanimators.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerViewLastDetector extends RecyclerView.OnScrollListener {

    public static boolean isLast(@NonNull RecyclerView recycler) {
        final int total = recycler.getLayoutManager().getItemCount();

        Integer maxPos = null;

        for (int i = 0; i < recycler.getChildCount(); i++) {
            final View v = recycler.getChildAt(i);
            final RecyclerView.ViewHolder vh = recycler.findContainingViewHolder(v);

            if (maxPos == null || maxPos < vh.getLayoutPosition()) {
                maxPos = vh.getLayoutPosition();
            }
        }

        return total == 0 || (maxPos != null && maxPos == (total - 1));
    }

    @Nullable
    protected Boolean isLast = null;

    @Nullable
    protected IListener listener = null;

    public RecyclerViewLastDetector() {
        this(null);
    }

    public RecyclerViewLastDetector(@Nullable IListener listener) {
        this.listener = listener;
    }

    @Nullable
    public Boolean isLastOrNotInitialized() {
        return isLast;
    }

    public boolean isLast() {
        return isLast != null ? isLast : false;
    }

    @Override
    public void onScrolled(RecyclerView self, int dx, int dy) {
        super.onScrolled(self, dx, dy);
        onDetect(isLast(self));
    }

    protected void onDetect(boolean isLast) {
        @Nullable final Boolean cache = this.isLast;

        this.isLast = isLast;

        if (cache != Boolean.valueOf(isLast)) {
            notifyChanged();
        }
    }

    protected void notifyChanged() {
        if (listener != null) {
            listener.onLastStateChanged(isLast);
        }
    }

    interface IListener {

        void onLastStateChanged(boolean isLast);

    }

}
