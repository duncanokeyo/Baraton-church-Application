package com.dans.apps.baratonchurch.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;

import com.dans.apps.baratonchurch.BUCApplication;
import com.dans.apps.baratonchurch.Constants;
import com.dans.apps.baratonchurch.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by duncan on 8/1/18.
 */

public class EnterPinDialog extends DialogFragment {
    String TAG = "EnterPinDialog";
    String KEY_PIN = "pin";
    EditText pinValue;
    SharedPreferences preferences;
    FirebaseFirestore store;
    int pin;

    public interface onSuccess{
        void onPinCorrect();
    }

    public static onSuccess callback;

    public  static EnterPinDialog newInstance(onSuccess success){
        callback = success;
        return new EnterPinDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo encrypt this pin
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        pin =preferences.getInt(KEY_PIN,-1);
        store = BUCApplication.getInstance().getStore();

        if(pin ==-1){
            store.document(Constants.Store.chaplain_path).
                    get().addOnSuccessListener(getActivity(), new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    pin = documentSnapshot.getLong(Constants.Chaplain.pin).intValue();
                    preferences.edit().putInt(KEY_PIN,pin).apply();
                }
            });
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.enter_pin);
        builder.setCancelable(false);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.enter_pin,null);
        pinValue = view.findViewById(R.id.pin);
        Button cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAllowingStateLoss();
            }
        });

        Button ok = view.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /////verfity
                String value = pinValue.getText().toString();
                if(value.isEmpty()){
                    pinValue.setError("This field cannot be null");
                    return;
                }
                if(pin == -1){
                    showError(false);
                    return;
                }

                int userPin = Integer.valueOf(value);
                if(userPin!=pin){
                    pinValue.setError("Incorrect pin");
                    pinValue.startAnimation(shakeError());
                    return;
                }
                if(callback!=null){
                    callback.onPinCorrect();
                    dismissAllowingStateLoss();
                }
            }
        });
        builder.setView(view);
        return builder.create();
    }

    public TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }



    /**
     * here we only show two errors, 1, incorrect password, and bad network connection..
     * @param incorrectPassWord
     */
    public void showError(boolean incorrectPassWord){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(incorrectPassWord? R.string.incorrect_pin : R.string.error_authenicating);
        builder.setPositiveButton(R.string.ok,null);
        builder.create().show();
    }
}
