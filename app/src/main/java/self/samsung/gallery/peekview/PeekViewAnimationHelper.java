package self.samsung.gallery.peekview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Animation helper class used to aid the pop up animation.
 * <p>
 * Created by subin on 3/17/2017.
 */
class PeekViewAnimationHelper {
    private final View peekLayout;
    private final View peekView;
    private final Context context;

    /**
     * Constructor to initialize the helper class with
     *
     * @param context    calling context
     * @param peekLayout layout to animate
     * @param peekView   PeekView view
     */
    PeekViewAnimationHelper(Context context, View peekLayout, View peekView) {
        this.context = context;
        this.peekLayout = peekLayout;
        this.peekView = peekView;
    }

    /**
     * Should happen on peek
     * <p/>
     */
    void animatePeek(int duration) {
        peekView.setAlpha(1);
        ObjectAnimator animatorLayoutAlpha = ObjectAnimator.ofFloat(peekLayout, "alpha", 1);
        animatorLayoutAlpha.setInterpolator(new OvershootInterpolator(1.2f));
        animatorLayoutAlpha.setDuration(duration);
        ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(peekView, "scaleX", 1);
        animatorScaleX.setDuration(duration);
        ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(peekView, "scaleY", 1);
        animatorScaleY.setDuration(duration);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new OvershootInterpolator(2.0f));
        animatorSet.play(animatorScaleX).with(animatorScaleY);

        animatorSet.start();
        animatorLayoutAlpha.start();
    }

    /**
     * Should happen on hide.
     * <p/>
     * fades out the background
     */
    void animateHide(Animator.AnimatorListener animatorListener, int duration) {
        ObjectAnimator animatorLayoutAlpha = ObjectAnimator.ofFloat(peekLayout, "alpha", 0);
        animatorLayoutAlpha.setDuration(duration);
        animatorLayoutAlpha.addListener(animatorListener);
        animatorLayoutAlpha.setInterpolator(new DecelerateInterpolator(1.5f));

        animatorLayoutAlpha.start();
        animateReturn(duration);
    }

    /**
     * Should happen on hide
     * <p/>
     */
    private void animateReturn(int duration) {
        ObjectAnimator animatorTranslate;
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            animatorTranslate = ObjectAnimator.ofFloat(peekView, "translationY", 0);
        } else {
            animatorTranslate = ObjectAnimator.ofFloat(peekView, "translationX", 0);
        }
        ObjectAnimator animatorShrinkY = ObjectAnimator.ofFloat(peekView, "scaleY", 0.75f);
        ObjectAnimator animatorShrinkX = ObjectAnimator.ofFloat(peekView, "scaleX", 0.75f);
        animatorShrinkX.setInterpolator(new DecelerateInterpolator());
        animatorShrinkY.setInterpolator(new DecelerateInterpolator());
        animatorTranslate.setInterpolator(new DecelerateInterpolator());
        animatorShrinkX.setDuration(duration);
        animatorShrinkY.setDuration(duration);
        animatorTranslate.setDuration(duration);
        animatorShrinkX.start();
        animatorShrinkY.start();
        animatorTranslate.start();
    }
}
