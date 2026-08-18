package com.example.communityproject.UserCheck;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.communityproject.R;
import com.example.communityproject.SessionManager;
import com.example.communityproject.UrlSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link blockadeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class blockadeFragment extends Fragment {

    String m_id,c_id;
    private SessionManager sessionManager;
    private String URL_USERCHECKDATA ;
    UrlSetting urlSetting;
    private RecyclerView recyclerView;
    private View view;
    private List<UsercheckCardViewData> list_data;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static UserAuthorityAdapter userAuthorityAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public blockadeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment blockadeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static blockadeFragment newInstance(String param1, String param2) {
        blockadeFragment fragment = new blockadeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_blockade, container, false);
        sessionManager = new SessionManager(getActivity());
        sessionManager.checkLogin();
        HashMap<String, String> sessionUserData = sessionManager.getUserDetail();
        m_id = sessionUserData.get(sessionManager.USERID);
        c_id = sessionUserData.get(sessionManager.C_ID);

        swipeRefreshLayout = view.findViewById(R.id.gank_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list_data.clear();
                FirstLoadPostData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        list_data = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.userCheckRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        if(list_data.isEmpty()){
            FirstLoadPostData();
        }

        return view;
    }

    private void FirstLoadPostData(){

        JSONObject datas = new JSONObject();
        try{
            datas.put("c_id",c_id);
            datas.put("m_id",m_id);
            datas.put("userType","1");
        }catch (JSONException e){
            e.printStackTrace();
        }
        urlSetting = new UrlSetting(getContext());
        URL_USERCHECKDATA = urlSetting.getUrl()+"user/getUser";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_USERCHECKDATA,datas, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String UserId = jsonObject.getString("m_id");
                        String UserName = jsonObject.getString("m_name");
                        String UserImage = jsonObject.getString("image");
                        String authorityName = jsonObject.getString("a_name");
                        UsercheckCardViewData usercheckCardViewData = new UsercheckCardViewData(UserId, UserName, UserImage,authorityName);
                        list_data.add(usercheckCardViewData);
                    }
                    userAuthorityAdapter = new UserAuthorityAdapter(getActivity(),list_data); // 將資料交給adapter
                    recyclerView.setAdapter(userAuthorityAdapter);// 設置adapter給recyclerView

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),
                        "onErrorResponse form FirstLoadPostData in PostFragment" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse form FirstLoadPostData in PostFragment", error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }
}