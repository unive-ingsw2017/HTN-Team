package com.greenteadev.unive.clair.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.greenteadev.unive.clair.R;

import static android.widget.RelativeLayout.CENTER_IN_PARENT;

public class ProgressFrameLayout extends FrameLayout {

    private static final String TAG_LOADING = "ProgressActivity.TAG_LOADING";
    private static final String TAG_EMPTY = "ProgressActivity.TAG_EMPTY";
    private static final String TAG_ERROR = "ProgressActivity.TAG_ERROR";

    public enum Status {CONTENT, LOADING, EMPTY, ERROR};

    LayoutInflater inflater;
    View view;
    FrameLayout.LayoutParams layoutParams;
    Drawable currentBackground;

    List<View> contentViews = new ArrayList<>();

    FrameLayout loadingStateFrameLayout;
    ProgressBar loadingStateProgressBar;

    FrameLayout emptyStateFrameLayout;
    ImageView emptyStateImageView;
    TextView emptyStateTitleTextView;
    TextView emptyStateContentTextView;

    FrameLayout errorStateFrameLayout;
    ImageView errorStateImageView;
    TextView errorStateTitleTextView;
    TextView errorStateContentTextView;
    Button errorStateButton;

    int loadingStateProgressBarWidth;
    int loadingStateProgressBarHeight;
    int loadingStateProgressBarColor;
    int loadingStateBackgroundColor;

    int emptyStateImageWidth;
    int emptyStateImageHeight;
    int emptyStateTitleTextSize;
    int emptyStateContentTextSize;
    int emptyStateTitleTextColor;
    int emptyStateContentTextColor;
    int emptyStateBackgroundColor;

    int errorStateImageWidth;
    int errorStateImageHeight;
    int errorStateTitleTextSize;
    int errorStateContentTextSize;
    int errorStateTitleTextColor;
    int errorStateContentTextColor;
    int errorStateButtonTextColor;
    int errorStateButtonBackgroundColor;
    int errorStateBackgroundColor;

    @State
    Status status = Status.CONTENT;

    public ProgressFrameLayout(Context context) {
        super(context);
    }

    public ProgressFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ProgressFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ProgressActivity);

        //Loading status attrs
        loadingStateProgressBarWidth =
                typedArray.getDimensionPixelSize(R.styleable.ProgressActivity_loadingProgressBarWidth, 108);

        loadingStateProgressBarHeight =
                typedArray.getDimensionPixelSize(R.styleable.ProgressActivity_loadingProgressBarHeight, 108);

        loadingStateProgressBarColor =
                typedArray.getColor(R.styleable.ProgressActivity_loadingProgressBarColor, Color.RED);

        loadingStateBackgroundColor =
                typedArray.getColor(R.styleable.ProgressActivity_loadingBackgroundColor, Color.TRANSPARENT);

        //Empty status attrs
        emptyStateImageWidth =
                typedArray.getDimensionPixelSize(R.styleable.ProgressActivity_emptyImageWidth, 308);

        emptyStateImageHeight =
                typedArray.getDimensionPixelSize(R.styleable.ProgressActivity_emptyImageHeight, 308);

        emptyStateTitleTextSize =
                typedArray.getDimensionPixelSize(R.styleable.ProgressActivity_emptyTitleTextSize, 15);

        emptyStateContentTextSize =
                typedArray.getDimensionPixelSize(R.styleable.ProgressActivity_emptyContentTextSize, 14);

        emptyStateTitleTextColor =
                typedArray.getColor(R.styleable.ProgressActivity_emptyTitleTextColor, Color.BLACK);

        emptyStateContentTextColor =
                typedArray.getColor(R.styleable.ProgressActivity_emptyContentTextColor, Color.BLACK);

        emptyStateBackgroundColor =
                typedArray.getColor(R.styleable.ProgressActivity_emptyBackgroundColor, Color.TRANSPARENT);

        //Error status attrs
        errorStateImageWidth =
                typedArray.getDimensionPixelSize(R.styleable.ProgressActivity_errorImageWidth, 308);

        errorStateImageHeight =
                typedArray.getDimensionPixelSize(R.styleable.ProgressActivity_errorImageHeight, 308);

        errorStateTitleTextSize =
                typedArray.getDimensionPixelSize(R.styleable.ProgressActivity_errorTitleTextSize, 15);

        errorStateContentTextSize =
                typedArray.getDimensionPixelSize(R.styleable.ProgressActivity_errorContentTextSize, 14);

        errorStateTitleTextColor =
                typedArray.getColor(R.styleable.ProgressActivity_errorTitleTextColor, Color.BLACK);

        errorStateContentTextColor =
                typedArray.getColor(R.styleable.ProgressActivity_errorContentTextColor, Color.BLACK);

        errorStateButtonTextColor =
                typedArray.getColor(R.styleable.ProgressActivity_errorButtonTextColor, Color.BLACK);

        errorStateButtonBackgroundColor =
                typedArray.getColor(R.styleable.ProgressActivity_errorButtonBackgroundColor, Color.WHITE);

        errorStateBackgroundColor =
                typedArray.getColor(R.styleable.ProgressActivity_errorBackgroundColor, Color.TRANSPARENT);

        typedArray.recycle();

        currentBackground = this.getBackground();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        if (child.getTag() == null || (!child.getTag().equals(TAG_LOADING) &&
                !child.getTag().equals(TAG_EMPTY) && !child.getTag().equals(TAG_ERROR))) {

            contentViews.add(child);
        }
    }

    /**
     * Hide all other states and show content
     */
    public void showContent() {
        switchState(Status.CONTENT, 0, null, null, null, null, Collections.<Integer>emptyList());
    }

    /**
     * Hide all other states and show content
     *
     * @param skipIds Ids of views not to show
     */
    public void showContent(List<Integer> skipIds) {
        switchState(Status.CONTENT, 0, null, null, null, null, skipIds);
    }

    /**
     * Hide content and show the progress bar
     */
    public void showLoading() {
        switchState(Status.LOADING, 0, null, null, null, null, Collections.<Integer>emptyList());
    }

    /**
     * Hide content and show the progress bar
     *
     * @param skipIds Ids of views to not hide
     */
    public void showLoading(List<Integer> skipIds) {
        switchState(Status.LOADING, 0, null, null, null, null, skipIds);
    }

    /**
     * Show empty view when there are not data to show
     *
     * @param emptyImageDrawable Drawable to show
     * @param emptyTextTitle     Title of the empty view to show
     * @param emptyTextContent   Content of the empty view to show
     */
    public void showEmpty(int emptyImageDrawable, String emptyTextTitle, String emptyTextContent) {
        switchState(Status.EMPTY, emptyImageDrawable, emptyTextTitle, emptyTextContent, null, null, Collections.<Integer>emptyList());
    }

    /**
     * Show empty view when there are not data to show
     *
     * @param emptyImageDrawable Drawable to show
     * @param emptyTextTitle     Title of the empty view to show
     * @param emptyTextContent   Content of the empty view to show
     */
    public void showEmpty(Drawable emptyImageDrawable, String emptyTextTitle, String emptyTextContent) {
        switchState(Status.EMPTY, emptyImageDrawable, emptyTextTitle, emptyTextContent, null, null, Collections.<Integer>emptyList());
    }

    /**
     * Show empty view when there are not data to show
     *
     * @param emptyImageDrawable Drawable to show
     * @param emptyTextTitle     Title of the empty view to show
     * @param emptyTextContent   Content of the empty view to show
     * @param skipIds            Ids of views to not hide
     */
    public void showEmpty(int emptyImageDrawable, String emptyTextTitle, String emptyTextContent, List<Integer> skipIds) {
        switchState(Status.EMPTY, emptyImageDrawable, emptyTextTitle, emptyTextContent, null, null, skipIds);
    }

    /**
     * Show empty view when there are not data to show
     *
     * @param emptyImageDrawable Drawable to show
     * @param emptyTextTitle     Title of the empty view to show
     * @param emptyTextContent   Content of the empty view to show
     * @param skipIds            Ids of views to not hide
     */
    public void showEmpty(Drawable emptyImageDrawable, String emptyTextTitle, String emptyTextContent, List<Integer> skipIds) {
        switchState(Status.EMPTY, emptyImageDrawable, emptyTextTitle, emptyTextContent, null, null, skipIds);
    }

    /**
     * Show error view with a button when something goes wrong and prompting the user to try again
     *
     * @param errorImageDrawable Drawable to show
     * @param errorTextTitle     Title of the error view to show
     * @param errorTextContent   Content of the error view to show
     * @param errorButtonText    Text on the error view button to show
     * @param onClickListener    Listener of the error view button
     */
    public void showError(int errorImageDrawable, String errorTextTitle, String errorTextContent, String errorButtonText, View.OnClickListener onClickListener) {
        switchState(Status.ERROR, errorImageDrawable, errorTextTitle, errorTextContent, errorButtonText, onClickListener, Collections.<Integer>emptyList());
    }

    /**
     * Show error view with a button when something goes wrong and prompting the user to try again
     *
     * @param errorImageDrawable Drawable to show
     * @param errorTextTitle     Title of the error view to show
     * @param errorTextContent   Content of the error view to show
     * @param errorButtonText    Text on the error view button to show
     * @param onClickListener    Listener of the error view button
     */
    public void showError(Drawable errorImageDrawable, String errorTextTitle, String errorTextContent, String errorButtonText, View.OnClickListener onClickListener) {
        switchState(Status.ERROR, errorImageDrawable, errorTextTitle, errorTextContent, errorButtonText, onClickListener, Collections.<Integer>emptyList());
    }


    /**
     * Show error view with a button when something goes wrong and prompting the user to try again
     *
     * @param errorImageDrawable Drawable to show
     * @param errorTextTitle     Title of the error view to show
     * @param errorTextContent   Content of the error view to show
     * @param errorButtonText    Text on the error view button to show
     * @param onClickListener    Listener of the error view button
     * @param skipIds            Ids of views to not hide
     */
    public void showError(int errorImageDrawable, String errorTextTitle, String errorTextContent, String errorButtonText, View.OnClickListener onClickListener, List<Integer> skipIds) {
        switchState(Status.ERROR, errorImageDrawable, errorTextTitle, errorTextContent, errorButtonText, onClickListener, skipIds);
    }

    /**
     * Show error view with a button when something goes wrong and prompting the user to try again
     *
     * @param errorImageDrawable Drawable to show
     * @param errorTextTitle     Title of the error view to show
     * @param errorTextContent   Content of the error view to show
     * @param errorButtonText    Text on the error view button to show
     * @param onClickListener    Listener of the error view button
     * @param skipIds            Ids of views to not hide
     */
    public void showError(Drawable errorImageDrawable, String errorTextTitle, String errorTextContent, String errorButtonText, View.OnClickListener onClickListener, List<Integer> skipIds) {
        switchState(Status.ERROR, errorImageDrawable, errorTextTitle, errorTextContent, errorButtonText, onClickListener, skipIds);
    }

    /**
     * Get which status is set
     *
     * @return Status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Check if content is shown
     *
     * @return boolean
     */
    public boolean isContent() {
        return status.equals(Status.CONTENT);
    }

    /**
     * Check if loading status is shown
     *
     * @return boolean
     */
    public boolean isLoading() {
        return status.equals(Status.LOADING);
    }

    /**
     * Check if empty status is shown
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return status.equals(Status.EMPTY);
    }

    /**
     * Check if error status is shown
     *
     * @return boolean
     */
    public boolean isError() {
        return status.equals(Status.ERROR);
    }

    private void switchState(Status status, Drawable drawable, String errorText, String errorTextContent,
                             String errorButtonText, View.OnClickListener onClickListener, List<Integer> skipIds) {
        this.status = status;

        switch (status) {
            case CONTENT:
                //Hide all status views to display content
                setContentState(skipIds);
                break;
            case LOADING:
                setLoadingState(skipIds);
                break;
            case EMPTY:
                setEmptyState(skipIds);

                emptyStateImageView.setImageDrawable(drawable);
                emptyStateTitleTextView.setText(errorText);
                emptyStateContentTextView.setText(errorTextContent);
                break;
            case ERROR:
                setErrorState(skipIds);

                errorStateImageView.setImageDrawable(drawable);
                errorStateTitleTextView.setText(errorText);
                errorStateContentTextView.setText(errorTextContent);
                errorStateButton.setText(errorButtonText);
                errorStateButton.setOnClickListener(onClickListener);
                break;
        }
    }

    private void switchState(Status status, int drawable, String errorText, String errorTextContent,
                             String errorButtonText, View.OnClickListener onClickListener, List<Integer> skipIds) {
        this.status = status;

        switch (status) {
            case CONTENT:
                //Hide all status views to display content
                setContentState(skipIds);
                break;
            case LOADING:
                setLoadingState(skipIds);
                break;
            case EMPTY:
                setEmptyState(skipIds);

                emptyStateImageView.setImageResource(drawable);
                emptyStateTitleTextView.setText(errorText);
                emptyStateContentTextView.setText(errorTextContent);
                break;
            case ERROR:
                setErrorState(skipIds);

                errorStateImageView.setImageResource(drawable);
                errorStateTitleTextView.setText(errorText);
                errorStateContentTextView.setText(errorTextContent);
                errorStateButton.setText(errorButtonText);
                errorStateButton.setOnClickListener(onClickListener);
                break;
        }
    }

    private void setLoadingView() {
        if (loadingStateFrameLayout == null) {
            view = inflater.inflate(R.layout.progress_frame_layout_loading_view, null);
            loadingStateFrameLayout = (FrameLayout) view.findViewById(R.id.frame_layout_progress);
            loadingStateFrameLayout.setTag(TAG_LOADING);

            // Setup ProgressBar
            loadingStateProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar_loading);
            loadingStateProgressBar.getLayoutParams().width = loadingStateProgressBarWidth;
            loadingStateProgressBar.getLayoutParams().height = loadingStateProgressBarHeight;
            loadingStateProgressBar.getIndeterminateDrawable()
                    .setColorFilter(loadingStateProgressBarColor, PorterDuff.Mode.SRC_IN);
            loadingStateProgressBar.requestLayout();

            //Set background color if not TRANSPARENT
            if (loadingStateBackgroundColor != Color.TRANSPARENT) {
                this.setBackgroundColor(loadingStateBackgroundColor);
            }

            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = CENTER_IN_PARENT;

            addView(loadingStateFrameLayout, layoutParams);
        } else {
            //Set background color if not TRANSPARENT
            if (loadingStateBackgroundColor != Color.TRANSPARENT) {
                this.setBackgroundColor(loadingStateBackgroundColor);
            }

            loadingStateFrameLayout.setVisibility(VISIBLE);
        }
    }

    private void setEmptyView() {
        if (emptyStateFrameLayout == null) {
            view = inflater.inflate(R.layout.progress_frame_layout_empty_view, null);
            emptyStateFrameLayout = (FrameLayout) view.findViewById(R.id.frame_layout_empty);
            emptyStateFrameLayout.setTag(TAG_EMPTY);

            emptyStateImageView = (ImageView) view.findViewById(R.id.image_icon);
            emptyStateTitleTextView = (TextView) view.findViewById(R.id.text_title);
            emptyStateContentTextView = (TextView) view.findViewById(R.id.text_content);

            //Set empty status image width and height
            emptyStateImageView.getLayoutParams().width = emptyStateImageWidth;
            emptyStateImageView.getLayoutParams().height = emptyStateImageHeight;
            emptyStateImageView.requestLayout();

            emptyStateTitleTextView.setTextSize(emptyStateTitleTextSize);
            emptyStateContentTextView.setTextSize(emptyStateContentTextSize);
            emptyStateTitleTextView.setTextColor(emptyStateTitleTextColor);
            emptyStateContentTextView.setTextColor(emptyStateContentTextColor);

            //Set background color if not TRANSPARENT
            if (emptyStateBackgroundColor != Color.TRANSPARENT) {
                this.setBackgroundColor(emptyStateBackgroundColor);
            }

            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = CENTER_IN_PARENT;

            addView(emptyStateFrameLayout, layoutParams);
        } else {
            //Set background color if not TRANSPARENT
            if (emptyStateBackgroundColor != Color.TRANSPARENT) {
                this.setBackgroundColor(emptyStateBackgroundColor);
            }

            emptyStateFrameLayout.setVisibility(VISIBLE);
        }
    }

    private void setErrorView() {
        if (errorStateFrameLayout == null) {
            view = inflater.inflate(R.layout.progress_frame_layout_error_view, null);
            errorStateFrameLayout = (FrameLayout) view.findViewById(R.id.frame_layout_error);
            errorStateFrameLayout.setTag(TAG_ERROR);

            errorStateImageView = (ImageView) view.findViewById(R.id.image_icon);
            errorStateTitleTextView = (TextView) view.findViewById(R.id.text_title);
            errorStateContentTextView = (TextView) view.findViewById(R.id.text_content);
            errorStateButton = (Button) view.findViewById(R.id.button_retry);

            //Set error status image width and height
            errorStateImageView.getLayoutParams().width = errorStateImageWidth;
            errorStateImageView.getLayoutParams().height = errorStateImageHeight;
            errorStateImageView.requestLayout();

            errorStateTitleTextView.setTextSize(errorStateTitleTextSize);
            errorStateContentTextView.setTextSize(errorStateContentTextSize);
            errorStateTitleTextView.setTextColor(errorStateTitleTextColor);
            errorStateContentTextView.setTextColor(errorStateContentTextColor);
            errorStateButton.setTextColor(errorStateButtonTextColor);
            errorStateButton.getBackground().setColorFilter(new LightingColorFilter(1, errorStateButtonBackgroundColor));

            //Set background color if not TRANSPARENT
            if (errorStateBackgroundColor != Color.TRANSPARENT) {
                this.setBackgroundColor(errorStateBackgroundColor);
            }

            layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = CENTER_IN_PARENT;

            addView(errorStateFrameLayout, layoutParams);
        } else {
            //Set background color if not TRANSPARENT
            if (errorStateBackgroundColor != Color.TRANSPARENT) {
                this.setBackgroundColor(errorStateBackgroundColor);
            }
            errorStateFrameLayout.setVisibility(VISIBLE);
        }
    }

    /**
     * Helpers
     */

    private void setContentState(List<Integer> skipIds) {
        hideLoadingView();
        hideEmptyView();
        hideErrorView();

        setContentVisibility(true, skipIds);
    }

    private void setLoadingState(List<Integer> skipIds) {
        hideEmptyView();
        hideErrorView();

        setLoadingView();
        setContentVisibility(false, skipIds);
    }

    private void setEmptyState(List<Integer> skipIds) {
        hideLoadingView();
        hideErrorView();

        setEmptyView();
        setContentVisibility(false, skipIds);
    }

    private void setErrorState(List<Integer> skipIds) {
        hideLoadingView();
        hideEmptyView();

        setErrorView();
        setContentVisibility(false, skipIds);
    }

    private void setContentVisibility(boolean visible, List<Integer> skipIds) {
        for (View v : contentViews) {
            if (!skipIds.contains(v.getId())) {
                v.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void hideLoadingView() {
        if (loadingStateFrameLayout != null) {
            loadingStateFrameLayout.setVisibility(GONE);

            //Restore the background color if not TRANSPARENT
            if (loadingStateBackgroundColor != Color.TRANSPARENT) {
                this.setBackgroundDrawable(currentBackground);
            }
        }
    }

    private void hideEmptyView() {
        if (emptyStateFrameLayout != null) {
            emptyStateFrameLayout.setVisibility(GONE);

            //Restore the background color if not TRANSPARENT
            if (emptyStateBackgroundColor != Color.TRANSPARENT) {
                this.setBackgroundDrawable(currentBackground);
            }
        }
    }

    private void hideErrorView() {
        if (errorStateFrameLayout != null) {
            errorStateFrameLayout.setVisibility(GONE);

            //Restore the background color if not TRANSPARENT
            if (errorStateBackgroundColor != Color.TRANSPARENT) {
                this.setBackgroundDrawable(currentBackground);
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return StateSaver.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(StateSaver.restoreInstanceState(this, state));
    }
}

