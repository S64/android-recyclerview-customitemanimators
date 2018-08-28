package jp.s64.android.recyclerview.customitemanimators.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

public class MoveInfo implements IInfo {
    
    @NonNull
    public final RecyclerView.ViewHolder holder;
    
    public final float fromLeft;
    public final float toLeft;
    public final float fromTop;
    public final float toTop;
    
    MoveInfo(
            @NonNull RecyclerView.ViewHolder holder,
            float fromLeft,
            float fromTop,
            float toLeft,
            float toTop
    ) {
        this.holder = holder;
        this.fromLeft = fromLeft;
        this.toLeft = toLeft;
        this.fromTop = fromTop;
        this.toTop = toTop;
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.Move;
    }

    public static class Builder {
        
        @Nullable
        RecyclerView.ViewHolder holder;

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
        public Builder holder(@Nullable RecyclerView.ViewHolder holder) {
            this.holder = holder;
            return this;
        }
        
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
        
        @NonNull
        public MoveInfo build() {
            return new MoveInfo(
                    holder,
                    fromLeft,
                    fromTop,
                    toLeft,
                    toTop
            );
        }
        
    }
    
}
