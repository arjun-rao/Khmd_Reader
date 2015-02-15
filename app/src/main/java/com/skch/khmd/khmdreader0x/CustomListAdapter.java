package com.skch.khmd.khmdreader0x;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.skch.khmd.khmdreader0x.asynctaask.ImageDownloaderTask;
import com.skch.khmd.khmdreader0x.model.FeedItem;

    public class CustomListAdapter extends BaseAdapter {
        private ArrayList listData;
        private LayoutInflater layoutInflater;
        private Context mContext;

        public CustomListAdapter(Context context, ArrayList listData) {
            this.listData = listData;
            layoutInflater = LayoutInflater.from(context);
            mContext = context;
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_row_layout, null);
                holder = new ViewHolder();
                holder.headlineView = (TextView) convertView.findViewById(R.id.title);
                holder.authorView = (TextView) convertView.findViewById(R.id.author);
                holder.imageView = (ImageView) convertView.findViewById(R.id.thumbImage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            FeedItem newsItem = (FeedItem) listData.get(position);
            holder.headlineView.setText(newsItem.getTitle());
            holder.authorView.setText(newsItem.getAuthor());

            if (holder.imageView != null) {
                new ImageDownloaderTask(holder.imageView).execute(newsItem.getAttachmentUrl());
            }

            return convertView;
        }

        static class ViewHolder {
            TextView headlineView;
            TextView authorView;
            ImageView imageView;
        }
    }