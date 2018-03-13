package com.bombardier_gabriel.wizzenger.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bombardier_gabriel.wizzenger.ContactInfoActivity;
import com.bombardier_gabriel.wizzenger.ConversationActivity;
import com.bombardier_gabriel.wizzenger.LoginActivity;
import com.bombardier_gabriel.wizzenger.R;
import com.bombardier_gabriel.wizzenger.model.Conversation;
import com.bombardier_gabriel.wizzenger.model.User;

import java.util.List;

/**
 * Classe inspirée de https://www.androidhive.info/2016/01/android-working-with-recycler-view/
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    private List<User> contactsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView nomContact;
        public ImageView imageWizz;

        public MyViewHolder(View view) {
            super(view);
            image= (ImageView) view.findViewById(R.id.imgContactContacts);
            nomContact=(TextView) view.findViewById(R.id.nomContact);
            imageWizz = (ImageView) view.findViewById(R.id.imgWizz);
        }
    }


    public ContactsAdapter(List<User> contactsList) {
        this.contactsList = contactsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_one_contact, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        User user = contactsList.get(position);
        holder.image.setImageResource(user.getPhotoUrl());
        holder.nomContact.setText(user.getUsername());
        holder.imageWizz.setImageResource(R.drawable.vibration_icon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ContactInfoActivity.class);
                i.putExtra("contactName", holder.nomContact.getText().toString());
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

}
