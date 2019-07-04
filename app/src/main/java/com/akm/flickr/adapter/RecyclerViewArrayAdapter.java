package com.akm.flickr.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akm.flickr.BR;
import com.akm.flickr.R;
import com.akm.flickr.model.FlickrPublicPhotosModel;
import com.google.gson.internal.LinkedTreeMap;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class RecyclerViewArrayAdapter<T>
        extends RecyclerView.Adapter<RecyclerViewArrayAdapter.MyViewHolder> {


    public List<T> getmObjects() {
        return mObjects;
    }

    private List<T> mObjects;
    private OnItemClickListener<T> onItemClickListener;
    private TextView emptyTextView;
    private int emptyViewText = R.string.app_name;
    private Fragment fragment;

    public void setEmptyTextView(TextView emptyTextView, @StringRes int emptyViewText) {
        this.emptyTextView = emptyTextView;
        this.emptyViewText = emptyViewText;
    }


    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public interface OnItemClickListener<T> {
        public void onItemClick(View view, int position, T object);
    }

    public RecyclerViewArrayAdapter(final List<T> objects) {
        this(objects, null);
    }

    public RecyclerViewArrayAdapter(final List<T> objects, OnItemClickListener onItemClickListener) {
        mObjects = objects;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), viewType, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.getBinding().setVariable(BR.item, getItem(position));
        holder.getBinding().setVariable(BR.itemClickListener, onItemClickListener);
        holder.getBinding().setVariable(BR.index, ( position + 1));
    }

    @Override
    public int getItemViewType(int position) {
        if (mObjects.get(position) instanceof FlickrPublicPhotosModel.Item) {
            return R.layout.row_images;
        } else {
            return 0;
        }
    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(final T object) {
        mObjects.add(object);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addAll(final List<T> objects) {
        int posStart = mObjects.size();
        mObjects.addAll(objects);
        notifyItemRangeInserted(posStart, getItemCount() - 1);
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        final int size = getItemCount();
        mObjects.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public int getItemCount() {
        if (emptyTextView != null) {
            if (mObjects.size() == 0) {
                emptyTextView.setVisibility(View.VISIBLE);
                emptyTextView.setText(emptyViewText);
            } else {
                emptyTextView.setVisibility(View.INVISIBLE);
            }
        }

        return mObjects.size();
    }


    public T getItem(final int position) {
        return mObjects.get(position);
    }

    public long getItemId(final int position) {
        return position;
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     * @return The position of the specified item.
     */
    public int getPosition(final T item) {
        return mObjects.indexOf(item);
    }

    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insert(final T object, int index) {
        mObjects.add(index, object);
        notifyItemInserted(index);
    }


    public void insert(final List<T> objects, int index) {
        int posStart = mObjects.size();
        mObjects.addAll(index, objects);
        notifyItemRangeInserted(posStart, getItemCount() - 1);
    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public void remove(T object) {
        final int position = getPosition(object);
        mObjects.remove(object);
        notifyItemRemoved(position);
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained in this adapter.
     */
    public void sort(Comparator<? super T> comparator) {
        Collections.sort(mObjects, comparator);
        notifyItemRangeChanged(0, getItemCount());
    }


    public void addItem(int position, T model) {
        mObjects.add(position, model);
        notifyItemInserted(position);
    }

    public void setItem(int position, T model) {
        mObjects.set(position, model);
        notifyItemChanged(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final T model = mObjects.remove(fromPosition);
        mObjects.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void update(List<T> models, boolean replaceExisting) {
        /*removal of objects creates problem with pagination logic, so this is skipped.*/
//        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models, replaceExisting);
        applyAndAnimateMovedItems(models);

        //Collections.sort(models);
    }

    private void applyAndAnimateRemovals(List<T> newModels) {
        for (T mObject : mObjects) {
            if (!newModels.contains(mObject)) {
                remove(mObject);
            }
        }
    }

    private void applyAndAnimateAdditions(List<T> newModels, boolean replaceExisting) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final T model = newModels.get(i);

            int index = mObjects.indexOf(model);
            if (!mObjects.contains(model)) {
                addItem(i, model);
            } else {
                if (replaceExisting) {
                    setItem(index, model);
                }
            }
        }
    }

    private void applyAndAnimateMovedItems(List<T> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final T model = newModels.get(toPosition);
            final int fromPosition = mObjects.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ViewDataBinding binding;

        public MyViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.executePendingBindings();
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }


}
