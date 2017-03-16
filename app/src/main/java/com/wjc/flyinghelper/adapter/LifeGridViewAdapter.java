package com.wjc.flyinghelper.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wjc.flyinghelper.R;

import java.util.ArrayList;

public class LifeGridViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> arrayList;

    public LifeGridViewAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_life_fragment, null);

            viewHolder = new ViewHolder();
            viewHolder.lifeCardIcon = (TextView) view.findViewById(R.id.lifeCardIcon);
            viewHolder.lifeCardName = (TextView) view.findViewById(R.id.lifeCardName);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");

        TextView lifeCardIcon = viewHolder.lifeCardIcon;
        lifeCardIcon.setTypeface(typeface);
        switch (i) {
            case 0 :
                lifeCardIcon.setText(context.getString(R.string.icon_sleep));
                break;
            case 1 :
                lifeCardIcon.setText(context.getString(R.string.icon_environment));
                break;
            case 2 :
                lifeCardIcon.setText(context.getString(R.string.icon_express));
                break;
            case 3 :
                lifeCardIcon.setText(context.getString(R.string.icon_mobile));
                break;
            case 4 :
                lifeCardIcon.setText(context.getString(R.string.icon_idcard));
                break;
            case 5 :
                lifeCardIcon.setText(context.getString(R.string.icon_person));
                break;
        }

        TextView lifeCardName = viewHolder.lifeCardName;
        lifeCardName.setText(arrayList.get(i));

        return view;
    }

    private class ViewHolder {
        TextView lifeCardIcon;
        TextView lifeCardName;
    }

}
