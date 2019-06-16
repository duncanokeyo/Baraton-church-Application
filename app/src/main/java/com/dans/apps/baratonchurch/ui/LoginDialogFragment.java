package com.dans.apps.baratonchurch.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.dans.apps.baratonchurch.BuildConfig;
import com.dans.apps.baratonchurch.Constants;
import com.dans.apps.baratonchurch.R;
import com.dans.apps.baratonchurch.SettingsActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

/**
 * Created by duncan on 10/17/18.
 */

public class LoginDialogFragment extends DialogFragment implements View.OnClickListener, EnterPinDialog.onSuccess {

    private static final String TAG = "LoginFragment";
    private static final int RC_SIGN_IN = 123;
    private SharedPreferences preferences;
    private CardView chaplain;
    private CardView congregant;
    private FirebaseAuth auth;
    private LinearLayout layout;


    public interface onLogin{
        void onLoginSuccess();
    }

    public static onLogin callback;

    public  static LoginDialogFragment newInstance(onLogin onLogin){
        callback = onLogin;
        return new LoginDialogFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        auth = FirebaseAuth.getInstance();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);
        chaplain = view.findViewById(R.id.chaplain);
        congregant = view.findViewById(R.id.congregant);
        layout = view.findViewById(R.id.parent_layout);
        chaplain.setOnClickListener(this);
        congregant.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chaplain: {
                EnterPinDialog fragment = EnterPinDialog.newInstance(this);
                android.support.v4.app.FragmentManager fm = getFragmentManager();
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                android.support.v4.app.Fragment prev = fm.findFragmentByTag("pin_dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                fragment.show(ft, "pin_dialog");
                break;
            }
            case R.id.congregant: {
                checks();
                break;
            }
        }
    }

    private void checks() {
        if(auth.getCurrentUser() == null){
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .setLogo(R.drawable.logo)
                            .setTheme(R.style.AppTheme)
                            .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                            .build(),
                    RC_SIGN_IN);
        }else{
            if(callback!=null){
                callback.onLoginSuccess();
            }
            dismissAllowingStateLoss();
        }
    }

    @Override
    public void onPinCorrect() {
        if(callback!=null){
            callback.onLoginSuccess();
        }
        dismissAllowingStateLoss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == getActivity().RESULT_OK) {
                preferences.edit().putInt(SettingsActivity.KEY_USER_TYPE,Constants.USER_TYPE.Congregant).apply();
                if(callback!=null){
                    callback.onLoginSuccess();
                }
                dismissAllowingStateLoss();
                //Intent intent = new Intent(LoginActivity.this,BaseActivity.class);
                //intent.putExtra(SettingsActivity.KEY_USER_TYPE,Constants.USER_TYPE.Congregant);
                //startActivity(intent);
                //finish();
                //return;
            } else {
                if (response == null) {
                    // User pressed back button
                    showError(R.string.sign_in_cancelled);
                    return;
                }
                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showError(R.string.no_internet_connection);
                    return;
                }
                Log.e(TAG,"sign in error ",response.getError());
                showError(R.string.unknown_error);
            }
        }
    }

    public void showError(int string){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(string);
        builder.setPositiveButton(R.string.ok,null);
        builder.show();

    }
}
