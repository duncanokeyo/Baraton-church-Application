package com.dans.apps.baratonchurch.ui;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dans.apps.baratonchurch.Constants;
import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.models.Request;
import com.dans.apps.baratonchurch.utils.LogUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AnnouncementFragment extends Fragment{
    String TAG = "Announcement";
    private RecyclerView list;
    private ProgressBar progressBar;
    private TextView message;
    LinearLayoutManager layoutManager;
    AnnouncementsAdapter adapter;
    SharedPreferences preference;
    DatabaseReference database;
    ValueEventListener announcementValueEventListener;
    FirebaseAuth auth;
    List<Request>announcements = new ArrayList<>();
    boolean isChaplain;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_announcement, container, false);
        progressBar =rootView.findViewById(R.id.announcement_progressbar);
        message = rootView.findViewById(R.id.no_announcements);
        list = rootView.findViewById(R.id.announcement_list);

        message.setVisibility(View.GONE);
        adapter = new AnnouncementsAdapter(getActivity());
        layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        list.setAdapter(adapter);
        list.setLayoutManager(layoutManager);
        preference  = PreferenceManager.getDefaultSharedPreferences(getActivity());

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        database = FirebaseDatabase.getInstance().getReference().child("Requests");
        announcementValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Request>requests = new ArrayList<>();
                progressBar.setVisibility(View.GONE);
                message.setVisibility(View.GONE);

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Request request = snapshot.getValue(Request.class);
                    request.setRequestID(snapshot.getKey());
                    if(request.getRequestStatus() == Constants.RequestStatus.APPROVED) {
                        requests.add(request);
                        LogUtils.d(TAG, "the values are --->" + request.toString());
                    }
                }

               /* if(!isUserLoggedIn()){
                    message.setVisibility(View.VISIBLE);
                    message.setText(R.string.login_view_announcement);
                    return;
                }
*/
                if(requests.isEmpty()){
                    message.setVisibility(View.VISIBLE);
                    message.setText(R.string.no_announcements);
                }else{
                    message.setVisibility(View.GONE);
                    Collections.reverse(requests);
                    announcements.clear();
                    announcements.addAll(requests);
                }
                if(adapter!=null){
                    adapter.addAnnouncements(requests);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                message.setText(R.string.error_fetching_data);
            }
        };

        database.orderByChild("requestType").equalTo(Constants.RequestTypes.ANNOUNCEMENT).
                addValueEventListener(announcementValueEventListener);
    }

    boolean isUserLoggedIn(){
        return auth.getCurrentUser()!=null||isChaplain;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_announcementfragment,menu);
        MenuItem item = menu.findItem(R.id.action_clear);
        if(!isChaplain){
            item.setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear: {
                if (adapter != null) {
                    List<Request> requests = adapter.getItems();
                    for (Request request : requests) {
                        database.child(request.getRequestID()).removeValue();
                    }
                }
                break;
            }
            case R.id.action_calender:
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        String selectedStartDate = year + "-" + month + "-" + dayOfMonth;
                        List<Request>filter = new ArrayList<>();
                        try {
                            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                            Date date = dateFormat.parse(selectedStartDate);
                            for(Request request:announcements){
                                Timestamp timestamp = new Timestamp((Long) request.getCreatedAt());
                                Date requestDate = new Date(timestamp.getTime());
                                calendar.setTime(requestDate);
                                int requestYear = calendar.get(Calendar.YEAR);
                                int requestMonth = calendar.get(Calendar.MONTH);
                                int requestDay = calendar.get(Calendar.DAY_OF_MONTH);

                                String requestDateString = requestYear + "-" + requestMonth + "-" + requestDay;
                                Date formattedDate = dateFormat.parse(requestDateString);
                                if(formattedDate.compareTo(date) == 0){
                                    filter.add(request);
                                }
                            }
                            if(adapter!=null){
                                adapter.addAnnouncements(filter);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //todo filter by the calender
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            database.removeEventListener(announcementValueEventListener);
        }catch (Exception ignored){}
    }


    public void setUserType(int userType) {
        LogUtils.d(TAG,"user type is --> "+userType);
        isChaplain = (userType == Constants.USER_TYPE.Chaplain);
    }
}
