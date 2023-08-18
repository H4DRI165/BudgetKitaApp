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
import com.example.budgetkitaapp.TransactionHistory;
import com.example.budgetkitaapp.transaction.viewTransaction.TransactionDetail;

import java.util.List;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder> {

    private Context context;
    private List<TransactionHistory> transactionList;

    public TransactionHistoryAdapter(Context context, List<TransactionHistory> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction_history, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionHistory history = transactionList.get(position);
        String entryText = history.getEntry() + "  " + history.getDate();
        holder.entry_tv.setText(entryText);

        if (history.getEntry().equals("Income")) {
            holder.income_tv.setText("RM" + history.getIncomeValue());
            holder.income_tv.setTypeface(null, Typeface.BOLD);
            holder.income_tv.setTextColor(ContextCompat.getColor(context, R.color.incomeColor));
            holder.expense_tv.setText("");
            holder.expense_tv.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        } else if (history.getEntry().equals("Expense")) {
            holder.income_tv.setText("");
            holder.income_tv.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            holder.expense_tv.setText("RM" + history.getExpenseValue());
            holder.expense_tv.setTypeface(null, Typeface.BOLD);
            holder.expense_tv.setTextColor(ContextCompat.getColor(context, R.color.expenseColor));
        }

        // Set the entry text color to black
        holder.entry_tv.setTextColor(ContextCompat.getColor(context, android.R.color.black));

        // Set the background color to white for the whole item view
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView entry_tv, income_tv, expense_tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            entry_tv = itemView.findViewById(R.id.entry_tv);
            income_tv = itemView.findViewById(R.id.income_tv);
            expense_tv = itemView.findViewById(R.id.expense_tv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        TransactionHistory history = transactionList.get(position);
                        String incomeId = history.getIncomeId();
                        String expenseId = history.getExpenseId();

                        // Start the next activity and pass the transaction details, incomeId, and expenseId
                        Intent intent = new Intent(context, TransactionDetail.class);
                        intent.putExtra("entry", history.getEntry());
                        intent.putExtra("date", history.getDate());
                        intent.putExtra("incomeValue", history.getIncomeValue());
                        intent.putExtra("expenseValue", history.getExpenseValue());
                        intent.putExtra("incomeId", incomeId);
                        intent.putExtra("expenseId", expenseId);
                        context.startActivity(intent);

                    }
                }
            });

        }
    }
}



