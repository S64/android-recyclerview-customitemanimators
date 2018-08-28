package jp.s64.android.recyclerview.customitemanimators.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

public class AdditionInfo implements IInfo {

    @NonNull
    public final RecyclerView.ViewHolder holder;

    @Nullable
    public Boolean waitAdditionAfterFinishMoves;

    @Nullable
    public Integer movesPending;

    public AdditionInfo(@NonNull RecyclerView.ViewHolder holder) {
        this.holder = holder;
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.Addition;
    }

}
