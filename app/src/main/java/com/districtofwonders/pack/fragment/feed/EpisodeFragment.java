package com.districtofwonders.pack.fragment.feed;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.districtofwonders.pack.R;
import com.districtofwonders.pack.util.DateUtils;
import com.districtofwonders.pack.util.DowDownloadManager;
import com.districtofwonders.pack.util.ViewUtils;

import java.util.HashMap;
import java.util.Map;

public class EpisodeFragment extends Fragment {

    public static final String ARG_PAGE_NUMBER = "ARG_PAGE_NUMBER";
    private static final String ARG_FEED_ITEM = "ARG_FEED_ITEM";

    private Map<String, String> mFeedItem;
    private int mPageNumber;

    public static Fragment newInstance(int pageNumber, Map<String, String> feedItem) {
        EpisodeFragment fragment = new EpisodeFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_PAGE_NUMBER, pageNumber);
        arguments.putSerializable(ARG_FEED_ITEM, (HashMap)feedItem);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        mPageNumber = arguments.getInt(ARG_PAGE_NUMBER);
        mFeedItem = (HashMap<String, String>) arguments.getSerializable(ARG_FEED_ITEM);

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.episode_fragment, null);

        // feed name
        ((TextView)root.findViewById(R.id.episodeFeedName)).setText(FeedsFragment.feeds[mPageNumber].title);
        // episode title
        String title = FeedsFragment.extractFeedItemTitle(mPageNumber, mFeedItem.get(FeedParser.Tags.TITLE));
        ((TextView)root.findViewById(R.id.episodeTitle)).setText(title);
        // date
        String pubDate = DateUtils.getPubDate(mFeedItem.get(FeedParser.Tags.PUB_DATE));
        ((TextView)root.findViewById(R.id.episodePubDate)).setText(pubDate);
        // duration
        String durationString = "";
        if (mFeedItem.get(FeedParser.Tags.DURATION) != null) {
            int duration = DateUtils.getMinutes(mFeedItem.get(FeedParser.Tags.DURATION));
            durationString = duration + " " + "min";
        }
        ((TextView)root.findViewById(R.id.episodeDuration)).setText(durationString);

        // buttons
        root.findViewById(R.id.episodePlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPlay();
            }
        });
        root.findViewById(R.id.episodeDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDownload();
            }
        });
        // show notes
        String content = mFeedItem.get(FeedParser.Tags.CONTENT_ENCODED);
        WebView webView = (WebView)root.findViewById(R.id.episodeShowNotes);
        webView.loadData(content, "text/html; charset=UTF-8", null);

        return root;
    }

    private void onClickDownload() {
        String url = mFeedItem.get(FeedParser.Keys.ENCLOSURE_URL);
        String title = mFeedItem.get(FeedParser.Tags.TITLE);
        DowDownloadManager.getInstance(getActivity()).enqueueRequest(getActivity(), mPageNumber, url, title);
    }

    private void onClickPlay() {
        ViewUtils.playAudio(getActivity(), mFeedItem.get(FeedParser.Keys.ENCLOSURE_URL));
    }
}