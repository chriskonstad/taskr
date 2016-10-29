package com.taskr.client;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created by guillaumelam34 on 10/29/2016.
 */

public class RequestsFragment extends ListFragment {
    private static final String TAG = "RequestsFragment";

    @BindString(R.string.nearby_requests) String mTitle;

    public RequestsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        View rootView = inflater.inflate(R.layout.requests_list, container, false);
        ButterKnife.bind(this, rootView);

        getActivity().setTitle(mTitle);

        ArrayList<Integer> request_ids = new ArrayList<Integer>();
        ArrayList<String> request_text = new ArrayList<String>();

        String requestsStr = getArguments().getString("requests");
        if(!requestsStr.isEmpty()){
            try {
                JSONArray reqArr = new JSONArray(requestsStr);
                for(int x = 0; x < reqArr.length(); x++){
                    JSONObject reqObj = reqArr.getJSONObject(x);
                    request_ids.add(reqObj.getInt("ID"));
                    request_text.add(reqObj.getString("Title"));
                }
            }catch(JSONException e){
                Log.i(TAG, "Error convert nearby request to JSON");
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(),android.R.layout.simple_list_item_1, request_text);
        setListAdapter(adapter);

        return rootView;
    }
}
