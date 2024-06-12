package com.example.budgetkitaapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetkitaapp.R;
import com.example.budgetkitaapp.debt.viewDetail.debtDetail;
import com.example.budgetkitaapp.debt.debtClass.Debt;

import java.util.List;

public class DebtAdapter extends RecyclerView.Adapter<DebtAdapter.DebtViewHolder> {
    private Context context;
    private List<Debt> debtList;

    public DebtAdapter(Context context, List<Debt> debtList) {
        this.context = context;
        this.debtList = debtList;
    }

    @NonNull
    @Override
    public DebtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_debt, parent, false);
        return new DebtViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DebtViewHolder holder, int position) {
        Debt debt = debtList.get(position);
        String entryText = debt.getDebtName() + "\n" + debt.getDebtDate();
        String debtInfo = debt.getDebtStatus();

        holder.debtName.setText(entryText);

        holder.debtTotal.setText("RM " + debt.getDebtTotal());
        holder.debtTotal.setTypeface(null, Typeface.BOLD);
        holder.debtTotal.setTextColor(ContextCompat.getColor(context, R.color.expenseColor));

        if(debtInfo.equals("Not Paid")){
            holder.debtStatus.setText(debt.getDebtStatus());
            holder.debtStatus.setTypeface(null, Typeface.BOLD);
            holder.debtStatus.setTextColor(ContextCompat.getColor(context, R.color.expenseColor));
        }else{
            holder.debtStatus.setText(debt.getDebtStatus());
            holder.debtStatus.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        // Set the entry text color to black
        holder.debtName.setTextColor(ContextCompat.getColor(context, android.R.color.black));

        // Set the background color to white for the whole item view
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
    }

    @Override
    public int getItemCount() {
        return debtList.size();
    }

    public class DebtViewHolder extends RecyclerView.ViewHolder {
        TextView debtName, debtTotal, debtStatus;

        public DebtViewHolder(@NonNull View itemView) {
            super(itemView);

            debtName = itemView.findViewById(R.id.tvDebtName);
            debtTotal = itemView.findViewById(R.id.tvDebtTotal);
            debtStatus = itemView.findViewById(R.id.tvDebtStatus);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        Debt debt = debtList.get(position);
                        String debtId = debt.getDebtId();

                        // Start the next activity and pass the debt details
                        Intent intent = new Intent(context, debtDetail.class);
                        intent.putExtra("entry", debt.getDebtName());
                        intent.putExtra("date", debt.getDebtDate());
                        intent.putExtra("debtAmount", debt.getDebtTotal());
                        intent.putExtra("datePaid", debt.getDebtDatePaid());
                        intent.putExtra("debtId", debtId);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
