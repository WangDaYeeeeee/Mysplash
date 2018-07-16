package com.wangdaye.mysplash.common.basic;

import android.os.Message;
import android.support.v7.widget.RecyclerView;

import com.wangdaye.mysplash.common.utils.widget.SafeHandler;

/**
 * Mysplash adapter.
 *
 * A RecyclerView.Adapter class that can update item data in asynchronous thread.
 * */

public abstract class AsynUpdateAdapter<VH extends RecyclerView.ViewHolder> extends FooterAdapter<VH>
        implements SafeHandler.HandlerContainer {

    private SafeHandler<AsynUpdateAdapter<VH>> handler;
    private boolean working;

    public AsynUpdateAdapter() {
        handler = new SafeHandler<>(this);
        working = false;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        working = true;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        handler.removeCallbacksAndMessages(null);
        working = false;
    }

    protected void sendUpdateItemMessage(int position) {
        if (working) {
            handler.obtainMessage(position).sendToTarget();
        }
    }

    @Override
    public void handleMessage(Message message) {
        notifyItemChanged(message.what);
    }
}
