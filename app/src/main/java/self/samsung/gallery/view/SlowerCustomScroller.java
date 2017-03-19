package self.samsung.gallery.view;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Custom slower scroller for the pager view
 * <p>
 * Created by subin on 3/19/2017.
 */

class SlowerCustomScroller extends Scroller {

    private double mScrollFactor = 1;

    @SuppressWarnings("unused")
    public SlowerCustomScroller(Context context) {
        super(context);
    }

    SlowerCustomScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @SuppressWarnings("unused")
    public SlowerCustomScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    /**
     * Set the factor by which the duration will change
     */
    void setScrollDurationFactor(double scrollFactor) {
        mScrollFactor = scrollFactor;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, (int) (duration * mScrollFactor));
    }

}
