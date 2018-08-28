package jp.s64.android.recyclerview.customitemanimators.liftup;

import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewPropertyAnimator;

import java.util.LinkedList;
import java.util.List;

import jp.s64.android.recyclerview.customitemanimators.core.AdditionInfo;
import jp.s64.android.recyclerview.customitemanimators.core.CustomizableDefaultItemAnimator;

public class FadeLiftUpItemAnimator extends CustomizableDefaultItemAnimator {

    @NonNull
    private final RecyclerView attachedRecycler;

    public FadeLiftUpItemAnimator(@NonNull RecyclerView target) {
        setMoveDuration(getAddDuration());
        this.attachedRecycler = target;
    }

    public FadeLiftUpItemAnimator(@NonNull RecyclerView target, int duration) {
        setAddDuration(duration);
        setMoveDuration(duration);
        this.attachedRecycler = target;
    }

    @Override
    protected boolean waitAdditionAfterFinishMoves(int pendingMoves) {
        int visibleViewCount = attachedRecycler.getChildCount();

        if (pendingMoves != visibleViewCount - 1) {
            return true; // 移動する数が合わないので最下部ではない
        }

        int addedItemCount = attachedRecycler.getAdapter().getItemCount();

        for (int i = 0; i < visibleViewCount; i++) {
            final RecyclerView.ViewHolder target = attachedRecycler.findContainingViewHolder(attachedRecycler.getChildAt(i));
            int targetPosition = target.getAdapterPosition();

            if (targetPosition < addedItemCount - visibleViewCount) {
                return true; // 移動するものの位置が下に寄ってない
            }
        }

        return false;
    }

    @NonNull
    @Override
    protected ViewPropertyAnimator createAddAnimation(
            @NonNull AdditionInfo info,
            @NonNull ViewPropertyAnimator animator,
            long duration,
            @Nullable TimeInterpolator interpolator,
            @NonNull AnimatorListenerAdapter listener
    ) {
        if (info.waitAdditionAfterFinishMoves != Boolean.TRUE) {
            info.holder.itemView.setTranslationY(
                    info.holder.itemView.getMeasuredHeight()
            );
        }
        return super.createAddAnimation(info, animator, duration, interpolator, listener)
                .translationY(0f);
    }

}
