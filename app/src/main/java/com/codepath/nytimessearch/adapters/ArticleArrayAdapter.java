package com.codepath.nytimessearch.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.data.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by arunesh on 10/23/16.
 */

public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the data item for the current position.
        Article article = getItem(position);

        // check if a recycled view is being used, if not inflate.
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);

        }

        // find the imageview, clear out the existing image.
        ImageView imageView = (ImageView) convertView.findViewById(R.id.ivImage);
        imageView.setImageResource(0);  // clear out the image

        String url = article.getThumbNail();
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(getContext()).load(article.getThumbNail()).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.article);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.tvTitle);
        textView.setText(article.getHeadline());
        return convertView;
    }
}
