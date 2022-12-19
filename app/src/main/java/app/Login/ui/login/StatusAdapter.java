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

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    private List<String> usernameList = new ArrayList<>();
    private List<HashMap<String, String>> userList;
    private String user;

    public void updateItems(List<String> usernameList, List<HashMap<String, String>> userList, String user) {
        this.usernameList = usernameList;
        this.userList = userList;
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
        holder.bindView(usernameList.get(position));
    }

    @Override
    public int getItemCount() {
        return usernameList.size();
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
            popupMenu.inflate(R.menu.status_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Intent intent;
            int eventId = -1;
            String username = "";
            int hashMapIndex = usernameList.indexOf(nameTextView.getText().toString());
            eventId = Integer.parseInt(userList.get(hashMapIndex).get("event_id"));
            username = userList.get(hashMapIndex).get("username");

            switch (item.getItemId()) {
                case R.id.remove_user:
                    return removeUser(username, eventId);

                default:
                    return false;
            }
        }

        private boolean removeUser(String username, int eventId) {
            URL url;
            HttpURLConnection con = null;
            String eventChosen = "https://kdy25u7ek3.execute-api.us-east-1.amazonaws.com/dev/rsvp/delete";
            // Get request for that particular event.
            try {
                url = new URL(eventChosen);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("DELETE");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                String jsonInputString = "{\"username\": \"" + username;
                jsonInputString += "\",\n \"event_id\": \"" + eventId + "\"}";

                Log.e("View Event", jsonInputString);

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

                if(!eventInfo.equals("{\"Status\":\"Success\"}")) {
                    Toast.makeText(nameTextView.getContext(), "Permissions lacked to delete.", Toast.LENGTH_LONG).show();
                }
                Log.e("View Event", eventInfo);

            }catch(Exception e) {
                if(e != null) {
                    Log.e("View Event", e.getClass().getSimpleName());
                    Log.e("View Event", e.getMessage());
                } else {
                    Log.e("View Event", "Null exception");
                }
            } finally {
                if(con != null) {
                    con.disconnect();
                }
            }

            Intent intent = new Intent(nameTextView.getContext(), ViewEventActivity.class);
            intent.putExtra("username", user);
            intent.putExtra("eventId", ""+eventId);
            nameTextView.getContext().startActivity(intent);

            return true;
        }
        URL url;
        HttpURLConnection con = null;
        String userChosen = "";
    }
}
