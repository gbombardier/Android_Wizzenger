package com.bombardier_gabriel.wizzenger.fragments;

/**
 * Created by gabb_ on 2018-03-14.
 * Inspired by: http://www.androidbegin.com/tutorial/android-dialogfragment-tutorial/
 */

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bombardier_gabriel.wizzenger.R;
import com.bombardier_gabriel.wizzenger.database.DatabaseProfile;
import com.google.firebase.auth.FirebaseAuth;

public class InputFragment extends DialogFragment {
    public static String email;
    private EditText emailZone;
    private Button validateButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_alert_email, container, false);

        Bundle mArgs = getArguments();
        final String action = mArgs.getString("action");

//        getDialog().setTitle(action);


        emailZone = (EditText) rootView.findViewById(R.id.alert_contact_email);
        validateButton = (Button) rootView.findViewById(R.id.alert_button);

        //Action lorsque l'on clique sur valider
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailZone.getText().toString();

                if(action.equals("ajouter")){
                    DatabaseProfile.getInstance().writeContact(FirebaseAuth.getInstance().getCurrentUser(), email, getActivity());
                }else if(action.equals("supprimer")){
                    DatabaseProfile.getInstance().removeContact(FirebaseAuth.getInstance().getCurrentUser(), email, getActivity());
                }
                dismiss();
            }
        });

        return rootView;
    }
}
