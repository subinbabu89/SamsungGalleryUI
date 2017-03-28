package self.samsung.gallery.peekview;

import android.animation.Animator;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

import self.samsung.gallery.R;
import self.samsung.gallery.util.Util;

/**
 * Custom class written to handle the hide up view like interface as seen in popular apps like Instagram.
 * <p>
 * Created by subin on 3/18/2017.
 */
public class PeekView {

    private static final int PEEK_VIEW_MARGIN = 12;
    private static final int ANIMATION_HIDE_DURATION = 250;
    private static final int ANIMATION_PEEK_DURATION = 400;
    private static final long LONG_CLICK_DURATION = 200;

    private final Activity activity;
    private ViewGroup contentView;

    private int peekLayoutId = -1;
    private ViewGroup peekLayout;
    private View peekView;

    private int peekViewMargin;
    private PeekViewAnimationHelper peekViewAnimationHelper;
    private float[] peekViewOriginalPosition;

    private OnPeekActionListener onPeekActionListener;
    private final ViewGroup parentViewGroup;

    private OnSweepAcrossListener onSweepAcrossListener;

    private int intialTapLocationX = -1;
    private int childrenViewCount;

    /**
     * Constructor to initialize the peekView
     *
     * @param activity        calling activity
     * @param peekLayout      id for the peekview layout
     * @param parentViewGroup view to attach the peekview to
     */
    public PeekView(Activity activity, int peekLayout, ViewGroup parentViewGroup) {
        this.activity = activity;
        this.peekLayoutId = peekLayout;
        this.parentViewGroup = parentViewGroup;

        initialize();
    }

    /**
     * Method to initialize the components for the peek view
     */
    private void initialize() {
        int orientation = activity.getResources().getConfiguration().orientation;
        this.peekViewMargin = Util.convertDpToPx(activity.getApplicationContext(), PEEK_VIEW_MARGIN);

        LayoutInflater inflater = LayoutInflater.from(activity);
        contentView = (ViewGroup) activity.findViewById(android.R.id.content).getRootView();

        peekLayout = (FrameLayout) inflater.inflate(R.layout.peek_background, contentView, false);
        peekView = inflater.inflate(peekLayoutId, peekLayout, false);
        peekView.setId(R.id.peek_view);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) peekView.getLayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            layoutParams.topMargin = peekViewMargin;

        peekLayout.addView(peekView, layoutParams);
        contentView.addView(peekLayout);

        peekLayout.setVisibility(View.GONE);
        peekLayout.setAlpha(0);
        peekLayout.requestLayout();

        peekViewAnimationHelper = new PeekViewAnimationHelper(activity.getApplicationContext(), peekLayout, peekView);

        placeInFront();
        initialiseViewTreeObserver();
        resetPeekView();
    }

    /**
     * initialize Gesture Listeners on the peek view
     *
     * @param view     view to initialize the Gesture Listener to
     * @param position position of the clicked view
     */
    private void initialiseGestureListener(@NonNull View view, int position) {
        view.setOnTouchListener(new PeekViewTouchListener(position));
        // needed because touch listener doesn't work without on click listener
        if (!view.hasOnClickListeners()) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    /**
     * Method used to bring the peekview above the rest of the views
     */
    private void placeInFront() {
        if (Build.VERSION.SDK_INT >= 21) {
            peekLayout.setElevation(10f);
            peekView.setElevation(10f);
        } else {
            peekLayout.bringToFront();
            peekView.bringToFront();
            contentView.requestLayout();
            contentView.invalidate();
        }
    }

    /**
     * Setup Global Layout listener to notify on any view change
     */
    private void initialiseViewTreeObserver() {
        peekView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initialisePeekViewOriginalPosition();
            }
        });
    }

    /**
     * Initialize the PeekView to original default position
     */
    private void initialisePeekViewOriginalPosition() {
        peekViewOriginalPosition = new float[2];
        peekViewOriginalPosition[0] = (peekLayout.getWidth() / 2) - (peekView.getWidth() / 2);
        peekViewOriginalPosition[1] = (peekLayout.getHeight() / 2) - (peekView.getHeight() / 2) + peekViewMargin;
    }

    /**
     * Reset the peek View
     */
    private void resetPeekView() {
        peekLayout.setVisibility(View.GONE);

        if (peekViewOriginalPosition != null) {
            peekView.setX(peekViewOriginalPosition[0]);
            peekView.setY(peekViewOriginalPosition[1]);
        }
        peekView.setScaleX(0.85f);
        peekView.setScaleY(0.85f);
    }

    /**
     * Class used to handle touch events on the peekview
     */
    private class PeekViewTouchListener implements View.OnTouchListener {

        private boolean peekShown;
        private final int position;

        private Timer longHoldTimer;
        private Runnable longHoldRunnable;

        /**
         * set the position variable for the peek view touch listener
         *
         * @param position position to set
         */
        private PeekViewTouchListener(int position) {
            this.position = position;
            this.longHoldTimer = new Timer();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                peekShown = false;
                startTimer();
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                cancelPendingTimer();
            }

            if (peekShown) {
                handleTouch(event);
            }

            return peekShown;
        }

        /**
         * Start timer for long press delay
         */
        private void startTimer() {
            longHoldTimer = new Timer();
            longHoldTimer.schedule(new TimerTask() {
                public void run() {
                    peekShown = true;
                    longHoldRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (peekShown) {
                                peek(position);
                                longHoldRunnable = null;
                            }
                        }
                    };
                    activity.runOnUiThread(longHoldRunnable);
                }
            }, LONG_CLICK_DURATION);
        }

        /**
         * Cancel the long press delay timer
         */
        private void cancelPendingTimer() {
            longHoldTimer.cancel();
            if (longHoldRunnable != null) {
                longHoldRunnable = new Runnable() {
                    @Override
                    public void run() {
                        peekShown = false;
                        hide();
                        longHoldRunnable = null;
                    }
                };
                activity.runOnUiThread(this.longHoldRunnable);
            }
        }
    }

    /**
     * handle the other touch events on the peek view (dragging)
     *
     * @param event Motion Event to track touch events
     */
    private void handleTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            hide();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int movementFactor = (peekView.getWidth() / childrenViewCount) / 2;

            int downX = (int) event.getRawX();
            if (intialTapLocationX < 0) {
                intialTapLocationX = downX;
            }
            if (intialTapLocationX - downX > movementFactor) {
                onSweepAcrossListener.sweepLeft();
                intialTapLocationX = downX;
            } else if (downX - intialTapLocationX > movementFactor) {
                onSweepAcrossListener.sweepRight();
                intialTapLocationX = downX;
            }
        }
    }

    /**
     * Method used to hide the peek view
     */
    private void hide() {
        if (onPeekActionListener != null)
            onPeekActionListener.onHide();

        peekViewAnimationHelper.animateHide(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                resetPeekView();
                animation.cancel();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        }, ANIMATION_HIDE_DURATION);
    }

    /**
     * Method used to show the peek view
     *
     * @param index position of the folder in the list to show the peekview for
     */
    private void peek(int index) {
        if (onPeekActionListener != null)
            onPeekActionListener.onPeek(index);

        peekLayout.setVisibility(View.VISIBLE);

        peekViewAnimationHelper.animatePeek(ANIMATION_PEEK_DURATION);

        if (parentViewGroup != null)
            parentViewGroup.requestDisallowInterceptTouchEvent(true);
    }

    /**
     * Get the view for the Peek View
     *
     * @return view of PeekView
     */
    public View getPeekView() {
        return peekView;
    }

    /**
     * Method used to set the long click events on the Peek View
     *
     * @param view     view to set the ontouch listener for
     * @param position position for the folder in the list
     */
    public void addLongClickView(@NonNull View view, int position) {
        initialiseGestureListener(view, position);
    }

    /**
     * Method to set the count for childview
     *
     * @param childViewCount count to be set
     */
    public void setChildViewCount(int childViewCount) {
        this.childrenViewCount = childViewCount;
    }

    /**
     * Listener interface for events like peek and hide for the calling context
     */
    public interface OnPeekActionListener {

        /**
         * Callback for the peek method
         *
         * @param position position of the view to be peeked
         */
        void onPeek(int position);

        /**
         * Callback for the hide method
         */
        void onHide();
    }

    /**
     * Setter for the OnPeekActionListener object
     *
     * @param onPeekActionListener object to initialize
     */
    public void setOnPeekActionListener(@Nullable OnPeekActionListener onPeekActionListener) {
        this.onPeekActionListener = onPeekActionListener;
    }

    /**
     * Listener interface for the Sweep action for the calling context
     */
    public interface OnSweepAcrossListener {

        /**
         * Callback when left sweep is detected
         */
        void sweepLeft();

        /**
         * Callback when right sweep is detected
         */
        void sweepRight();
    }

    /**
     * Setter for the OnSweepAcrossListener object
     *
     * @param onSweepAcrossListener object to be set
     */
    public void setOnSweepAcrossListener(@Nullable OnSweepAcrossListener onSweepAcrossListener) {
        this.onSweepAcrossListener = onSweepAcrossListener;
    }
}
