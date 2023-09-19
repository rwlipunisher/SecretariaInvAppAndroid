package com.example.secretariainv;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.json.Json;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ContentFragment extends Fragment {

    String urlmeu = "anteshttps://www.googleapis.com/youtube/v3/search?key=AIzaSyCPZ-O-lAZK1MwgRw6Qqj5EXfgVu3RjTI8&channelId=UCrPJ3cINYk0gOLMHaxjzSOg&part=snippet,id&order=date&maxResults=10";
    String url = "https://www.googleapis.com/youtube/v3/search?key=AIzaSyCmiG9TLwz7rI1I80sadMFxZAgWjvJ4KWo&channelId=UCBKACr9PpV9IbX0d9ILZhFg&part=snippet,id&order=date&maxResults=10";

    ListView listView;
    ArrayList<VideoDetails> videoDetailsArrayList;
    AdpterVideosList adpterVideosList;
    TextView textView;
    Button buttonRefresh;
    WebView myWebView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_content, container, false);

        listView  = view.findViewById(R.id.listviweyoutube);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), listView.getOnItemClickListener().toString(), Toast.LENGTH_LONG).show();
            }
        });

        videoDetailsArrayList = new ArrayList<>();
        adpterVideosList = new AdpterVideosList(getActivity(), videoDetailsArrayList, view);
        displayVideos(view, textView, listView, adpterVideosList);

        String myYouTubeVideoUrl = "https://www.youtube.com/embed/UQibT6sSV6E";

        String dataUrl =
                "<html>" +
                        "<body>" +
                        "<iframe width=\"100%\" height=\"100%\" src=\""+myYouTubeVideoUrl+"\" frameborder=\"0\" allowfullscreen/>" +
                        "</body>" +
                        "</html>";

        myWebView = view.findViewById(R.id.webView);

        WebSettings webSettings = myWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        myWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebChromeClient( new MyChrome());
        myWebView.setWebViewClient( new WebViewClient());
        myWebView.loadData(dataUrl, "text/html", "utf-8");
        buttonRefresh = view.findViewById(R.id.buttonAtualizarYoutube);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSharedPreferences("youtubelist", Context.MODE_PRIVATE).edit().putString("atualizado", "no").apply();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_1, new ContentFragment());
                transaction.commit();
            }
        });

        return view;
    }

    private class MyChrome extends WebChromeClient
    {
        View fullscreen = null;

        @Override
        public void onHideCustomView()
        {
            fullscreen.setVisibility(View.GONE);
            myWebView.setVisibility(View.VISIBLE);
        }
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback)
        {
            myWebView.setVisibility(View.GONE);

            if(fullscreen != null)
            {
                ((FrameLayout)getActivity().getWindow().getDecorView()).removeView(fullscreen);
            }

            fullscreen = view;
            ((FrameLayout)getActivity().getWindow().getDecorView()).addView(fullscreen, new FrameLayout.LayoutParams(-1, -1));
            fullscreen.setVisibility(View.VISIBLE);
        }
    }

    private void displayVideos(View view, TextView textView, ListView listView, AdpterVideosList adapterVideosList){
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());


        if( getActivity().getSharedPreferences("youtubelist", Context.MODE_PRIVATE).getString("atualizado", "no").equals("yes")){
            try {
                JSONObject jsonObject = new JSONObject(getActivity().getSharedPreferences("youtubelistall", Context.MODE_PRIVATE).getString("jsonString", ""));
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for( int i = 0; i < jsonArray.length();i++){

                    VideoDetails vd = new VideoDetails();
                    vd.setVideoId(jsonObject.getJSONArray("items").getJSONObject(i).getJSONObject("id").getString("videoId"));
                    vd.setTitle(jsonObject.getJSONArray("items").getJSONObject(i).getJSONObject("snippet").getString("title"));
                    vd.setDescription(jsonObject.getJSONArray("items").getJSONObject(i).getJSONObject("snippet").getString("description"));
                    vd.setUrl(jsonObject.getJSONArray("items").getJSONObject(i).getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url"));

                    videoDetailsArrayList.add(vd);
                    listView.setAdapter(adapterVideosList);
                    adpterVideosList.notifyDataSetChanged();
                }
            } catch (JSONException e){
                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        else {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("items");

                        getActivity().getSharedPreferences("youtubelistall", Context.MODE_PRIVATE).edit().putString("jsonString", response).apply();
                        getActivity().getSharedPreferences("youtubelist", Context.MODE_PRIVATE).edit().putString("atualizado", "yes").apply();

                        for( int i = 0; i < jsonArray.length();i++){

                            VideoDetails vd = new VideoDetails();
                            vd.setVideoId(jsonObject.getJSONArray("items").getJSONObject(i).getJSONObject("id").getString("videoId"));
                            vd.setTitle(jsonObject.getJSONArray("items").getJSONObject(i).getJSONObject("snippet").getString("title"));
                            vd.setDescription(jsonObject.getJSONArray("items").getJSONObject(i).getJSONObject("snippet").getString("description"));
                            vd.setUrl(jsonObject.getJSONArray("items").getJSONObject(i).getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url"));

                            videoDetailsArrayList.add(vd);
                            listView.setAdapter(adapterVideosList);
                            adpterVideosList.notifyDataSetChanged();
                        }
                        getActivity().getSharedPreferences("youtubelist", Context.MODE_PRIVATE).edit().putString("atualizado", "yes").apply();

                    } catch (JSONException e){
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar.make(getView(), error.getMessage(), Snackbar.LENGTH_LONG)
                            .show();
                }
            });

            requestQueue.add(stringRequest);

        }
    }

    public class VideoDetails{

        public String videoId, title, description, url;

        public VideoDetails(String videoId, String title, String description, String url) {
            this.videoId = videoId;
            this.title = title;
            this.description = description;
            this.url = url;
        }

        public VideoDetails(){}

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
    public class AdpterVideosList extends BaseAdapter {

        Activity activity;
        ArrayList<VideoDetails> videoDetailsArrayList;
        LayoutInflater inflater;
        View theView;
        public AdpterVideosList (Activity activity, ArrayList<VideoDetails> videoDetailsArrayList, View theView){
            this.activity = activity;
            this.videoDetailsArrayList = videoDetailsArrayList;
            this.theView = theView;

        }
        @Override
        public Object getItem(int position) {
            return this.videoDetailsArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(inflater == null ){
                inflater = this.activity.getLayoutInflater();
            }
            if( convertView ==null ){
                convertView = inflater.inflate(R.layout.layoutyoutubelist, null);
            }

            ImageView imageView = (ImageView)convertView.findViewById(R.id.imagethumb);
            TextView textView = (TextView) convertView.findViewById(R.id.titleyoutubevideo);

            VideoDetails videoDetails = (VideoDetails) this.videoDetailsArrayList.get(position);
            Picasso.get().load(videoDetails.getUrl()).into(imageView);
            textView.setText(videoDetails.getTitle());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String srcvideo = "https://www.youtube.com/embed/"+videoDetailsArrayList.get(position).videoId;
                    String dataUrl =
                            "<html>" +
                                    "<body>" +
                                    "<iframe width=\"100%\" height=\"100%\" src=\""+srcvideo+"\" frameborder=\"0\" allowfullscreen/>" +
                                    "</body>" +
                                    "</html>";
                    WebView webView = theView.findViewById(R.id.webView);
                    webView.loadData(dataUrl, "text/html", "utf-8");
                }
            });

            return convertView;
        }

        @Override
        public int getCount() {
            return this.videoDetailsArrayList.size();
        }
    }
}