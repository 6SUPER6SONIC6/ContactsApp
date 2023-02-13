package com.supersonic.contactsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Model.Contact;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Contact> contacts;
    private MainActivity mainActivity;


    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView firstNameTextView;
        public TextView secondNameTextView;
        public TextView emailTextView;
        public TextView phoneNumberTextView;

        public MyViewHolder(View view){
            super(view);

            firstNameTextView = view.findViewById(R.id.firstNameTextView);
            secondNameTextView = view.findViewById(R.id.secondNameTextView);
            emailTextView = view.findViewById(R.id.emailTextView);
            phoneNumberTextView = view.findViewById(R.id.phoneNumberTextView);

        }
    }

    public ContactsAdapter(Context context, ArrayList<Contact> contacts, MainActivity mainActivity) {
        this.context = context;
        this.contacts = contacts;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final Contact contact = contacts.get(position);

        holder.firstNameTextView.setText(contact.getFirstName());
        holder.secondNameTextView.setText(contact.getSecondName());
        holder.emailTextView.setText(contact.getEmail());
        holder.phoneNumberTextView.setText(contact.getPhoneNumber());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.addAndEditContacts(true, contact, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }




}
