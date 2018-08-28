package jp.s64.android.recyclerview.customitemanimators.liftup;

import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewPropertyAnimator;

import jp.s64.android.recyclerview.customitemanimators.core.AdditionInfo;

public class PlaceAndFadeLiftUpItemAnimator extends LiftUpItemAnimator {

    public PlaceAndFadeLiftUpItemAnimator(@NonNull RecyclerView target) {
        super(target);
    }

    public PlaceAndFadeLiftUpItemAnimator(@NonNull RecyclerView target, int duration) {
        super(target, duration);
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
        final ViewPropertyAnimator ret;

        if (info.waitAdditionAfterFinishMoves == Boolean.FALSE) {
            info.holder.itemView.setTranslationY(
                    info.holder.itemView.getMeasuredHeight()
            );
            ret = animator
                    .translationY(0f);
        } else {
            ret = animator;
        }

        return ret
                .alpha(AFTER_ANIMATE_ALPHA)
                .setDuration(duration)
                .setInterpolator(interpolator)
                .setListener(listener);
    }

}
