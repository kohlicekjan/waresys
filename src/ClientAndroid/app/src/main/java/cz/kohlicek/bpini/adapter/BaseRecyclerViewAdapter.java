package cz.kohlicek.bpini.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseRecyclerViewAdapter<E> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_LOADING = 1;

    protected boolean loading = false;
    protected List<E> data;
    protected Context context;
    protected OnClickListener onClickListener;

    public E selected;

    public BaseRecyclerViewAdapter(Context context) {
        this.data = new ArrayList<>();
        this.context = context;

    }

    public void addAll(List<E> data) {
        if (data.size() > 0) {
            this.data.addAll(data);

            //MOZNA DAM PRYC
            //HashSet<E> hashSet = new HashSet<E>();
            //hashSet.addAll(this.data);

//            Set set = new TreeSet<E>(new Comparator() {
//                @Override
//                public int compare(Object o1, Object o2) {
//                    if(((BasicModel)o1).getId().equalsIgnoreCase(((BasicModel)o1).getId())){
//                        return 0;
//                    }
//                    return 1;
//                }
//            });
//            set.addAll(data);


            //this.data.clear();
            //this.data.addAll(hashSet);

        }
        this.loading = false;
        notifyDataSetChanged();
    }

    public E get(int position) {
        return data.get(position);
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

    protected class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnClickListener<E> {
        void onClick(View v, int position, E data);
    }
}
