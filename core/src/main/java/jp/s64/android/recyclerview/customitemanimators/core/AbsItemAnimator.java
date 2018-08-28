package jp.s64.android.recyclerview.customitemanimators.core;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.ViewPropertyAnimator;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsItemAnimator extends SimpleItemAnimator {

    @Nullable
    protected final TimeInterpolator interpolator;
    
    protected AbsItemAnimator() {
        this(null);
    }
    
    protected AbsItemAnimator(@Nullable TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }

    protected final List<RecyclerView.ViewHolder> pendingRemovals = new ArrayList<>();
    protected final List<AdditionInfo> pendingAdditions = new ArrayList<>();
    protected final List<MoveInfo> pendingMoves = new ArrayList<>();
    protected final List<ChangeInfo> pendingChanges = new ArrayList<>();

    protected final List<List<AdditionInfo>> additionsList = new ArrayList<>();
    protected final List<List<MoveInfo>> movesList = new ArrayList<>();
    protected final List<List<ChangeInfo>> changesList = new ArrayList<>();

    protected final List<RecyclerView.ViewHolder> addAnimations = new ArrayList<>();
    protected final List<RecyclerView.ViewHolder> moveAnimations = new ArrayList<>();
    protected final List<RecyclerView.ViewHolder> removeAnimations = new ArrayList<>();
    protected final List<RecyclerView.ViewHolder> changeAnimations = new ArrayList<>();

    @Override
    public void runPendingAnimations() {
        boolean removalsPending = !pendingRemovals.isEmpty();
        final int movesPending = pendingMoves.size();
        boolean changesPending = !pendingChanges.isEmpty();
        boolean additionsPending = !pendingAdditions.isEmpty();

        if (!removalsPending && (movesPending == 0) && !additionsPending && !changesPending) {
            return; // do nothing
        }

        if (removalsPending) {
            for (RecyclerView.ViewHolder holder : pendingRemovals) {
                animateRemoveImpl(holder);
            }
            pendingRemovals.clear();
        }

        if (movesPending != 0) {
            final List<MoveInfo> copiedMoves = new ArrayList<>(pendingMoves);
            movesList.add(copiedMoves); // *1 Added!
            pendingMoves.clear();

            Runnable mover = new Runnable() {
                @Override
                public void run() {
                    for (MoveInfo info : copiedMoves) {
                        animateMoveImpl(
                                info.holder,
                                info.fromLeft,
                                info.fromTop,
                                info.toLeft,
                                info.toTop
                        );
                    }
                    copiedMoves.clear();
                    movesList.remove(copiedMoves); // *1 Removed!
                }
            };

            if (removalsPending) {
                ViewCompat.postOnAnimationDelayed(
                        copiedMoves.get(0).holder.itemView,
                        mover,
                        getRemoveDuration()
                );
            } else {
                mover.run();
            }
        }

        if (changesPending) {
            final ArrayList<ChangeInfo> copiedChanges = new ArrayList<>(pendingChanges);
            changesList.add(copiedChanges); // *2 Added!
            pendingChanges.clear();

            Runnable changer = new Runnable() {
                @Override
                public void run() {
                    for(ChangeInfo info : copiedChanges) {
                        animateChangeImpl(info);
                    }
                    copiedChanges.clear();
                    changesList.remove(copiedChanges); // *2 Removed!
                }
            };

            if (removalsPending) {
                ViewCompat.postOnAnimationDelayed(
                        copiedChanges.get(0).oldHolder.itemView,
                        changer,
                        getRemoveDuration()
                );
            } else {
                changer.run();
            }
        }

        if (additionsPending) {
            final ArrayList<AdditionInfo> copiedAdditions = new ArrayList<>(pendingAdditions);
            additionsList.add(copiedAdditions); // *3 Added!
            pendingAdditions.clear();

            final long totalDelay;
            final @Nullable Boolean waitAdditionAfterFinishMoves;
            if (removalsPending || (movesPending != 0) || changesPending) {
                long removeDuration = removalsPending ? getRemoveDuration() : 0L;
                long moveDuration = ( waitAdditionAfterFinishMoves = (movesPending != 0 && waitAdditionAfterFinishMoves(movesPending)) ) ? getMoveDuration() : 0L;
                long changeDuration = changesPending ? getChangeDuration() : 0L;

                totalDelay = removeDuration + Math.max(moveDuration, changeDuration);
            } else {
                totalDelay = 0L;
                waitAdditionAfterFinishMoves = null;
            }

            Runnable adder = new Runnable() {
                @Override
                public void run() {
                    for (AdditionInfo info : copiedAdditions) {
                        {
                            info.movesPending = movesPending;
                            info.waitAdditionAfterFinishMoves = waitAdditionAfterFinishMoves;
                        }
                        animateAddImpl(info);
                    }
                    copiedAdditions.clear();
                    additionsList.remove(copiedAdditions); // *3 Removed!
                }
            };

            if (totalDelay != 0L) {
                ViewCompat.postOnAnimationDelayed(
                        copiedAdditions.get(0).holder.itemView,
                        adder,
                        totalDelay
                );
            } else {
                adder.run();
            }
        }
    }

    @Override
    public boolean animateRemove(@NonNull RecyclerView.ViewHolder holder) {
        resetAnimation(holder);
        pendingRemovals.add(holder);
        return true;
    }

    protected void animateRemoveImpl(final @NonNull RecyclerView.ViewHolder holder) {
        final ViewPropertyAnimator animation = holder.itemView.animate();

        removeAnimations.add(holder);

        createRemoveAnimation(
                animation,
                getRemoveDuration(),
                interpolator,
                new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animator) {
                        dispatchRemoveStarting(holder);
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        animation.setListener(null);
                        resetToAfterAnimateVisual(holder);
                        dispatchRemoveFinished(holder);
                        removeAnimations.remove(holder);
                        dispatchFinishedWhenDone();
                    }
                }
        ).start();
    }

    @Override
    public boolean animateAdd(@NonNull RecyclerView.ViewHolder holder) {
        resetAnimation(holder);
        initBeforeAdditionAnimate(holder);
        pendingAdditions.add(new AdditionInfo(holder));
        return true;
    }

    protected void animateAddImpl(@NonNull final AdditionInfo info) {
        addAnimations.add(info.holder);

        final ViewPropertyAnimator animation = info.holder.itemView.animate();

        createAddAnimation(
                info,
                animation,
                getAddDuration(),
                interpolator,
                new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animator) {
                        dispatchAddStarting(info.holder);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        resetToAfterAnimateVisual(info.holder);
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        animation.setListener(null);
                        dispatchAddFinished(info.holder);
                        addAnimations.remove(info.holder);
                        dispatchFinishedWhenDone();
                    }
                }
        ).start();
    }

    @Override
    public boolean animateMove(
            @NonNull RecyclerView.ViewHolder holder,
            int fromX,
            int fromY,
            int toX,
            int toY
    ) {
        final View target = holder.itemView;

        float computedFromX, computedFromY;
        {
            computedFromX = fromX + target.getTranslationX();
            computedFromY = fromY + target.getTranslationY();
        }

        resetAnimation(holder);

        float deltaX, deltaY;
        {
            deltaX = toX - computedFromX;
            deltaY = toY - computedFromY;
        }

        if (deltaX != 0f) {
            target.setTranslationX(deltaX * -1f);
        }

        if (deltaY != 0f) {
            target.setTranslationY(deltaY * -1f);
        }

        pendingMoves.add(
                new MoveInfo.Builder()
                        .holder(holder)
                        .fromLeft(computedFromX)
                        .fromTop(computedFromY)
                        .toLeft(toX)
                        .toTop(toY)
                        .build()
        );

        return true;
    }

    protected void animateMoveImpl(
            @NonNull final RecyclerView.ViewHolder holder,
            float fromX,
            float fromY,
            float toX,
            float toY
    ) {
        final float deltaX, deltaY;
        {
            deltaX = toX - fromX;
            deltaY = toY - fromY;
        }

        final View target = holder.itemView;

        if (deltaX != 0f) {
            animateToAfterMovedPositionX(target.animate());
        }

        if (deltaY != 0f) {
            animateToAfterMovedPositionY(target.animate());
        }

        moveAnimations.add(holder);

        final ViewPropertyAnimator animation = target.animate();

        createMoveAnimation(
                animation,
                getMoveDuration(),
                interpolator,
                new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animator) {
                        dispatchMoveStarting(holder);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        if (deltaX != 0f) {
                            resetToAfterAnimatePositionX(holder);
                        }
                        if (deltaY != 0f) {
                            resetToAfterAnimatePositionY(holder);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        animation.setListener(null);
                        dispatchMoveFinished(holder);
                        moveAnimations.remove(holder);
                        dispatchFinishedWhenDone();
                    }
                }
        ).start();
    }

    @Override
    public boolean animateChange(
            @NonNull RecyclerView.ViewHolder oldHolder,
            @NonNull RecyclerView.ViewHolder newHolder,
            int fromLeft,
            int fromTop,
            int toLeft,
            int toTop
    ) {
        if (oldHolder == newHolder) {
            return animateMove(oldHolder, fromLeft, fromTop, toLeft, toTop);
        }

        final float prevTranslationX, prevTranslationY;
        {
            prevTranslationX = oldHolder.itemView.getTranslationX();
            prevTranslationY = oldHolder.itemView.getTranslationY();
        }
        final float prevAlpha = oldHolder.itemView.getAlpha();

        resetAnimation(oldHolder);

        final float deltaX, deltaY;
        {
            deltaX = toLeft - fromLeft - prevTranslationX;
            deltaY = toTop - fromTop - prevTranslationY;
        }

        {
            oldHolder.itemView.setTranslationX(prevTranslationX);
            oldHolder.itemView.setTranslationY(prevTranslationY);
            oldHolder.itemView.setAlpha(prevAlpha);
        }

        resetAnimation(newHolder);
        {
            newHolder.itemView.setTranslationX(deltaX * -1);
            newHolder.itemView.setTranslationY(deltaY * -1);
            initBeforeChangeAnimate(newHolder);
        }

        {
            ChangeInfo info = new ChangeInfo.Builder()
                    .fromLeft(fromLeft)
                    .fromTop(fromTop)
                    .toLeft(toLeft)
                    .toTop(toTop)
                    .build();
            info.oldHolder = oldHolder;
            info.newHolder = newHolder;

            pendingChanges.add(info);
        }

        return true;
    }

    protected void animateChangeImpl(@NonNull ChangeInfo changeInfo) {
        if (changeInfo.oldHolder != null) {
            final @NonNull RecyclerView.ViewHolder target = changeInfo.oldHolder;

            final ViewPropertyAnimator animation = target.itemView.animate();

            changeAnimations.add(target);

            animation.translationX(changeInfo.toLeft - changeInfo.fromLeft);
            animation.translationY(changeInfo.toTop - changeInfo.fromTop);

            createChangeOldAnimation(
                    animation,
                    getChangeDuration(),
                    interpolator,
                    new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationStart(Animator animator) {
                            dispatchChangeStarting(target, true);
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            animation.setListener(null);
                            resetToAfterAnimateVisual(target);
                            resetToAfterAnimatePositionX(target);
                            resetToAfterAnimatePositionY(target);
                            dispatchChangeFinished(target, true);
                            changeAnimations.remove(target);
                            dispatchFinishedWhenDone();
                        }

                    }
            ).start();
        }

        if (changeInfo.newHolder != null) {
            final @NonNull RecyclerView.ViewHolder target = changeInfo.newHolder;

            final ViewPropertyAnimator animation = target.itemView.animate();

            changeAnimations.add(target);

            createChangeNewAnimation(
                    animation,
                    getChangeDuration(),
                    interpolator,
                    new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationStart(Animator animator) {
                            dispatchChangeStarting(target, false);
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            animation.setListener(null);
                            resetToAfterAnimateVisual(target);
                            resetToAfterAnimatePositionX(target);
                            resetToAfterAnimatePositionY(target);
                            dispatchChangeFinished(target, false);
                            changeAnimations.remove(target);
                            dispatchFinishedWhenDone();
                        }
                    }
            ).start();
        }
    }

    protected void endChangeAnimation(
            @NonNull List<ChangeInfo> changesRef,
            @NonNull RecyclerView.ViewHolder item
    ) {
        for (int i = changesRef.size() - 1; i >= 0; i--) {
            final ChangeInfo target = changesRef.get(i);
            if (endChangeAnimationIfNecessary(target, item)) {
                if (target.oldHolder == null && target.newHolder == null) {
                    changesRef.remove(target);
                }
            }
        }
    }

    protected void endChangeAnimationIfNecessary(@NonNull ChangeInfo change) {
        if (change.oldHolder != null) {
            endChangeAnimationIfNecessary(change, change.oldHolder);
        }
        if (change.newHolder != null) {
            endChangeAnimationIfNecessary(change, change.newHolder);
        }
    }

    protected boolean endChangeAnimationIfNecessary(@NonNull ChangeInfo change, @NonNull RecyclerView.ViewHolder item) {
        boolean oldItem = false;

        if (change.newHolder == item) {
            change.newHolder = null;
        } else if (change.oldHolder == item) {
            change.oldHolder = null;
            oldItem = true;
        } else {
            return false;
        }

        resetToAfterAnimateVisual(item);
        resetToAfterAnimatePositionX(item);
        resetToAfterAnimatePositionY(item);

        dispatchChangeFinished(item, oldItem);

        return true;
    }

    @Override
    public void endAnimation(@NonNull RecyclerView.ViewHolder item) {
        item.itemView.animate().cancel();

        for (int i = pendingMoves.size() - 1;i >= 0;i--) {
            final MoveInfo target = pendingMoves.get(i);
            if (target.holder == item) {
                resetToAfterAnimatePositionX(item);
                resetToAfterAnimatePositionY(item);
                dispatchMoveFinished(item);
                pendingMoves.remove(i);
            }
        }

        endChangeAnimation(pendingChanges, item);

        if (pendingRemovals.remove(item)) {
            resetToAfterAnimateVisual(item);
            dispatchRemoveFinished(item);
        }

        for (int i = pendingAdditions.size() - 1; i >= 0; i--) {
            final AdditionInfo target = pendingAdditions.get(i);
            if (target.holder == item) {
                pendingAdditions.remove(i);
                resetToAfterAnimateVisual(item);
                dispatchAddFinished(item);
                break;
            }
        }

        for (int i = changesList.size() - 1; i >= 0; i--) {
            final List<ChangeInfo> target = changesList.get(i);
            endChangeAnimation(target, item);
            if (target.isEmpty()) {
                changesList.remove(i);
            }
        }

        for (int i = movesList.size() - 1; i >= 0; i--) {
            final List<MoveInfo> target = movesList.get(i);

            for (int inner = target.size() - 1; inner >= 0; inner--) {
                final MoveInfo innerTarget = target.get(inner);

                if (innerTarget.holder == item) {
                    resetToAfterAnimatePositionX(item);
                    resetToAfterAnimatePositionY(item);
                    dispatchMoveFinished(item);
                    target.remove(inner); // IMPORTANT!
                    if (target.isEmpty()) {
                        movesList.remove(i); // IMPORTANT!
                    }
                    break;
                }
            }
        }

        for (int i = additionsList.size() - 1; i >= 0; i--) {
            final List<AdditionInfo> target = additionsList.get(i);
            for (int inner = target.size() - 1; inner >= 0; inner--) {
                final AdditionInfo innerTarget = target.get(inner);
                if (innerTarget.holder == item) {
                    target.remove(inner);
                    resetToAfterAnimateVisual(item);
                    dispatchAddFinished(item);
                    if (target.isEmpty()) {
                        additionsList.remove(i);
                    }
                }
            }
        }

        if (removeAnimations.remove(item) || addAnimations.remove(item) || changeAnimations.remove(item) || moveAnimations.remove(item)) {
            throw new AbsItemAnimatorIllegalStateException();
        }

        dispatchFinishedWhenDone();
    }

    protected void resetAnimation(@NonNull RecyclerView.ViewHolder holder) {
        endAnimation(holder);
    }

    @Override
    public boolean isRunning() {
        return !pendingAdditions.isEmpty()
                || !pendingChanges.isEmpty()
                || !pendingMoves.isEmpty()
                || !pendingRemovals.isEmpty()
                || !moveAnimations.isEmpty()
                || !removeAnimations.isEmpty()
                || !addAnimations.isEmpty()
                || !changeAnimations.isEmpty()
                || !movesList.isEmpty()
                || !additionsList.isEmpty()
                || !changesList.isEmpty();
    }

    protected void dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
        }
    }

    @Override
    public void endAnimations() {
        for (int i = pendingMoves.size() - 1; i >= 0; i--) {
            final MoveInfo target = pendingMoves.get(i);

            resetToAfterAnimatePositionX(target.holder);
            resetToAfterAnimatePositionY(target.holder);

            dispatchMoveFinished(target.holder);
            pendingMoves.remove(i);
        }

        for (int i = pendingRemovals.size() - 1; i >= 0; i--) {
            final RecyclerView.ViewHolder target = pendingRemovals.get(i);

            dispatchRemoveFinished(target);
            pendingRemovals.remove(i);
        }

        for (int i = pendingAdditions.size() - 1; i >= 0; i--) {
            final AdditionInfo target = pendingAdditions.get(i);

            resetToAfterAnimateVisual(target.holder);
            dispatchAddFinished(target.holder);
            pendingAdditions.remove(i);
        }

        {
            for (int i = pendingChanges.size() - 1; i >= 0; i--) {
                endChangeAnimationIfNecessary(
                        pendingChanges.get(i)
                );
            }
            pendingChanges.clear();
        }

        if (!isRunning()) {
            return;
        }

        for (int i = movesList.size() - 1; i >= 0; i--) {
            final List<MoveInfo> target = movesList.get(i);

            for (int inner = target.size() - 1; inner >= 0; inner--) {
                final MoveInfo innerTarget = target.get(inner);

                resetToAfterAnimatePositionX(innerTarget.holder);
                resetToAfterAnimatePositionY(innerTarget.holder);

                dispatchMoveFinished(innerTarget.holder);
                target.remove(inner);

                if (target.isEmpty()) {
                    movesList.remove(target);
                }
            }
        }

        for (int i = additionsList.size() - 1; i >= 0; i--) {
            final List<AdditionInfo> target = additionsList.get(i);

            for (int inner = target.size() - 1; inner >= 0; inner--) {
                final AdditionInfo innerTarget = target.get(inner);

                resetToAfterAnimateVisual(innerTarget.holder);
                dispatchAddFinished(innerTarget.holder);
                target.remove(innerTarget);

                if (target.isEmpty()) {
                    additionsList.remove(target);
                }
            }
        }

        for (int i = changesList.size() - 1; i >= 0; i--) {
            final List<ChangeInfo> target = changesList.get(i);

            for (int inner = target.size() - 1; i >= 0; i--) {
                final ChangeInfo innerTarget = target.get(inner);

                endChangeAnimationIfNecessary(innerTarget);
                if (target.isEmpty()) {
                    changesList.remove(target);
                }
            }
        }

        cancelAll(removeAnimations);
        cancelAll(moveAnimations);
        cancelAll(addAnimations);
        cancelAll(changeAnimations);

        dispatchAnimationsFinished();
    }

    protected void cancelAll(@NonNull List<RecyclerView.ViewHolder> holdersRef) {
        for (int i = holdersRef.size() - 1; i >= 0; i--) {
            holdersRef.get(i).itemView.animate().cancel();
        }
    }

    public boolean canReuseUpdatedViewHolder(
            @NonNull RecyclerView.ViewHolder viewHolder,
            @NonNull List<Object> payloads
    ) {
        return !payloads.isEmpty() || super.canReuseUpdatedViewHolder(viewHolder, payloads);
    }
    
    protected abstract void resetToAfterAnimatePositionX(@NonNull RecyclerView.ViewHolder holder);
    protected abstract void resetToAfterAnimatePositionY(@NonNull RecyclerView.ViewHolder holder);
    protected abstract void resetToAfterAnimateVisual(@NonNull RecyclerView.ViewHolder holder);
    
    protected abstract boolean waitAdditionAfterFinishMoves(int pendingMoves);
    
    protected abstract void initBeforeAdditionAnimate(@NonNull RecyclerView.ViewHolder holder);
    protected abstract void initBeforeChangeAnimate(@NonNull RecyclerView.ViewHolder holder);
    
    @NonNull
    protected abstract ViewPropertyAnimator animateToAfterMovedPositionX(@NonNull ViewPropertyAnimator animator);
    
    @NonNull
    protected abstract ViewPropertyAnimator animateToAfterMovedPositionY(@NonNull ViewPropertyAnimator animator);

    @NonNull
    protected abstract ViewPropertyAnimator createAddAnimation(
            @NonNull AdditionInfo info,
            @NonNull ViewPropertyAnimator animator,
            long duration,
            @Nullable TimeInterpolator interpolator,
            @NonNull AnimatorListenerAdapter listener
    );

    @NonNull
    protected abstract ViewPropertyAnimator createMoveAnimation(
            @NonNull ViewPropertyAnimator animator,
            long duration,
            @Nullable TimeInterpolator interpolator,
            @NonNull AnimatorListenerAdapter listener
    );

    @NonNull
    protected abstract ViewPropertyAnimator createChangeOldAnimation(
            @NonNull ViewPropertyAnimator animator,
            long duration,
            @Nullable TimeInterpolator interpolator,
            @NonNull AnimatorListenerAdapter listener
    );

    @NonNull
    protected abstract ViewPropertyAnimator createChangeNewAnimation(
            @NonNull ViewPropertyAnimator animator,
            long duration,
            @Nullable TimeInterpolator interpolator,
            @NonNull AnimatorListenerAdapter listener
    );

    @NonNull
    protected abstract ViewPropertyAnimator createRemoveAnimation(
            @NonNull ViewPropertyAnimator animator,
            long duration,
            @Nullable TimeInterpolator interpolator,
            @NonNull AnimatorListenerAdapter listener
    );
    
}
