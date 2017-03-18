package self.samsung.gallery.peekview;

import android.animation.Animator;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.SystemClock;
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
 * Custom class written to handle the pop up view like interface as seen in popular apps like Instagram.
 *
 * Created by subin on 3/18/2017.
 */

public class PeekView {

    private Activity activity;
    private ViewGroup contentView;

    private int peekLayoutId = -1;
    private ViewGroup peekLayout;
    private View peekView;

    private int orientation;
    private int peekViewMargin;
    private PeekViewAnimationHelper peekViewAnimationHelper;
    private float[] peekViewOriginalPosition;

    private static final int PEEK_VIEW_MARGIN = 12;
    private static final long LONG_CLICK_DURATION = 200;
    private static final int ANIMATION_POP_DURATION = 250;
    private static final int ANIMATION_PEEK_DURATION = 275;

    private OnGeneralActionListener onGeneralActionListener;
    private ViewGroup parentViewGroup;

    public PeekView(Activity activity,int peekLayout,ViewGroup parentViewGroup){
        this.activity = activity;
        this.peekLayoutId = peekLayout;
        this.parentViewGroup = parentViewGroup;
        init();
    }

    private void init(){
        this.orientation = activity.getResources().getConfiguration().orientation;
        this.peekViewMargin = Util.convertDpToPx(activity.getApplicationContext(), PEEK_VIEW_MARGIN);
        initialize();
    }

    private void initialiseGestureListener(@NonNull View view, int position) {
        view.setOnTouchListener(new PeekViewTouchListener(position));
        // onTouchListener will not work correctly if the view doesn't have an
        // onClickListener set, hence adding one if none has been added.
        if(!view.hasOnClickListeners()){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    private void initialize() {
        LayoutInflater inflater = LayoutInflater.from(activity);
        contentView = (ViewGroup) activity.findViewById(android.R.id.content).getRootView();

        // Center onPeek view in the onPeek layout and add to the container view group
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

    private void initialiseViewTreeObserver() {
        peekView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initialisePeekViewOriginalPosition();
            }
        });
    }

    private void initialisePeekViewOriginalPosition() {
        peekViewOriginalPosition = new float[2];
        peekViewOriginalPosition[0] = (peekLayout.getWidth() / 2) - (peekView.getWidth() / 2);
        peekViewOriginalPosition[1] = (peekLayout.getHeight() / 2) - (peekView.getHeight() / 2) + peekViewMargin;
    }

    private void resetPeekView(){
        peekLayout.setVisibility(View.GONE);

        if (peekViewOriginalPosition != null) {
            peekView.setX(peekViewOriginalPosition[0]);
            peekView.setY(peekViewOriginalPosition[1]);
        }
        peekView.setScaleX(0.85f);
        peekView.setScaleY(0.85f);
    }

    private class PeekViewTouchListener implements View.OnTouchListener {

        private boolean peekShown;

        private Timer longHoldTimer;
        private Runnable longHoldRunnable;

        private int position;

        private PeekViewTouchListener(int position) {
            this.position = position;
            longHoldTimer = new Timer();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                peekShown = false;
                startTimer(v);
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                cancelPendingTimer(v);
            }

            if (peekShown){
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    pop(v, position);
                }
            }

            return peekShown;
        }

        private void startTimer(@NonNull final View view) {
            longHoldTimer = new Timer();
            longHoldTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    peekShown = true;
                    longHoldRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (peekShown) {
                                peek(view, position);
                                longHoldRunnable = null;
                            }
                        }
                    };
                    activity.runOnUiThread(longHoldRunnable);
                }
            }, LONG_CLICK_DURATION);
        }

        private void cancelPendingTimer(@NonNull final View view) {
            longHoldTimer.cancel();
            if (longHoldRunnable != null) {
                longHoldRunnable = new Runnable() {
                    @Override
                    public void run() {
                        peekShown = false;
                        pop(view, position);
                        longHoldRunnable = null;
                    }
                };
                activity.runOnUiThread(longHoldRunnable);
            }
        }
    }

    private void pop(@NonNull View longClickView, int index) {
        if (onGeneralActionListener != null)
            onGeneralActionListener.onPop(longClickView, index);

        peekViewAnimationHelper.animatePop(new Animator.AnimatorListener() {
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
        }, ANIMATION_POP_DURATION);
    }

    private void peek(@NonNull View longClickView, int index) {
        if (onGeneralActionListener != null)
            onGeneralActionListener.onPeek(longClickView, index);

        peekLayout.setVisibility(View.VISIBLE);

        cancelClick(longClickView);

        peekViewAnimationHelper.animatePeek(ANIMATION_PEEK_DURATION);

        if (parentViewGroup != null)
            parentViewGroup.requestDisallowInterceptTouchEvent(true);
    }

    private void cancelClick(@NonNull View longClickView) {
        MotionEvent e = MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_CANCEL,
                0, 0, 0);
        longClickView.onTouchEvent(e);
        e.recycle();
    }

    public View getPeekView() {
        return peekView;
    }

    public void addLongClickView(@NonNull View view, int position) {
        initialiseGestureListener(view, position);
    }

    public interface OnGeneralActionListener {
        void onPeek(View longClickView, int position);

        void onPop(View longClickView, int position);
    }

    public void setOnGeneralActionListener(@Nullable OnGeneralActionListener onGeneralActionListener) {
        this.onGeneralActionListener = onGeneralActionListener;
    }
}