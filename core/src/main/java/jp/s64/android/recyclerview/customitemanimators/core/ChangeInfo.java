package jp.s64.android.recyclerview.customitemanimators.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

public class ChangeInfo implements IInfo {
    
    @Nullable
    public RecyclerView.ViewHolder oldHolder;
    
    @Nullable
    public RecyclerView.ViewHolder newHolder;
    
    public final float fromLeft;
    public final float toLeft;
    public final float fromTop;
    public final float toTop;
    
    ChangeInfo(
            float fromLeft,
            float fromTop,
            float toLeft,
            float toTop
    ) {
        this.oldHolder = null;
        this.newHolder = null;
        this.fromLeft = fromLeft;
        this.fromTop = fromTop;
        this.toLeft = toLeft;
        this.toTop = toTop;
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.Change;
    }

    public static class Builder {

        @Nullable
        Float fromLeft;
        
        @Nullable
        Float toLeft;
        
        @Nullable
        Float fromTop;
        
        @Nullable
        Float toTop;
        
        public Builder() {}

        @NonNull
        public Builder fromLeft(float fromLeft) {
            this.fromLeft = fromLeft;
            return this;
        }

        @NonNull
        public Builder toLeft(float toLeft) {
            this.toLeft = toLeft;
            return this;
        }

        @NonNull
        public Builder fromTop(float fromTop) {
            this.fromTop = fromTop;
            return this;
        }

        @NonNull
        public Builder toTop(float toTop) {
            this.toTop = toTop;
            return this;
        }
        
        public ChangeInfo build() {
            return new ChangeInfo(
                    fromLeft,
                    fromTop,
                    toLeft,
                    toTop
            );
        }
        
    }
    
}
