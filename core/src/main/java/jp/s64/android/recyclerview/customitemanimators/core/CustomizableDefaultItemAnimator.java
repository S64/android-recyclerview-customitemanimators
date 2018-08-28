package jp.s64.android.recyclerview.customitemanimators.core;

import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewPropertyAnimator;

public class CustomizableDefaultItemAnimator extends AbsItemAnimator {

    protected static final float BEFORE_ANIMATE_ALPHA = 0f;
    protected static final float AFTER_ANIMATE_ALPHA = 1f;

    protected static final float AFTER_ANIMATE_POSITION_X = 0f;
    protected static final float AFTER_ANIMATE_POSITION_Y = 0f;

    @Override
    protected void resetToAfterAnimatePositionX(@NonNull RecyclerView.ViewHolder holder) {
        holder.itemView.setTranslationX(AFTER_ANIMATE_POSITION_X);
    }

    @Override
    protected void resetToAfterAnimatePositionY(@NonNull RecyclerView.ViewHolder holder) {
        holder.itemView.setTranslationY(AFTER_ANIMATE_POSITION_Y);
    }

    @Override
    protected void resetToAfterAnimateVisual(@NonNull RecyclerView.ViewHolder holder) {
        holder.itemView.setAlpha(AFTER_ANIMATE_ALPHA);
    }

    @Override
    protected boolean waitAdditionAfterFinishMoves(int pendingMoves) {
        return true;
    }

    @Override
    protected void initBeforeAdditionAnimate(@NonNull RecyclerView.ViewHolder holder) {
        holder.itemView.setAlpha(BEFORE_ANIMATE_ALPHA);
    }

    @Override
    protected void initBeforeChangeAnimate(@NonNull RecyclerView.ViewHolder holder) {
        initBeforeAdditionAnimate(holder);
    }

    @NonNull
    @Override
    protected ViewPropertyAnimator animateToAfterMovedPositionX(@NonNull ViewPropertyAnimator animator) {
        return animator
                .translationX(AFTER_ANIMATE_POSITION_X);
    }

    @NonNull
    @Override
    protected ViewPropertyAnimator animateToAfterMovedPositionY(@NonNull ViewPropertyAnimator animator) {
        return animator
                .translationY(AFTER_ANIMATE_POSITION_Y);
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
        return animator
                .alpha(AFTER_ANIMATE_ALPHA)
                .setDuration(duration)
                .setInterpolator(interpolator)
                .setListener(listener);
    }

    @NonNull
    @Override
    protected ViewPropertyAnimator createMoveAnimation(
            @NonNull ViewPropertyAnimator animator,
            long duration,
            @Nullable TimeInterpolator interpolator,
            @NonNull AnimatorListenerAdapter listener
    ) {
        return animator
                .setDuration(duration)
                .setInterpolator(interpolator)
                .setListener(listener);
    }

    @NonNull
    @Override
    protected ViewPropertyAnimator createChangeOldAnimation(
            @NonNull ViewPropertyAnimator animator,
            long duration,
            @Nullable TimeInterpolator interpolator,
            @NonNull AnimatorListenerAdapter listener
    ) {
        return animator
                .alpha(BEFORE_ANIMATE_ALPHA)
                .setDuration(duration)
                .setInterpolator(interpolator)
                .setListener(listener);
    }

    @NonNull
    @Override
    protected ViewPropertyAnimator createChangeNewAnimation(
            @NonNull ViewPropertyAnimator animator,
            long duration,
            @Nullable TimeInterpolator interpolator,
            @NonNull AnimatorListenerAdapter listener
    ) {
        return animator
                .alpha(AFTER_ANIMATE_ALPHA)
                .translationX(AFTER_ANIMATE_POSITION_X)
                .translationY(AFTER_ANIMATE_POSITION_Y)
                .setDuration(duration)
                .setInterpolator(interpolator)
                .setListener(listener);
    }

    @NonNull
    @Override
    protected ViewPropertyAnimator createRemoveAnimation(@NonNull ViewPropertyAnimator animator, long duration, @Nullable TimeInterpolator interpolator, @NonNull AnimatorListenerAdapter listener) {
        return animator
                .alpha(BEFORE_ANIMATE_ALPHA)
                .setDuration(duration)
                .setInterpolator(interpolator)
                .setListener(listener);
    }

}
