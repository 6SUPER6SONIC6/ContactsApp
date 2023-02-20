package com.supersonic.contactsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.supersonic.contactsapp.databinding.ContactListItemBinding;

import java.util.ArrayList;

import Model.Contact;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private Context context;
    private ArrayList<Contact> contacts;
    private MainActivity mainActivity;


    public class ContactViewHolder extends RecyclerView.ViewHolder{

        private ContactListItemBinding contactListItemBinding;

        public ContactViewHolder(ContactListItemBinding contactListItemBinding){
            super(contactListItemBinding.getRoot());

            this.contactListItemBinding = contactListItemBinding;

        }
    }

    public ContactsAdapter(Context context, ArrayList<Contact> contacts, MainActivity mainActivity) {
        this.context = context;
        this.contacts = contacts;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ContactListItemBinding contactListItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.contact_list_item,
                parent,
                false);

        return new ContactViewHolder(contactListItemBinding);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final Contact contact = contacts.get(position);

        holder.contactListItemBinding.setContact(contact);

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
