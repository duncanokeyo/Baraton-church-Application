package com.dans.apps.baratonchurch;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.dans.apps.baratonchurch.service.FetchWorker;
import com.dans.apps.baratonchurch.ui.AnnouncementFragment;
import com.dans.apps.baratonchurch.ui.HomeFragment;
import com.dans.apps.baratonchurch.ui.LoginDialogFragment;
import com.dans.apps.baratonchurch.ui.QuarterliesFragment;
import com.dans.apps.baratonchurch.ui.RequestFragment;
import com.dans.apps.baratonchurch.ui.SermonsFragment;
import com.dans.apps.baratonchurch.utils.LogUtils;
import com.dans.apps.baratonchurch.widget.CustomBottomNavigationView;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class BaseActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        HomeFragment.CallBack, LoginDialogFragment.onLogin , RequestFragment.Callback{

    String TAG = "BaseActivity";

    FragmentManager manager;
    SharedPreferences preference;
    CustomBottomNavigationView navigation;
    RelativeLayout container;
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 123;
    int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        preference = PreferenceManager.getDefaultSharedPreferences(this);
        userType = preference.getInt(SettingsActivity.KEY_USER_TYPE,Constants.USER_TYPE.Unkown);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        navigation = findViewById(R.id.bottom_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        container = findViewById(R.id.container);
        setSupportActionBar(toolbar);

        navigation.enableItemShiftingMode(true);
        navigation.enableShiftingMode(true);
        navigation.setOnNavigationItemSelectedListener(this);
        manager = getFragmentManager();
        preference = PreferenceManager.getDefaultSharedPreferences(this);

        if(savedInstanceState==null) {
            getFragmentManager().beginTransaction().add(R.id.fragment_container,
                    new HomeFragment()).addToBackStack(null).commit();
            setTitle(getResources().getString(R.string.home));
        }

        scheduleWork();
    }

    private void scheduleWork() {
        PeriodicWorkRequest.Builder fetcher
                = new PeriodicWorkRequest.Builder(FetchWorker.class,24,TimeUnit.HOURS);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        fetcher.setConstraints(constraints);
        WorkManager.getInstance().enqueue(fetcher.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_activity_menu,menu);
        MenuItem item = menu.findItem(R.id.superpermission);
        userType = preference.getInt(SettingsActivity.KEY_USER_TYPE,Constants.USER_TYPE.Unkown);
        if(userType == Constants.USER_TYPE.Unkown){
            item.setTitle(R.string.login);
        }else{
            item.setTitle(R.string.log_out);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void showLoginDialog(){
        //LoginDialogFragment fragment = LoginDialogFragment.newInstance(this);
        //android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        //android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        //android.support.v4.app.Fragment prev = fm.findFragmentByTag("pin_dialog");
        //if (prev != null) {
        //    ft.remove(prev);
       // }
        //ft.addToBackStack(null);
        //fragment.show(ft, "pin_dialog");
        if(auth.getCurrentUser()==null){
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .setLogo(R.drawable.logo)
                            .setTheme(R.style.AppTheme)
                            .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.superpermission: {
                if(userType!=Constants.USER_TYPE.Unkown) {
                    preference.edit().putInt(SettingsActivity.KEY_USER_TYPE, Constants.USER_TYPE.Unkown).apply();
                    if (auth.getCurrentUser() != null) {
                        auth.signOut();
                    }
                    finish();
                }else {
                    showLoginDialog();
                }

                break;
            }
            case R.id.useful_links: {
                Intent intent = new Intent(this, UsefulLinksActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.about_us: {
                Intent intent = new Intent(this, AboutUsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.share: {
                String packageName = getPackageName();
                String shareURl = "https://play.google.com/store/apps/details?id=" + packageName;
                Intent txtIntent = new Intent(android.content.Intent.ACTION_SEND);
                txtIntent.setType("text/plain");
                txtIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareURl);
                startActivity(Intent.createChooser(txtIntent, "BUC"));
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void setTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction= manager.beginTransaction();
        switch (item.getItemId()) {
            case R.id.home: {
                Fragment fragment = getCurrentFragment();
                if (fragment != null && fragment instanceof HomeFragment) {
                    return false;
                }
                setTitle(getResources().getString(R.string.home));
                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setUserType(userType);
                transaction.replace(R.id.fragment_container, homeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }
            case R.id.announcement: {
                Fragment fragment = getCurrentFragment();
                if (fragment != null && fragment instanceof AnnouncementFragment) {
                    return false;
                }
                setTitle(getResources().getString(R.string.announcements));
                AnnouncementFragment announcementFragment = new AnnouncementFragment();
                announcementFragment.setUserType(userType);
                transaction.replace(R.id.fragment_container,announcementFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }

            case R.id.quarterlies: {
                Fragment fragment = getCurrentFragment();
                if (fragment != null && fragment instanceof QuarterliesFragment) {
                    return false;
                }
                setTitle(getResources().getString(R.string.quarterlies));
                transaction.replace(R.id.fragment_container, new QuarterliesFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }

            case R.id.request: {
                Fragment fragment = getCurrentFragment();
                if (fragment != null && fragment instanceof RequestFragment) {
                    return false;
                }
                setTitle(userType == Constants.USER_TYPE.Chaplain?getResources().getString(R.string.requests):
                        getResources().getString(R.string.my_requests));
                RequestFragment requestFragment = new RequestFragment();
                requestFragment.setUserType(userType);
                transaction.replace(R.id.fragment_container,requestFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }
            case R.id.sermon: {
                Fragment fragment = getCurrentFragment();
                if (fragment != null && fragment instanceof SermonsFragment) {
                    return false;
                }
                setTitle(getResources().getString(R.string.sermon));
                transaction.replace(R.id.fragment_container, new SermonsFragment());
                transaction.addToBackStack(null);
                transaction.commit();

                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public Fragment getCurrentFragment(){
        Fragment fragment =getFragmentManager().findFragmentById(R.id.fragment_container);
        return fragment;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                preference.edit().putInt(SettingsActivity.KEY_USER_TYPE,Constants.USER_TYPE.Congregant).apply();

                invalidateOptionsMenu();
                LogUtils.d(TAG,"onActivity result called request code "+requestCode);
                Fragment fragment =getFragmentManager().findFragmentById(R.id.fragment_container);
                if(fragment!=null){
                    fragment.onActivityResult(requestCode,resultCode,data);
                }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(string);
        builder.setPositiveButton(R.string.ok,null);
        builder.show();

    }


    @Override
    public void onMoreVideosClicked() {
        navigation.setSelectedItemId(R.id.sermon);
        FragmentTransaction transaction= manager.beginTransaction();
        setTitle(getResources().getString(R.string.sermon));
        transaction.replace(R.id.fragment_container, new SermonsFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onMoreBlogsClicked() {
        Intent intent = new Intent(this,BlogsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLoginSuccess() {
        invalidateOptionsMenu();
        userType = preference.getInt(SettingsActivity.KEY_USER_TYPE,Constants.USER_TYPE.Unkown);
        Fragment fragment = getCurrentFragment();
        if(fragment instanceof AnnouncementFragment){
            ((AnnouncementFragment)fragment).setUserType(userType);
        }else if(fragment instanceof RequestFragment){
            ((RequestFragment)fragment).setUserType(userType);
        }
    }

    @Override
    public void onRequestLogin() {
        showLoginDialog();
    }
}
