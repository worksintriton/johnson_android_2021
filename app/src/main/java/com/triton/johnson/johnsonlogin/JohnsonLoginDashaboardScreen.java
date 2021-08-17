package com.triton.johnson.johnsonlogin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.triton.johnson.R;
import com.triton.johnson.adapter.JohnsonTicketsListAdapter;
import com.triton.johnson.api.APIInterface;

import com.triton.johnson.api.RetrofitClient;
import com.triton.johnson.arraylist.Adminstationlist;
import com.triton.johnson.requestpojo.JobNoListRequest;
import com.triton.johnson.requestpojo.JohnsonTicketListRequest;
import com.triton.johnson.requestpojo.StationNameRequest;
import com.triton.johnson.responsepojo.JobNoListResponse;
import com.triton.johnson.responsepojo.JohnsonTicketListResponse;
import com.triton.johnson.responsepojo.StationNameResponse;
import com.triton.johnson.session.SessionManager;
import com.triton.johnson.utils.ConnectionDetector;
import com.triton.johnson.utils.RestUtils;
import com.triton.johnson.view.JohnshonLoginDashboardActivity;
import com.triton.johnson.view.DepartmentListClass;
import com.triton.johnson.view.ProfileActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Iddinesh.
 */

public class JohnsonLoginDashaboardScreen extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private Adminstationlist adminStationList;
    private ArrayList<Adminstationlist> adminStationListArrayList = new ArrayList<>();
    //private AdminDepartmentAdapter adminStationListAdapter;
    private JohnsonTicketsListAdapter ticketsListAdapter;
    private TextView emptyCustomFontTextView;
    private SwipeRefreshLayout mWaveSwipeRefreshLayout;
    private FloatingActionButton floatingActionButton;
    private Dialog  dialog;
    private ImageView emptyImageView;
    private Button retryButton;
    private String networkStatus = "", selectStatus = "0", tabSelects;
    private LinearLayout elvalorLine, underLine;
    private String id;
    private LinearLayoutManager mLayoutManager;

    private String TAG = "JohnsonLoginDashaboardScreen";

    private  int type = 1;
    private List<StationNameResponse.DataBean> stationNameList = new ArrayList<>();
    HashMap<String, String> hashMap_StationId = new HashMap<>();
    HashMap<String, String> hashMap_JobNoId = new HashMap<>();
    Spinner spinner_stationname,spinner_jobno;
    private String StationName;
    private String StationName_id = "";
    String JobName,JobName_id = "";
    private List<JobNoListResponse.DataBean> JobNoList = new ArrayList<>();
    private List<JohnsonTicketListResponse.DataBean> ticketList = new ArrayList<>();

    LinearLayout ll_search,ll_clear,ll_job_no;
    Button btn_search;


    @SuppressLint("RestrictedApi")
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle arg) {

        Log.w(TAG,"onCreateView--->");
        final View view = inflater.inflate(R.layout.activity_johsnson_login_dashboard_screen, container,
                false);

        //private LinearLayout changePasswordLayout;
        SessionManager sessionManager = new SessionManager(getActivity());
        HashMap<String, String> hashMap = sessionManager.getUserDetails();
        id = hashMap.get(SessionManager.KEY_EMPID);

        recyclerView = view.findViewById(R.id.recycler_view);
        emptyCustomFontTextView =  view.findViewById(R.id.empty_text);
        emptyImageView = view.findViewById(R.id.empty_image);
        elvalorLine =  view.findViewById(R.id.elvator_line);
        LinearLayout elvatorLayout = view.findViewById(R.id.elvator_layout);
        LinearLayout underLayout = view.findViewById(R.id.under_layout);
        underLine =  view.findViewById(R.id.under_ground_line);

        mWaveSwipeRefreshLayout = view.findViewById(R.id.main_swipe);
        floatingActionButton =  view.findViewById(R.id.fab_createevent);
        LinearLayout profileLinearLayout = view.findViewById(R.id.profile_layout);
        retryButton =  view.findViewById(R.id.retry_button);
        LinearLayout sideMenuLayout = view.findViewById(R.id.back_layout);
        //changePasswordLayout =  view.findViewById(R.id.change_password);

        mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.WHITE);
        mWaveSwipeRefreshLayout.setOnRefreshListener(this);
        mWaveSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mWaveSwipeRefreshLayout.setOnRefreshListener(this);
        emptyCustomFontTextView.setVisibility(View.GONE);
        floatingActionButton.setVisibility(View.GONE);

        spinner_stationname = view.findViewById(R.id.spinner_stationname);
        ll_job_no = view.findViewById(R.id.ll_job_no);
        spinner_jobno = view.findViewById(R.id.spinner_jobno);
        ll_job_no.setVisibility(View.GONE);
        spinner_stationname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int arg2, long arg3) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                StationName = spinner_stationname.getSelectedItem().toString();
                StationName_id = hashMap_StationId.get(StationName);
                Log.w(TAG,"StationName : "+StationName+" StationName_id : "+StationName_id);
                if(StationName_id != null){
                    ll_job_no.setVisibility(View.VISIBLE);
                    JobNoListResponseCall(StationName_id);

                }else{
                    ll_job_no.setVisibility(View.GONE);
                    StationName_id = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        spinner_jobno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int arg2, long arg3) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                JobName = spinner_jobno.getSelectedItem().toString();
                JobName_id = hashMap_JobNoId.get(JobName);
                if(JobName != null && JobName.equalsIgnoreCase("Select Job No")){
                    JobName_id = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });



        elvatorLayout.setOnClickListener(view1 -> {
            selectStatus = "0";
            tabSelects = "0";
            type = 1;
            StationName_id = "";
            JobName_id = "";
            elvalorLine.setVisibility(View.VISIBLE);
            underLine.setVisibility(View.INVISIBLE);
            //DepaartmentUrl(ApiCall.API_URL+"get_estationtickets_new.php?user_id="+id);
            StationNameResponseCall();

        });
        underLayout.setOnClickListener(view2 -> {
            selectStatus = "1";
            tabSelects = "1";
            type = 2;
            StationName_id = "";
            JobName_id = "";
            elvalorLine.setVisibility(View.INVISIBLE);
            underLine.setVisibility(View.VISIBLE);
           // DepaartmentUrl(ApiCall.API_URL+"get_ustationtickets_new.php?user_id=" + id);
            StationNameResponseCall();


        });
               /* changePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SweetAlertDialog(AdminDepartmentScreen.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("CMRL")
                        .setContentText("Do you want to change password?")
                        .setCancelText("No")
                        .setConfirmText("Yes")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.dismiss();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.dismiss();
                                Intent intent = new Intent(AdminDepartmentScreen.this, ChangepawwordActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.new_right, R.anim.new_left);
                            }
                        })
                        .show();

            }
        });*/

        sideMenuLayout.setOnClickListener(view12 -> JohnshonLoginDashboardActivity.drawerLayout.openDrawer(JohnshonLoginDashboardActivity.nvDrawer));
        retryButton.setOnClickListener(view13 -> {
            networkStatus = ConnectionDetector.getConnectivityStatusString(getActivity());
            if (networkStatus.equalsIgnoreCase("Not connected to Internet")) {

                mWaveSwipeRefreshLayout.setVisibility(View.GONE);
                emptyCustomFontTextView.setVisibility(View.VISIBLE);
                emptyImageView.setVisibility(View.VISIBLE);
                retryButton.setVisibility(View.VISIBLE);

                emptyImageView.setImageResource(R.mipmap.wifi);
                emptyCustomFontTextView.setText(R.string.pleasecheckyourinternet);
                floatingActionButton.setVisibility(View.GONE);
            } else {
                if (selectStatus.equalsIgnoreCase("0")) {
                    type = 1;
                   // DepaartmentUrl(ApiCall.API_URL+"get_estationtickets_new.php?user_id=" + id);
                    StationNameResponseCall();
                } else if (selectStatus.equalsIgnoreCase("1")) {
                   // DepaartmentUrl(ApiCall.API_URL+"get_ustationtickets_new.php?user_id=" + id);
                    type = 2;
                    StationNameResponseCall();
                }
                mWaveSwipeRefreshLayout.setVisibility(View.VISIBLE);
                floatingActionButton.setVisibility(View.GONE);
            }

        });

        emptyImageView.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
        emptyCustomFontTextView.setVisibility(View.GONE);

        // check whether internet is on or not
        networkStatus = ConnectionDetector.getConnectivityStatusString(getActivity());

        if (networkStatus.equalsIgnoreCase("Not connected to Internet")) {

            mWaveSwipeRefreshLayout.setVisibility(View.GONE);
            emptyCustomFontTextView.setVisibility(View.VISIBLE);
            emptyImageView.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);

            emptyImageView.setImageResource(R.mipmap.wifi);
            emptyCustomFontTextView.setText(R.string.pleasecheckyourinternet);
            floatingActionButton.setVisibility(View.GONE);

        }
        else {
            if (JohnshonLoginDashboardActivity.tabSelects.equalsIgnoreCase("0")) {
                mWaveSwipeRefreshLayout.setVisibility(View.VISIBLE);
                floatingActionButton.setVisibility(View.GONE);
                tabSelects = "0";
                type = 1;
                StationNameResponseCall();
                //DepaartmentUrl(ApiCall.API_URL+"get_estationtickets_new.php?user_id=" + id);
                elvalorLine.setVisibility(View.VISIBLE);
                underLine.setVisibility(View.INVISIBLE);
            }
            else if (JohnshonLoginDashboardActivity.tabSelects.equalsIgnoreCase("1")) {
                mWaveSwipeRefreshLayout.setVisibility(View.VISIBLE);
                floatingActionButton.setVisibility(View.GONE);
                tabSelects = "1";
                type = 2;
                StationNameResponseCall();
               // DepaartmentUrl(ApiCall.API_URL+"get_ustationtickets_new.php?user_id=" + id);
                elvalorLine.setVisibility(View.INVISIBLE);
                underLine.setVisibility(View.VISIBLE);
            }


        }
        profileLinearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mWaveSwipeRefreshLayout.setEnabled(mLayoutManager.findFirstCompletelyVisibleItemPosition() == 0); // 0 is for first item position
            }
        });



        ll_search = view.findViewById(R.id.ll_search);
        btn_search = view.findViewById(R.id.btn_search);
        ll_clear = view.findViewById(R.id.ll_clear);
        ll_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JohnSonTicketListResponseCall();

            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JohnSonTicketListResponseCall();

            }
        });
        ll_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StationName_id = "";
                JobName_id = "";
                JohnSonTicketListResponseCall();
            }
        });


        return view;
    }

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {

        refresh();
    }

    private void refresh() {
        new Handler().postDelayed(() -> {
            // Refresh the department list to call api again
            if (selectStatus.equalsIgnoreCase("0")) {
               // DepaartmentUrl(ApiCall.API_URL+"get_estationtickets_new.php?user_id=" + id);
                StationNameResponseCall();
            } else {
                StationNameResponseCall();
                //DepaartmentUrl(ApiCall.API_URL+"get_ustationtickets_new.php?user_id=" + id);
            }

            mWaveSwipeRefreshLayout.setRefreshing(false);
        }, 3000);
    }



    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private DepartmentListClass.RecyclerItemClickListener.OnItemClickListener mListener;

        public interface OnItemClickListener {
             void onItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, DepartmentListClass.RecyclerItemClickListener.OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView view, @NonNull MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    /**
     * @param Url call the api to get the department list
     */
    private void DepaartmentUrl(String Url) {
        Log.w(TAG,"URL:"+"\t"+Url);
        Log.e("Url", "" + Url);
        dialog = new Dialog(getActivity(), R.style.NewProgressDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progroess_popup);
        dialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        @SuppressLint("SetTextI18n") JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, Url, null,
                response -> {
                    adminStationListArrayList.clear();
                    try {
                        dialog.dismiss();
                        JSONArray ja = response.getJSONArray("list");

                        for (int i = 0; i < ja.length(); i++) {

                            JSONObject jsonObject = ja.getJSONObject(i);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");
                            String id = jsonObject.getString("id");
                            String station_type = jsonObject.getString("station_type");
                            String name = jsonObject.getString("station_name");
                            String open_ticket_count = jsonObject.getString("topen");
                            String inprogress_ticket_count = jsonObject.getString("tinprogress");
                            String pending_ticket_count = jsonObject.getString("tpending");
                            String completed_ticket_count = jsonObject.getString("tcompleted");
                            String closed_ticket_count = jsonObject.getString("tclosed");
                            String favourite_status = jsonObject.getString("favourite_status");
                            String code = jsonObject.getString("code");
                            adminStationList = new Adminstationlist(status, message, id, code, station_type, name, open_ticket_count, inprogress_ticket_count, pending_ticket_count,
                                    completed_ticket_count, closed_ticket_count, favourite_status);
                            adminStationListArrayList.add(adminStationList);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    dialog.dismiss();

                    if (adminStationListArrayList.isEmpty()) {

                        emptyImageView.setVisibility(View.VISIBLE);
                        retryButton.setVisibility(View.GONE);
                        emptyCustomFontTextView.setVisibility(View.VISIBLE);

                        emptyImageView.setImageResource(R.mipmap.empty_icon);
                        emptyCustomFontTextView.setText("No departments");

                    } else {

                        emptyImageView.setVisibility(View.GONE);
                        retryButton.setVisibility(View.GONE);
                        emptyCustomFontTextView.setVisibility(View.GONE);

                       /* ticketsListAdapter = new TicketsListAdapter( getActivity(), adminStationListArrayList, id, tabSelects);
                        mLayoutManager = new LinearLayoutManager(getActivity());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());*/

                        recyclerView.setAdapter(ticketsListAdapter);
                    }
                },

                error -> {
                    Log.w(TAG,"onErrorResponse"+error.toString());
                    dialog.dismiss();
                }
        );

        requestQueue.add(jor);

    }


    @SuppressLint("LogNotTimber")
    private void StationNameResponseCall() {
        dialog = new Dialog(getActivity(), R.style.NewProgressDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progroess_popup);
        dialog.show();

        APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
        Call<StationNameResponse> call = apiInterface.StationNameResponseCall(RestUtils.getContentType(), stationNameRequest(type));
        Log.w(TAG,"StationNameResponseCall url  :%s"+" "+ call.request().url().toString());

        call.enqueue(new retrofit2.Callback<StationNameResponse>() {
            @SuppressLint("LogNotTimber")
            @Override
            public void onResponse(@NonNull Call<StationNameResponse> call, @NonNull Response<StationNameResponse> response) {
                dialog.dismiss();


                Log.w(TAG,"StationNameResponseCall" + new Gson().toJson(response.body()));
                if (response.body() != null) {

                    if (200 == response.body().getCode()) {
                        JohnSonTicketListResponseCall();
                        stationNameList.clear();
                        if(response.body().getData() != null && response.body().getData().size()>0){
                            stationNameList = response.body().getData();
                            setStationNameList(stationNameList);
                        }else{
                            stationNameList.clear();
                            setStationNameList(stationNameList);
                            Toasty.warning(getActivity(),response.body().getMessage(),Toasty.LENGTH_SHORT).show();
                        }
                    }
                }


            }

            @Override
            public void onFailure(@NonNull Call<StationNameResponse> call,@NonNull Throwable t) {
                dialog.dismiss();
                Log.w("StationNameResponse flr", "--->" + t.getMessage());
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void setStationNameList(List<StationNameResponse.DataBean> data) {
        Log.w(TAG, "data : " + new Gson().toJson(data));

        if (data != null && data.size() > 0) {
            ArrayList<String> StationArrayList = new ArrayList<>();
            StationArrayList.add("Select Station Name");
            for (int i = 0; i < data.size(); i++) {
                String StationName = data.get(i).getStation_name();
                hashMap_StationId.put(data.get(i).getStation_name(), data.get(i).get_id());

                Log.w(TAG, "StationName-->" + StationName);
                StationArrayList.add(StationName);

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, StationArrayList);
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item); // The drop down view
                spinner_stationname.setAdapter(spinnerArrayAdapter);
            }
        } else {
            ArrayList<String> StationArrayList = new ArrayList<>();
            StationArrayList.add("Select Station Name");
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, StationArrayList);
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item); // The drop down view
            spinner_stationname.setAdapter(spinnerArrayAdapter);

        }
    }
    private StationNameRequest stationNameRequest(int type) {
        /*
         * type : 1
         */

        StationNameRequest stationNameRequest = new StationNameRequest();
        stationNameRequest.setType(type);
        Log.w(TAG,"stationNameRequest "+ new Gson().toJson(stationNameRequest));
        return stationNameRequest;
    }


    @SuppressLint("LogNotTimber")
    private void JohnSonTicketListResponseCall() {
        dialog = new Dialog(getActivity(), R.style.NewProgressDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progroess_popup);
        dialog.show();

        APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
        Call<JohnsonTicketListResponse> call = apiInterface.JohnsonTicketListResponseCall(RestUtils.getContentType(), johnsonTicketListRequest());
        Log.w(TAG,"JohnsonTicketListResponse url  :%s"+" "+ call.request().url().toString());

        call.enqueue(new Callback<JohnsonTicketListResponse>() {
            @SuppressLint("LogNotTimber")
            @Override
            public void onResponse(@NonNull Call<JohnsonTicketListResponse> call, @NonNull Response<JohnsonTicketListResponse> response) {
                dialog.dismiss();
                Log.w(TAG,"JohnsonTicketListResponse" + new Gson().toJson(response.body()));
                if (response.body() != null) {

                    if (200 == response.body().getCode()) {
                        ticketList.clear();
                        if(response.body().getData() != null && response.body().getData().size()>0){
                            ticketList = response.body().getData();
                        }

                        if (ticketList != null && ticketList.size()>0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyImageView.setVisibility(View.GONE);
                            retryButton.setVisibility(View.GONE);
                            emptyCustomFontTextView.setVisibility(View.GONE);
                            ticketsListAdapter = new JohnsonTicketsListAdapter(getActivity(), ticketList, "", "");


                            //adminStationListAdapter = new AdminDepartmentAdapter(CmrlLoginDashaboardScreen.this, getActivity(), adminStationListArrayList, id, tabSelects);
                            mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());

                            recyclerView.setAdapter(ticketsListAdapter);



                        }
                        else {
                            recyclerView.setVisibility(View.GONE);
                            emptyImageView.setVisibility(View.VISIBLE);
                            retryButton.setVisibility(View.GONE);
                            emptyCustomFontTextView.setVisibility(View.VISIBLE);
                            emptyImageView.setImageResource(R.mipmap.empty_icon);
                            emptyCustomFontTextView.setText("No Breakdowns");
                        }





                    }
                }


            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<JohnsonTicketListResponse> call,@NonNull Throwable t) {
                dialog.dismiss();
                Log.w("JohnsonTicketListResponse flr", "--->" + t.getMessage());

            }
        });

    }
    private JohnsonTicketListRequest johnsonTicketListRequest() {

        /*
         * type : 1
         * station_id : 611511214f912e1856fc6d46
         * job_id : 61151dbaac7f9e21e2963133
         */



        JohnsonTicketListRequest johnsonTicketListRequest = new JohnsonTicketListRequest();
        johnsonTicketListRequest.setType(String.valueOf(type));
        johnsonTicketListRequest.setStation_id(StationName_id);
        johnsonTicketListRequest.setJob_id(JobName_id);
        Log.w(TAG,"johnsonTicketListRequest "+ new Gson().toJson(johnsonTicketListRequest));
        return johnsonTicketListRequest;
    }

    @SuppressLint("LogNotTimber")
    private void JobNoListResponseCall(String stationName_id) {
        dialog = new Dialog(getActivity(), R.style.NewProgressDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progroess_popup);
        dialog.show();

        APIInterface apiInterface = RetrofitClient.getClient().create(APIInterface.class);
        Call<JobNoListResponse> call = apiInterface.JobNoListResponseCall(RestUtils.getContentType(), jobNoListRequest(stationName_id));
        Log.w(TAG,"JobNoListResponse url  :%s"+" "+ call.request().url().toString());

        call.enqueue(new Callback<JobNoListResponse>() {
            @SuppressLint("LogNotTimber")
            @Override
            public void onResponse(@NonNull Call<JobNoListResponse> call, @NonNull Response<JobNoListResponse> response) {
                dialog.dismiss();
                Log.w(TAG,"JobNoListResponse" + new Gson().toJson(response.body()));
                if (response.body() != null) {

                    if (200 == response.body().getCode()) {

                        if(response.body().getData() != null && response.body().getData().size()>0){
                            JobNoList = response.body().getData();
                            setJobNoList(JobNoList);
                        }else{
                            JobNoList.clear();
                            setJobNoList(JobNoList);
                            Toasty.warning(getActivity(),response.body().getMessage(),Toasty.LENGTH_SHORT).show();
                        }
                    }
                }


            }

            @Override
            public void onFailure(@NonNull Call<JobNoListResponse> call,@NonNull Throwable t) {
                dialog.dismiss();
                Log.w("JobNoListResponse flr", "--->" + t.getMessage());
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void setJobNoList(List<JobNoListResponse.DataBean> jobNoList) {
        ArrayList<String> JobNoArrayList = new ArrayList<>();
        JobNoArrayList.add("Select Job No");
        for (int i = 0; i < jobNoList.size(); i++) {

            String JobNo = jobNoList.get(i).getJob_no();
            hashMap_JobNoId.put(jobNoList.get(i).getJob_no(), jobNoList.get(i).get_id());

            Log.w(TAG,"JobNo-->"+JobNo);
            JobNoArrayList.add(JobNo);

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, JobNoArrayList);
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item); // The drop down view
            spinner_jobno.setAdapter(spinnerArrayAdapter);




        }
    }
    private JobNoListRequest jobNoListRequest(String stationName_id) {

        /*
         * station_id : 6113c5ed895c673878e17719
         */

        JobNoListRequest jobNoListRequest = new JobNoListRequest();
        jobNoListRequest.setStation_id(stationName_id);
        Log.w(TAG,"jobNoListRequest "+ new Gson().toJson(jobNoListRequest));
        return jobNoListRequest;
    }



}
