package cz.kohlicek.bpini.ui.view;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * RecyclerView rozšířena o schovávání/zorazování popisku pro prazdný seznam
 */
public class EmptyRecyclerView extends RecyclerView {
    private View mEmptyView;
    private int visibility;


    final private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    public EmptyRecyclerView(Context context) {
        super(context);
        setVisibility(super.getVisibility());
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setVisibility(super.getVisibility());
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setVisibility(super.getVisibility());
    }

    public void checkIfEmpty() {
        if (visibility == VISIBLE && mEmptyView != null) {
            boolean emptyViewVisible = (getAdapter() != null && getAdapter().getItemCount() == 0);
            mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            super.setVisibility(emptyViewVisible ? GONE : VISIBLE);
        } else {
            super.setVisibility(visibility);
            if (mEmptyView != null)
                mEmptyView.setVisibility(visibility);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        checkIfEmpty();
    }

    public void setEmptyView(View emptyView) {
        if (mEmptyView != null)
            mEmptyView.setVisibility(GONE);
        this.mEmptyView = emptyView;
        checkIfEmpty();
    }

    @Override
    public void setVisibility(int visibility) {
        this.visibility = visibility;
        checkIfEmpty();
    }
}

