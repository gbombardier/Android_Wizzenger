package com.bombardier_gabriel.wizzenger.database;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.bombardier_gabriel.wizzenger.adapters.ContactsAdapter;
import com.bombardier_gabriel.wizzenger.model.Contact;
import com.bombardier_gabriel.wizzenger.model.Conversation;
import com.bombardier_gabriel.wizzenger.model.Message;
import com.bombardier_gabriel.wizzenger.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by gabb_ on 2018-01-08.
 * Inspired by Google Firebase examples
 */

public class DatabaseProfile {
    private static final String TAG = "DatabaseProfile";
    private static DatabaseProfile instance;
    private DatabaseReference usersDatabase, contactsDatabase, convosDatabase;
    private User user;
    private Context context;
    DatabaseReference myRef;
    private boolean persEnabled = false;

    private DatabaseProfile(final Context context) {
        this.context = context;

        if(persEnabled == false){
            persEnabled = true;
            FirebaseDatabase.getInstance().setPersistenceEnabled(false);
        }

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        usersDatabase = rootRef.child("users");
        contactsDatabase = FirebaseDatabase.getInstance().getReference("contacts");
        convosDatabase = FirebaseDatabase.getInstance().getReference("conversations");
    }

    public static void init(final Context context) {
        instance = new DatabaseProfile(context);
    }

    public static DatabaseProfile getInstance() {
        if(instance == null) {
            throw new IllegalStateException("You must init the database in the application class.");
        }

        return instance;
    }

    //Pour écrire un usager dans la base de données
    public void writeUser(User user){
        this.user = user;

        String key =  usersDatabase.push().getKey();
        usersDatabase.child(key).child("id").setValue(key);
        usersDatabase.child(key).child("username").setValue(user.getUsername());
        usersDatabase.child(key).child("email").setValue(user.getEmail());
        usersDatabase.child(key).child("phone").setValue(user.getPhone());
        usersDatabase.child(key).child("avatar").setValue(user.getPhotoUrl());
//        addProfileEventListener();
    }

    //Pour créer un contact dans la base de données
    public void writeContact(final FirebaseUser currentUser, final String contactEmail, final Activity currentActivity){
        final String key =  contactsDatabase.push().getKey();

        usersDatabase.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<User>();
                boolean trouveUser = false;
                boolean trouveNouveau = false;
                String emailUser="", emailNouveau="";

                for (com.google.firebase.database.DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {
                    User user = userDataSnapshot.getValue(User.class);
                    users.add(user);
                }

                for (User user : users){
                    if(user.getEmail().equals(currentUser.getEmail())){
                        emailUser = user.getEmail();
                        trouveUser = true;
                    }
                    if(user.getEmail().equals(contactEmail)){
                        emailNouveau = user.getEmail();
                        trouveNouveau = true;
                    }
                }

                if(trouveUser == false || trouveNouveau == false){
                    Toast.makeText(currentActivity, "Erreur, l'usager n'existe pas", Toast.LENGTH_LONG).show();
                }else{
                    contactsDatabase.child(key).child("id").setValue(key);
                    contactsDatabase.child(key).child("userID").setValue(emailUser);
                    contactsDatabase.child(key).child("contactID").setValue(emailNouveau);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}});
    }

    //Pour supprimer un contact dans la base de données (seulement le lien, pas le user)
    public void removeContact(final FirebaseUser currentUser, final String contactEmail, final Activity currentActivity){
        contactsDatabase.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Contact> contacts = new ArrayList<Contact>();
                boolean trouveUser = false;
                boolean trouveNouveau = false;

                for (com.google.firebase.database.DataSnapshot contactDataSnapshot : dataSnapshot.getChildren()) {
                    Contact contact = contactDataSnapshot.getValue(Contact.class);
                    contacts.add(contact);
                }

                for (Contact contact : contacts){
                    String plotte1 = currentUser.getEmail();
                    String plotte2 = contactEmail;
                    if(contact.getUserID().equals(currentUser.getEmail()) && contact.getContactID().equals(contactEmail)){
                        trouveUser = true;
                        trouveNouveau = true;
                        contactsDatabase.child(contact.getId()).removeValue();
                    }else if(contact.getUserID().equals(contactEmail) && contact.getContactID().equals(currentUser.getEmail())){
                        trouveUser = true;
                        trouveNouveau = true;
                        contactsDatabase.child(contact.getId()).removeValue();
                    }
               }
                if(trouveUser == false || trouveNouveau == false){
                    Toast.makeText(currentActivity, "Erreur, le contact n'existe pas", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(currentActivity, "Le contact a bien été supprimé.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}});
    }

    //Pour ouvrir une conversation dans la base de données
    public void writeConvoUsers(final FirebaseUser currentUser, final String contactEmail){
        final String key =  contactsDatabase.push().getKey();
        convosDatabase.child(key).child("id").setValue(key);

        usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<User>();
                for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {
                    User user = userDataSnapshot.getValue(User.class);
                    users.add(user);
                }

                for (User user : users){
                    if(user.getEmail().equals(currentUser.getEmail())){
                        convosDatabase.child(key).child("idUser1").setValue(user.getEmail());
                    }
                    if(user.getEmail().equals(contactEmail)){
                        convosDatabase.child(key).child("idUser2").setValue(contactEmail);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }});
    }

    public void updateTextHint(String id, String message){
        Map<String, Object> valUpdate = new HashMap<String, Object>();
        valUpdate.put("textHint", message);
        convosDatabase.child(id).updateChildren(valUpdate);
    }


    public void addFCMToken(final Context context) {
        FirebaseApp.getInstance().getToken(false).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    final String token = task.getResult().getToken();
                    String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    Log.i(TAG, "onFCMFetchSuccessful");
                    if (user.hasFcmTokenChanged(deviceId, token)) {
                        Log.i(TAG, "onFCMFetchSuccessful: user.devices=" + user.getDevicesList().size());
                        usersDatabase.child(user.getUsername()).child("devices").setValue(user.getDevicesList());
                    }
                }
            }
        });
    }
}
