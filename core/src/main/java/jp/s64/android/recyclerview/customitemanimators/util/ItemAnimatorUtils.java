package jp.s64.android.recyclerview.customitemanimators.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

public class ItemAnimatorUtils {

    public static long maxSingleDuration(@NonNull RecyclerView.ItemAnimator animator) {
        return Math.max(
                animator.getAddDuration(),
                Math.max(
                        animator.getChangeDuration(),
                        Math.max(
                                animator.getMoveDuration(),
                                animator.getRemoveDuration()
                        )
                )
        );
    }

    public static long maxDuration(@NonNull RecyclerView.ItemAnimator animator) {
        return Math.max(
                maxSingleDuration(animator),
                animator.getMoveDuration() + animator.getAddDuration()
        );
    }

}
