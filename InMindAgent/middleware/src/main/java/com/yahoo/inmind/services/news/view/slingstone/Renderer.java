package com.yahoo.inmind.services.news.view.slingstone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yahoo.inmind.services.news.control.reader.ReaderController;
import com.yahoo.inmind.services.news.model.vo.JsonItem;

public class Renderer
{
    protected LayoutInflater mInflater;
    protected int mResId;
    protected Context mCtx;

    public Renderer(Context ctx)
    {
        mCtx = ctx;
        mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResId = ReaderController.news_def_list_item_layout_id;
    }

    public boolean isCompatible(JsonItem item)
    {
        return true;
    }

    //Allow the renderer to determine whether the layout of the item should change due to the data newly arrived.
    public boolean isDirty(View v, JsonItem item)
    {
        return false;
    }

    public View inflate(View view, JsonItem item, ViewGroup vg)
    {
        if (mInflater != null)
            return mInflater.inflate(mResId, null);
        return null;
    }

    public void render(View v, JsonItem item, int idx)
    {
        ((TextView)v.findViewById(ReaderController.news_itemtext)).setText(item.getRawString());
    }

    public void freeView(View view) {

    }

    public Context getContext() {
        return mCtx;
    }

    public void setContext(Context mCtx) {
        this.mCtx = mCtx;
    }
}