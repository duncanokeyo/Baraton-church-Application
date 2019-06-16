
package com.dans.apps.baratonchurch.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dans.apps.baratonchurch.BUCApplication;
import com.dans.apps.baratonchurch.Constants;
import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.models.Request;
import com.dans.apps.baratonchurch.utils.LogUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RequestFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        View.OnClickListener, OnSuccessListener<DocumentSnapshot>, SlidingUpPanelLayout.PanelSlideListener, RequestAdapter.ActionListener {

    String TAG = "RequestFragment";

    Button send;
    Spinner requestTypes;
    int selectedRequest = -1;
    CardView entriesContainer;
    FirebaseAuth auth;
    SharedPreferences preference;
    FirebaseFirestore store;
    boolean isChaplain;
    TextInputLayout transferTypeInputLayout;
    TextInputLayout previousChurchInputLayout;
    TextInputLayout newChurchInputLayout;
    TextInputLayout reasonForTransferInputLayout;
    TextInputLayout nameOfBrideInputLayout;
    TextInputLayout nameOfBrideGroomInputLayout;
    TextInputLayout weddingDateInputLayout;
    TextInputLayout venueInputLayout;
    TextInputLayout facilitiesNeededInputLayout;
    TextInputLayout nameOfChildInputLayout;
    TextInputLayout dateOfBirthInputLayout;
    TextInputLayout dateOfDedicationInputLayout;
    TextInputLayout fatherNameInputLayout;
    TextInputLayout motherNameInputLayout;
    TextInputLayout dateOfBaptismInputLayout;

    TextInputEditText requestTitle;
    TextInputEditText sender;
    TextInputEditText transferType;
    TextInputEditText previousChurch;
    TextInputEditText newChurch;
    TextInputEditText reasonForTransfer;
    TextInputEditText nameOfBride;
    TextInputEditText nameOfBrideGroom;
    TextInputEditText weddingDate;
    TextInputEditText venue;
    TextInputEditText facilitiesNeeded;
    TextInputEditText nameOfChild;
    TextInputEditText dateOfBirth;
    TextInputEditText dateOfDedication;
    TextInputEditText fatherName;
    TextInputEditText motherName;
    TextInputEditText dateOfBaptism;
    TextInputEditText additionalInfo;

    SlidingUpPanelLayout panelLayout;
    ImageView panelStateIndicator;
    Drawable panelStateIndicatorExpanded;
    Drawable panelStateIndicatorCollapsed;

    RecyclerView requests;
    TextView messageView;
    ProgressBar progressBar;

    LinearLayoutManager layoutManager;
    RequestAdapter adapter;
    DatabaseReference database;
    ValueEventListener requestValueEventListener;
    private List<Request> requestsData= new ArrayList<>();

    public interface Callback{
        void onRequestLogin();
    }

    Callback callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_requests, container, false);
        requestTypes = root.findViewById(R.id.spinner);
        send = root.findViewById(R.id.send_request);
        auth= FirebaseAuth.getInstance();
        store = BUCApplication.getInstance().getStore();
        panelStateIndicatorExpanded = getResources().getDrawable(R.drawable.more);
        panelStateIndicatorCollapsed = getResources().getDrawable(R.drawable.less);
        panelLayout = root.findViewById(R.id.sliding_layout);
        panelStateIndicator = root.findViewById(R.id.panel_state_indicator);
        panelLayout.addPanelSlideListener(this);
        entriesContainer = root.findViewById(R.id.entry_fields_container);
        send.setOnClickListener(this);
        preference = PreferenceManager.
                getDefaultSharedPreferences(getActivity());

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        requests = root.findViewById(R.id.requests);
        adapter = new RequestAdapter(getActivity(),this,isChaplain);
        requests.setAdapter(adapter);
        requests.setLayoutManager(layoutManager);
        requests.addItemDecoration(new RecyclerViewDecoration(6,RecyclerViewDecoration.VERTICAL));
        messageView = root.findViewById(R.id.message_view);
        progressBar = root.findViewById(R.id.progress);

        transferTypeInputLayout= root.findViewById(R.id.transfer_type_input_layout);
        previousChurchInputLayout = root.findViewById(R.id.previous_church_input_layout);
        newChurchInputLayout = root.findViewById(R.id.new_church_input_layout);
        reasonForTransferInputLayout = root.findViewById(R.id.reason_for_transfer_input_layout);
        nameOfBrideInputLayout = root.findViewById(R.id.name_of_bride_input_layout);
        nameOfBrideGroomInputLayout = root.findViewById(R.id.name_of_bridegroom_input_layout);
        weddingDateInputLayout = root.findViewById(R.id.wedding_date_input_layout);
        venueInputLayout = root.findViewById(R.id.venue_input_layout);
        facilitiesNeededInputLayout = root.findViewById(R.id.facilities_needed_input_layout);
        nameOfChildInputLayout = root.findViewById(R.id.name_of_child_input_layout);
        dateOfBirthInputLayout = root.findViewById(R.id.date_of_birth_input_layout);
        dateOfDedicationInputLayout = root.findViewById(R.id.date_of_dedication_input_layout);
        fatherNameInputLayout = root.findViewById(R.id.father_name_input_layout);
        motherNameInputLayout = root.findViewById(R.id.mother_name_input_layout);
        dateOfBaptismInputLayout = root.findViewById(R.id.date_of_baptism_input_layout);

        requestTitle = root.findViewById(R.id.request_title);
        sender = root.findViewById(R.id.sender_name);
        transferType = root.findViewById(R.id.transfer_type);
        previousChurch = root.findViewById(R.id.previous_church);
        newChurch = root.findViewById(R.id.new_church);
        reasonForTransfer = root.findViewById(R.id.reason_for_transfer);
        nameOfBride = root.findViewById(R.id.name_of_bride);
        nameOfBrideGroom = root.findViewById(R.id.name_of_bridegroom);
        weddingDate = root.findViewById(R.id.wedding_date);
        venue= root.findViewById(R.id.venue);
        facilitiesNeeded = root.findViewById(R.id.facilities_needed);
        nameOfChild = root.findViewById(R.id.name_of_child);
        dateOfBirth = root.findViewById(R.id.date_of_birth);
        dateOfDedication = root.findViewById(R.id.date_of_dedication);
        fatherName = root.findViewById(R.id.father_name);
        motherName = root.findViewById(R.id.mother_name);
        dateOfBaptism = root.findViewById(R.id.date_of_baptism);
        additionalInfo = root.findViewById(R.id.additional_info);

        store.document(Constants.Store.requests_path).get().addOnSuccessListener(getActivity(),this);

        initializeViewStates(Constants.RequestTypes.OTHER);
        requestTypes.setOnItemSelectedListener(this);
        messageView.setVisibility(View.GONE);

        weddingDate.setFocusable(false);
        dateOfBirth.setFocusable(false);
        dateOfDedication.setFocusable(false);
        dateOfBaptism.setFocusable(false);

        weddingDate.setClickable(true);
        dateOfBirth.setClickable(true);
        dateOfDedication.setClickable(true);
        dateOfBaptism.setClickable(true);

        weddingDate.setOnClickListener(this);
        dateOfBirth.setOnClickListener(this);
        dateOfDedication.setOnClickListener(this);
        dateOfBaptism.setOnClickListener(this);

        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_requestfragment,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_clear:{
                if(adapter!=null) {
                    List<Request> requests = adapter.getItems();
                    for(Request request:requests){
                        database.child(request.getRequestID()).removeValue();
                    }
                }
                break;
            }
            case R.id.action_filter:
                ArrayList<String>menuItems = new ArrayList<>();
                menuItems.add("Prayer");
                menuItems.add("Announcement");
                menuItems.add("Membership transfer");
                menuItems.add("Marriage solemnization");
                menuItems.add("Child dedication");
                menuItems.add("Baptism");
                menuItems.add("Other");
                menuItems.add("All");

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setSingleChoiceItems(menuItems.toArray(new String[menuItems.size()]),
                        -1,
                        new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ArrayList<Request>filter = new ArrayList<>();
                        if(which == 7){
                            adapter.addRequests(requestsData);
                        }else{
                            for(Request request:requestsData){
                                LogUtils.d(TAG,"request type = "+request.getRequestType()+" which = "+which);
                                if(request.getRequestType() == which){
                                    filter.add(request);
                                }
                            }
                            adapter.addRequests(filter);
                        }
                        dialog.dismiss();
                    }
                });

                builder.show();
                return true;
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
                            for(Request request:requestsData){
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
                                adapter.addRequests(filter);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Callback){
            callback = (Callback) context;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            database.removeEventListener(requestValueEventListener);
        }catch (Exception ignored){}
    }


    public void fetchRequests(){
        if(database == null) {
            database = FirebaseDatabase.getInstance().getReference().
                    child("Requests");
        }
        if(requestValueEventListener!=null){
            database.removeEventListener(requestValueEventListener);
        }
        requestValueEventListener = null;

        requestValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LogUtils.d(TAG,"on data change called ----> "+dataSnapshot.getChildrenCount());
                ArrayList<Request>requests = new ArrayList<>();
                progressBar.setVisibility(View.GONE);
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Request request = snapshot.getValue(Request.class);
                    request.setRequestID(snapshot.getKey());
                    requests.add(request);
                    LogUtils.d(TAG,"the values are --->"+request.toString());
                }

                if(!isUserLoggedIn()){
                    messageView.setVisibility(View.VISIBLE);
                    messageView.setText(R.string.login_to_post_requests);
                    return;
                }

                if(requests.isEmpty()){
                    messageView.setVisibility(View.VISIBLE);
                    if(isChaplain){
                        messageView.setText(R.string.no_requests_chaplain);
                    }else {
                        messageView.setText(R.string.no_requests);
                    }
                }else{
                    Collections.reverse(requests);
                    requestsData.clear();
                    requestsData.addAll(requests);
                    messageView.setVisibility(View.GONE);
                }

                if(adapter!=null){
                    adapter.addRequests(requests);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                messageView.setVisibility(View.VISIBLE);
                messageView.setText(R.string.error_fetching_data);
            }
        };

        if(isChaplain) {
            database.addValueEventListener(requestValueEventListener);
        }

        if(auth.getCurrentUser()!=null) {
            database.orderByChild("senderEmailAddress")
                    .equalTo(auth.getCurrentUser().getEmail())
                    .addValueEventListener(requestValueEventListener);
        }else{
            progressBar.setVisibility(View.GONE);
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(R.string.not_logged_in);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchRequests();
    }


    public void clearFields(){
        requestTitle.getText().clear();
        requestTitle.setError(null);
        //sender.getText().clear();
        //sender.setError(null);
        transferType.getText().clear();
        transferType.setError(null);
        previousChurch.getText().clear();
        previousChurch.setError(null);
        newChurch.getText().clear();
        newChurch.setError(null);
        reasonForTransfer.getText().clear();
        reasonForTransfer.setError(null);
        nameOfBride.getText().clear();
        nameOfBride.setError(null);
        nameOfBrideGroom.getText().clear();
        nameOfBrideGroom.setError(null);
        weddingDate.getText().clear();
        weddingDate.setError(null);
        venue.getText().clear();
        venue.setError(null);
        facilitiesNeeded.getText().clear();
        facilitiesNeeded.setError(null);
        nameOfChild.getText().clear();
        nameOfChild.setError(null);
        dateOfBirth.getText().clear();
        dateOfBirth.setError(null);
        dateOfDedication.getText().clear();
        dateOfDedication.setError(null);
        fatherName.getText().clear();
        fatherName.setError(null);
        motherName.getText().clear();
        motherName.setError(null);
        dateOfBaptism.getText().clear();
        dateOfBaptism.setError(null);
        additionalInfo.getText().clear();
        additionalInfo.setError(null);
    }

    public void initializeViewStates(int requestType){
        LogUtils.d(TAG,"initialize view states called with request type ===> "+requestType);
        clearFields();
        if(isChaplain){
            sender.setText(R.string.chaplain_office);
        }else {
            if (auth.getCurrentUser() != null) {
                sender.setText(auth.getCurrentUser().getDisplayName());
            }
        }

        requestTitle.setText("");
        transferTypeInputLayout.setVisibility(View.GONE);
        previousChurchInputLayout.setVisibility(View.GONE);
        newChurchInputLayout.setVisibility(View.GONE);
        reasonForTransferInputLayout.setVisibility(View.GONE);
        nameOfBrideInputLayout.setVisibility(View.GONE);
        nameOfBrideGroomInputLayout.setVisibility(View.GONE);
        weddingDateInputLayout.setVisibility(View.GONE);
        venueInputLayout.setVisibility(View.GONE);
        facilitiesNeededInputLayout.setVisibility(View.GONE);
        nameOfChildInputLayout.setVisibility(View.GONE);
        dateOfBirthInputLayout.setVisibility(View.GONE);
        dateOfDedicationInputLayout.setVisibility(View.GONE);
        fatherNameInputLayout.setVisibility(View.GONE);
        motherNameInputLayout.setVisibility(View.GONE);
        dateOfBaptismInputLayout.setVisibility(View.GONE);

        switch (requestType){
            case Constants.RequestTypes.PRAYER: {
                requestTitle.setText(R.string.prayer);
                break;
            }
            case Constants.RequestTypes.ANNOUNCEMENT:{
                requestTitle.setText(R.string.announcement);
                break;
            }
            case Constants.RequestTypes.BAPTISM:{
                requestTitle.setText(R.string.baptism);
                dateOfBaptismInputLayout.setVisibility(View.VISIBLE);
                break;
            }
            case Constants.RequestTypes.CHILD_DEDICATION:{
                nameOfChildInputLayout.setVisibility(View.VISIBLE);
                dateOfBirthInputLayout.setVisibility(View.VISIBLE);
                dateOfDedicationInputLayout.setVisibility(View.VISIBLE);
                fatherNameInputLayout.setVisibility(View.VISIBLE);
                motherNameInputLayout.setVisibility(View.VISIBLE);

                requestTitle.setText(R.string.child_dedication);
                break;
            }
            case Constants.RequestTypes.MARRIAGE_SOLEMNIZATION:{
                requestTitle.setText(R.string.marriage_solemnization);
                nameOfBrideInputLayout.setVisibility(View.VISIBLE);
                nameOfBrideGroomInputLayout.setVisibility(View.VISIBLE);
                weddingDateInputLayout.setVisibility(View.VISIBLE);
                venueInputLayout.setVisibility(View.VISIBLE);
                facilitiesNeededInputLayout.setVisibility(View.VISIBLE);
                break;
            }
            case Constants.RequestTypes.MEMBERSHIP_TRANSFER:{
                requestTitle.setText(R.string.membership_transfer);
                transferTypeInputLayout.setVisibility(View.VISIBLE);
                previousChurchInputLayout.setVisibility(View.VISIBLE);
                newChurchInputLayout.setVisibility(View.VISIBLE);
                reasonForTransferInputLayout.setVisibility(View.VISIBLE);
                break;
            }
        }

    }

    @Override
    public void onSuccess(DocumentSnapshot documentSnapshot) {
        ArrayList<String> types = (ArrayList<String>) documentSnapshot.getData().get("types");
        if(isChaplain){
            types.clear();
            types.add("");
            types.add("Announcement");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        requestTypes.setAdapter(adapter);
        requestTypes.setSelection(isChaplain?0:Constants.RequestTypes.OTHER);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fetchRequests();
    }

    boolean isUserLoggedIn(){
        return auth.getCurrentUser()!=null;//||isChaplain;
    }

    @Override
    public void onClick(final View view) {
        if(view.getId() == R.id.send_request){
            if(!isUserLoggedIn()){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getResources().getString(R.string.login_prompt));
                builder.setNegativeButton(android.R.string.cancel,null);
                builder.setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(callback!=null){
                            callback.onRequestLogin();
                        }
                    }
                });
                builder.create().show();
            }else{
                sendRequest();
            }
        }else{
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    String date = dayOfMonth+"/"+month+"/"+year;
                    ((TextInputEditText)view).setText(date);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }
    }

    public void sendRequest(){
        String emailAddress="";
        String phoneNumber="";
        try {
            emailAddress = auth.getCurrentUser().getEmail();
        }catch (Exception ignored){}
        try {
            phoneNumber = auth.getCurrentUser().getPhoneNumber();
        }catch (Exception ignored){}

        if(selectedRequest == -1){
            return;
        }

        String title = requestTitle.getText().toString();
        String sender = this.sender.getText().toString();
        String info = additionalInfo.getText().toString();
        if(TextUtils.isEmpty(title)){
            requestTitle.setError("");
            return;
        }
        if(TextUtils.isEmpty(sender)){
            this.sender.setError("");
            return;
        }
        Request request = new Request(selectedRequest,sender,emailAddress,phoneNumber,title,info);
        if(isChaplain) {
            request.setRequestStatus(Constants.RequestStatus.APPROVED);
        }
        HashMap<String,String> specificRequestFields = new HashMap<>();
        switch (selectedRequest){
            case Constants.RequestTypes.OTHER:
            case Constants.RequestTypes.PRAYER:
            case Constants.RequestTypes.ANNOUNCEMENT:
                if(TextUtils.isEmpty(info)){
                    additionalInfo.setError("Please enter additional information");
                    return;
                }
                break;
            case Constants.RequestTypes.BAPTISM:
                String baptismDate = dateOfBaptism.getText().toString();
                if(TextUtils.isEmpty(baptismDate)){
                    dateOfBaptism.setError("Please enter the date of baptism");
                    return;
                }
                specificRequestFields.put(Constants.Request.dateOfBaptism,baptismDate);
                break;
            case Constants.RequestTypes.CHILD_DEDICATION:
                String childName = nameOfChild.getText().toString();
                String dateBirth = dateOfBirth.getText().toString();
                String dateDedication = dateOfDedication.getText().toString();
                String fathersName = fatherName.getText().toString();
                String mothersName = motherName.getText().toString();
                if(TextUtils.isEmpty(childName)){nameOfChild.setError("");return;}
                if(TextUtils.isEmpty(fathersName) && TextUtils.isEmpty(mothersName)){
                    fatherName.setError("Please enter the child's father name");
                    motherName.setError("Please enter the child's mother name");
                    return;
                }
                if(TextUtils.isEmpty(dateDedication)){dateOfDedication.setError("Please set the date of dedication");return;}
                specificRequestFields.put(Constants.Request.nameOfChild,childName.trim());
                specificRequestFields.put(Constants.Request.dateOfBirth,dateBirth.trim());
                specificRequestFields.put(Constants.Request.dateOfDedication,dateDedication.trim());
                specificRequestFields.put(Constants.Request.fatherName,fathersName.trim());
                specificRequestFields.put(Constants.Request.motherName,mothersName.trim());
                break;
            case Constants.RequestTypes.MARRIAGE_SOLEMNIZATION:
                String brideName= nameOfBride.getText().toString();
                String brideGroomName = nameOfBrideGroom.getText().toString();
                String dateWedding = weddingDate.getText().toString();
                String weddingVenue = venue.getText().toString();
                String facilities = facilitiesNeeded.getText().toString();
                if(TextUtils.isEmpty(brideName)){
                    nameOfBride.setError("Please enter bride name");
                    return;
                }
                if(TextUtils.isEmpty(brideGroomName)){
                    nameOfBrideGroom.setError("Please enter bridegroom name");
                    return;
                }
                if(TextUtils.isEmpty(dateWedding)){
                    weddingDate.setError("Please enter the date of wedding");
                    return;
                }
                if(TextUtils.isEmpty(weddingVenue)){
                    venue.setError("Please enter bride name");
                    return;
                }
                if(TextUtils.isEmpty(facilities)){
                    facilitiesNeeded.setError("Please list the facilities you will need");
                    return;
                }
                if(!isCorrectLength(brideName,brideGroomName,dateWedding,facilities)){
                    Toast.makeText(getActivity(),"Values should be of length greater than 3",Toast.LENGTH_SHORT).show();
                    return;
                }
                specificRequestFields.put(Constants.Request.nameOfBride,brideName.trim());
                specificRequestFields.put(Constants.Request.nameOfBrideGroom,brideGroomName.trim());
                specificRequestFields.put(Constants.Request.weddingDate,dateWedding.trim());
                specificRequestFields.put(Constants.Request.facilitiesNeeded,facilities.trim());
                break;
            case Constants.RequestTypes.MEMBERSHIP_TRANSFER:
                String typeTransfer = transferType.getText().toString();
                String churchPrevious = previousChurch.getText().toString();
                String churchNew = newChurch.getText().toString();
                String transferReason = reasonForTransfer.getText().toString();

                if(TextUtils.isEmpty(typeTransfer)){
                    transferType.setError("Please enter the transfer type (in/out)");
                    return;
                }
                //todo why is it not working?
               /* if(!typeTransfer.equalsIgnoreCase("in") || !typeTransfer.equalsIgnoreCase("out")) {
                    Toast.makeText(getActivity(),"Wrong transfer type value",Toast.LENGTH_SHORT).show();
                    return;
                }*/

                if(TextUtils.isEmpty(churchPrevious)){
                    previousChurch.setError("Please enter the name of previous church");
                    return;
                }
                if(TextUtils.isEmpty(churchNew)){
                    newChurch.setError("Please enter the name of new church");
                    return;
                }
                if(TextUtils.isEmpty(transferReason)){
                    reasonForTransfer.setError("Please state reason for transfer");
                    return;
                }
                if(!isCorrectLength(churchPrevious,churchNew,transferReason)){
                    Toast.makeText(getActivity(),"Values should be of length greater than 3",Toast.LENGTH_SHORT).show();
                    return;
                }
                specificRequestFields.put(Constants.Request.transferType,typeTransfer.trim());
                specificRequestFields.put(Constants.Request.previousChurch,churchPrevious.trim());
                specificRequestFields.put(Constants.Request.newChurch,churchNew.trim());
                specificRequestFields.put(Constants.Request.reasonForTansfer,transferReason.trim());
                break;
        }

        request.setSpecificRequestFields(specificRequestFields);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Requests");
        reference.push().setValue(request).addOnSuccessListener(getActivity(), new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if(isAdded()){
                    clearFields();
                    showSendReport(isChaplain?"Request sent":"Request sent, waiting for approval ..",false);
                }else {
                    Toast.makeText(BUCApplication.getInstance().getApplicationContext(), "Request sent", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(isAdded()){
                    showSendReport("Failed to send request",true);
                }else{
                    Toast.makeText(BUCApplication.getInstance().getApplicationContext(),"Failed to send request",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showSendReport(String message,boolean isError) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        if(isError){
            builder.setTitle(R.string.failed_to_send);
            builder.setIcon(R.drawable.rejected);
        }else{
            builder.setTitle(R.string.sent);
            builder.setIcon(R.drawable.approved);
        }
        builder.setPositiveButton(R.string.ok,null);
        builder.show();
    }

    boolean isCorrectLength(String ... fields){
        for(String field:fields){
            if(field.length()<3){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if(adapterView.getCount()==1){
            position = Constants.RequestTypes.OTHER;
        }
        selectedRequest = position;
        initializeViewStates(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
    @Override
    public void onPanelSlide(View panel, float slideOffset) {}

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
            panelStateIndicator.setImageDrawable(panelStateIndicatorCollapsed);
        }else if(newState == SlidingUpPanelLayout.PanelState.EXPANDED){
            panelStateIndicator.setImageDrawable(panelStateIndicatorExpanded);
        }
    }

    @Override
    public void onDeleteRequest(Request request) {
        if((database!=null) && (auth.getCurrentUser()!=null)){
            database.child(request.getRequestID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(BUCApplication.getInstance().getApplicationContext(),R.string.removed_request,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onRequestRejected(Request request,String reasonForRejection) {
        Map<String, Object> fieldValue = new HashMap<>();
        fieldValue.put("requestStatus",Constants.RequestStatus.REJECTED);
        fieldValue.put("reasonForRejection",reasonForRejection);
        database.child(request.getRequestID()).updateChildren(fieldValue);
    }

    @Override
    public void onRequestApproved(Request request) {
        Map<String, Object> fieldValue = new HashMap<>();
        fieldValue.put("requestStatus",Constants.RequestStatus.APPROVED);
        database.child(request.getRequestID()).updateChildren(fieldValue);
    }

    public void setUserType(int userType) {
        LogUtils.d(TAG,"user type is --> "+userType);
        isChaplain = (userType == Constants.USER_TYPE.Chaplain);

    }
}
