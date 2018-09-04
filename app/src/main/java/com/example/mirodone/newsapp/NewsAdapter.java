package com.example.mirodone.newsapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Activity context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_main, parent, false);
        }

        // Find the news at the given position in the list of News
        News currentNews = getItem(position);

        // find the TextView  in the activity xml layout with the id section
        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.list_item_section);
        sectionTextView.setText(currentNews.getNewsSection());

        // find the TextView  in the activity xml layout with the id title
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.list_item_webTitle);
        titleTextView.setText(currentNews.getNewsTitle());

        // find the TextView  in the activity xml layout with the id author
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.list_item_author);
        authorTextView.setText(currentNews.getNewsAuthor());


        TextView dateView = null;
        if (currentNews.getNewsDate() != null) {
            dateView = listItemView.findViewById(R.id.list_item_date);
            String formattedDate = formatDate(currentNews.getNewsDate());
            dateView.setText(formattedDate);
            dateView.setVisibility(View.VISIBLE);
        } else {
            dateView.setVisibility(View.GONE);
        }


        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy | h:mm a");
        return dateFormat.format(dateObject);
    }

}
