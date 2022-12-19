package app.Login.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.PopupMenu;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.Login.R;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private List<String> titleList = new ArrayList<>();
    private List<HashMap<String, String>> eventList;
    private String user;

    public void updateItems(List<String> titleList, List<HashMap<String, String>>eventList, String user) {
        this.titleList = titleList;
        this.eventList = eventList;
        this.user = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(titleList.get(position));
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener{

        public TextView nameTextView;
        public AppCompatImageButton messageButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textView);
            messageButton = itemView.findViewById(R.id.imageButton);
            messageButton.setOnClickListener(this);
        }

        void bindView(String name) {
            nameTextView.setText(name);
        }

        @Override
        public void onClick(View v) {
            showPopupMenu(v);
        }

        private void showPopupMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.popup_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Intent intent;
            int eventId = -1;
            int hashMapIndex = titleList.indexOf(nameTextView.getText().toString());
            eventId = Integer.parseInt(eventList.get(hashMapIndex).get("id"));
            Log.e("Create Event", "Found Event Id: "+eventId);

            switch (item.getItemId()) {
                case R.id.action_popup_edit:
                    intent = new Intent(nameTextView.getContext(), EditEventActivity.class);
                    intent.putExtra("eventId",""+eventId);
                    intent.putExtra("username", user);
                    nameTextView.getContext().startActivity(intent);

                    return true;
                case R.id.action_popup_delete:
                    return deleteEvent(eventId);

                case R.id.action_popup_view:
                    intent = new Intent(nameTextView.getContext(), ViewEventActivity.class);
                    intent.putExtra("eventId",""+eventId);
                    intent.putExtra("username", user);
                    nameTextView.getContext().startActivity(intent);

                    return true;

                case R.id.action_popup_join:
                    intent = new Intent(nameTextView.getContext(), JoinEvent.class);
                    intent.putExtra("eventId", ""+eventId);
                    intent.putExtra("username", user);
                    nameTextView.getContext().startActivity(intent);

                    return true;

                default:
                    return false;
            }
        }

        private boolean deleteEvent(int eventId) {
            URL url;
            HttpURLConnection con = null;
            String eventChosen = "https://kdy25u7ek3.execute-api.us-east-1.amazonaws.com/dev/event/remove";
            // Get request for that particular event.
            try {
                url = new URL(eventChosen);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("DELETE");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                String jsonInputString = "{\"username\": \"" + user;
                jsonInputString += "\",\n \"id\": \"" + eventId + "\"}";

                Log.e("Create Event", jsonInputString);

                OutputStream out = new BufferedOutputStream(con.getOutputStream());
                out.write(jsonInputString.getBytes());
                out.flush();
                out.close();

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String eventInfo = "";
                String tmp = "";
                while((tmp = in.readLine()) != null) {
                    eventInfo += tmp;
                }
                in.close();

                if(!eventInfo.equals("{\"Status\":\"Deleted Successfully\"}")) {
                    Toast.makeText(nameTextView.getContext(), "Permissions lacked to delete.", Toast.LENGTH_LONG).show();
                }
                Log.e("Create Event", eventInfo);

            }catch(Exception e) {
                if(e != null) {
                    Log.e("Create Event", e.getClass().getSimpleName());
                    Log.e("Create Event", e.getMessage());
                } else {
                    Log.e("Create Event", "Null exception");
                }
            } finally {
                if(con != null) {
                    con.disconnect();
                }
            }

            Intent intent = new Intent(nameTextView.getContext(), EventActivity.class);
            intent.putExtra("username", user);
            nameTextView.getContext().startActivity(intent);


            return true;
        }


    }
}
