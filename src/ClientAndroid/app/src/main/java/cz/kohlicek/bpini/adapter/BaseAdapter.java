package cz.kohlicek.bpini.adapter;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Stará se o zobrazení loadingu při načítání dalších dat
 *
 * @param <E>
 */
public abstract class BaseAdapter<E> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_LOADING = 1;

    protected boolean loading = false;
    protected List<E> data;
    protected Context context;
    protected OnClickListener onClickListener;
    private E selected;
    private int selectedPosition;

    public BaseAdapter(Context context) {
        this.data = new ArrayList<>();
        this.context = context;
    }

    public void addAll(List<E> data) {
        if (data.size() > 0) {
            this.data.addAll(data);
        }
        this.loading = false;
        notifyDataSetChanged();
    }

    public E get(int position) {
        return data.get(position);
    }

    public void set(int index, E object) {
        data.set(index, object);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        data.remove(position);
        notifyDataSetChanged();
    }

    public void remove(E object) {
        data.remove(object);
        notifyDataSetChanged();
    }

    public E getSelected() {
        return selected;
    }

    public void setSelected(E selected) {
        this.selected = selected;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void clear() {
        data.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return position < data.size() ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        if (this.loading != loading) {
            this.loading = loading;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return loading ? data.size() + 1 : data.size();
    }

    public void setOnClickListener(OnClickListener l) {
        this.onClickListener = l;
    }

    public interface OnClickListener<E> {
        void onClick(View v, int position, E data);
    }

    protected class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}
