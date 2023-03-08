package com.sh22.energy_saver_app.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.backend.AuthenticationException;
import com.sh22.energy_saver_app.backend.AuthenticationHandler;
import com.sh22.energy_saver_app.backend.AuthenticationStatus;
import com.sh22.energy_saver_app.backend.BackendException;
import com.sh22.energy_saver_app.backend.BackendInterface;
import com.sh22.energy_saver_app.common.Constants;
import com.sh22.energy_saver_app.common.SH22Utils;
import com.sh22.energy_saver_app.ui.activites.LoginActivity;
import com.sh22.energy_saver_app.ui.activites.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class changeProviderFragment extends Fragment{

        public changeProviderFragment() {
            // Required empty public constructor
        }

        public static com.sh22.energy_saver_app.ui.fragments.LoginFragment newInstance() {
            com.sh22.energy_saver_app.ui.fragments.LoginFragment fragment = new com.sh22.energy_saver_app.ui.fragments.LoginFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_account, container, false);
            ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#62A526"));
            ((MainActivity)getActivity()).getterActionBar().setBackgroundDrawable(colorDrawable);
            ((MainActivity)getActivity()).getterActionBar().setTitle(Html.fromHtml("<center><div><font color='#FFFFFF'>Appliances</font></div></center>"));
            ((MainActivity)getActivity()).getterActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            ((MainActivity)getActivity()).getterActionBar().setCustomView(R.layout.action_bar_account);

            // Populate list to change account data provider
            new Thread(() -> {
                try {
                    ArrayList<String> providers = BackendInterface.GetDataProviders(view.getContext());
                    final String[] current_provider = {BackendInterface.GetUserInfo(view.getContext()).data_provider};
                    Spinner dp_spinner = view.findViewById(R.id.data_provider_spinner);

                    getActivity().runOnUiThread(() -> {
                        ArrayAdapter<String> dp_adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, providers);
                        dp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dp_spinner.setAdapter(dp_adapter);
                        dp_spinner.setSelection(providers.indexOf(current_provider[0]));
                    });

                    dp_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            new Thread(() ->  {
                                if(!providers.get(i).equals(current_provider[0])){
                                    // Update provider
                                    HashMap<String, String> updates = new HashMap<>();
                                    updates.put("data_provider", providers.get(i));
                                    try {
                                        BackendInterface.UpdateUserInfo(view.getContext(), updates);

                                        // When the data provider is changed, we want to invalidate the cache
                                        BackendInterface.ClearCache();
                                        current_provider[0] = providers.get(i);

                                    } catch (AuthenticationException e) {
                                        SH22Utils.Logout(view.getContext());
                                        e.printStackTrace();
                                    } catch (BackendException e) {
                                        SH22Utils.ToastException(view.getContext(), e.reason);
                                        e.printStackTrace();
                                    }


                                }
                            }).start();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                } catch (AuthenticationException e) {
                    SH22Utils.Logout(view.getContext());
                    e.printStackTrace();
                } catch (BackendException e) {
                    SH22Utils.ToastException(view.getContext(), e.reason);
                    e.printStackTrace();
                }
            }).start();

            // Handle delete account button
            view.findViewById(R.id.delete_account_button).setOnClickListener((View v) -> {
                new Thread(() -> {
                    try {
                        BackendInterface.DeleteAccount(v.getContext());
                        SH22Utils.Logout(v.getContext());
                    } catch (AuthenticationException e) {
                        SH22Utils.Logout(v.getContext());
                        e.printStackTrace();
                    } catch (BackendException e) {
                        SH22Utils.ToastException(v.getContext(), "There was an error deleting your account");
                        e.printStackTrace();
                    }
                }).start();
            });
            return view;
        }
    }
