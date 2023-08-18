package com.example.budgetkitaapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.map.saveLocation.userLocation;
import com.example.budgetkitaapp.map.viewLocation.viewLocation;

import java.util.List;

public class UserLocationAdapter extends RecyclerView.Adapter<UserLocationAdapter.ViewHolder> {
    private List<userLocation> locationList;
    private Context context;
    private String source;
    private LocationClickListener locationClickListener;


    public UserLocationAdapter(Context context) {
        this.context = context;
    }

    public void setLocationClickListener(LocationClickListener listener) {
        this.locationClickListener = listener;
    }

    public void setLocationList(List<userLocation> locationList) {
        this.locationList = locationList;
        notifyDataSetChanged();
    }

    public void setSource(String source) {
        this.source = source;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        userLocation location = locationList.get(position);
        holder.bind(location);

        // Set OnClickListener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle item click and send location details to the next activity
                Context context = view.getContext();

                if (source.equals("expenseFragment") && locationClickListener != null) {
                    locationClickListener.onLocationClick(location.getLocationName());
                }else if(source.equals("otherFragment")){
                    Intent intent = new Intent(context, viewLocation.class);
                    intent.putExtra("locationID", location.getLocationID());
                    intent.putExtra("locationName", location.getLocationName());
                    intent.putExtra("locationDetail", location.getLocationDetail());
                    intent.putExtra("latitude", location.getLatitude());
                    intent.putExtra("longitude", location.getLongitude());
                    intent.putExtra("source", source);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList != null ? locationList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView locationNameTextView;
        private TextView locationDetailTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationNameTextView = itemView.findViewById(R.id.locationNameTextView);
            locationDetailTextView = itemView.findViewById(R.id.locationDetailTextView);
        }

        public void bind(userLocation location) {
            // Bind the location data to the ViewHolder's views
            locationNameTextView.setText(location.getLocationName());
            locationDetailTextView.setText("Detail: "+location.getLocationDetail());
        }

    }

    public interface LocationClickListener {
        void onLocationClick(String locationName);
    }


}
