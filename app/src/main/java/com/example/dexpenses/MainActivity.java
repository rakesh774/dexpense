package com.example.dexpenses;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView tvAirCash, tvSolidCash, tvSafeBalance, tvEMITotal;
    private AppDatabase db;
    private TransactionAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View mainLayout = findViewById(R.id.main);
        // applyBlurEffect(mainLayout); // REMOVED: This was blurring the whole app content

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = AppDatabase.getInstance(this);

        tvAirCash = findViewById(R.id.tvAirCash);
        tvSolidCash = findViewById(R.id.tvSolidCash);
        tvSafeBalance = findViewById(R.id.tvSafeBalance);
        tvEMITotal = findViewById(R.id.tvEMITotal);
        CardView cvAir = findViewById(R.id.cvAir);
        CardView cvSolid = findViewById(R.id.cvSolid);
        CardView cvEMI = findViewById(R.id.cvEMISection);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        TextView btnManageEMI = findViewById(R.id.btnManageEMI);

        setupLiquidInteraction(cvAir);
        setupLiquidInteraction(cvSolid);
        setupLiquidInteraction(cvEMI);
        setupLiquidInteraction(fabAdd);

        RecyclerView rvTransactions = findViewById(R.id.rvTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(new ArrayList<>());
        rvTransactions.setAdapter(adapter);

        cvAir.setOnClickListener(v -> showEditBalanceDialog("Air"));
        cvSolid.setOnClickListener(v -> showEditBalanceDialog("Solid"));
        
        initializeBalance();
        updateUI();

        fabAdd.setOnClickListener(v -> showAddExpenseDialog());
        btnManageEMI.setOnClickListener(v -> showAddEMIDialog());
    }

    private void applyBlurEffect(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(RenderEffect.createBlurEffect(40f, 40f, Shader.TileMode.CLAMP));
        }
    }

    private void setupLiquidInteraction(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                    break;
            }
            return false;
        });
    }

    private void initializeBalance() {
        executorService.execute(() -> {
            if (db.balanceDao().getBalance() == null) {
                db.balanceDao().insert(new Balance(0, 0, 15000));
            }
        });
    }

    private void showEditBalanceDialog(String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update " + type + " Balance");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Enter new balance");
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String val = input.getText().toString();
            if (!val.isEmpty()) {
                updateManualBalance(type, Double.parseDouble(val));
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateManualBalance(String type, double newAmount) {
        executorService.execute(() -> {
            Balance b = db.balanceDao().getBalance();
            if (type.equals("Air")) b.setInitialAir(newAmount);
            else b.setInitialSolid(newAmount);
            db.balanceDao().update(b);
            runOnUiThread(this::updateUI);
        });
    }

    private void showAddExpenseDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_expense);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // applyBlurEffect(dialog.getWindow().getDecorView()); // REMOVED: This blurs the dialog content itself
        }

        EditText etAmount = dialog.findViewById(R.id.etAmount);
        EditText etCategory = dialog.findViewById(R.id.etCategory);
        RadioGroup rgAccount = dialog.findViewById(R.id.rgAccount);
        RadioGroup rgType = dialog.findViewById(R.id.rgType);
        CheckBox cbIsLending = dialog.findViewById(R.id.cbIsLending);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            if (amountStr.isEmpty()) return;
            
            double amount = Double.parseDouble(amountStr);
            String category = etCategory.getText().toString();
            String accountType = (rgAccount.getCheckedRadioButtonId() == R.id.rbAir) ? "Air" : "Solid";
            String transType = (rgType.getCheckedRadioButtonId() == R.id.rbSpent) ? "Spent" : "Received";
            boolean isLending = cbIsLending.isChecked();
            
            executorService.execute(() -> {
                db.transactionDao().insert(new Transaction(amount, category, accountType, transType, isLending, System.currentTimeMillis()));
                runOnUiThread(() -> {
                    updateUI();
                    dialog.dismiss();
                });
            });
        });
        dialog.show();
    }

    private void showAddEMIDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New EMI");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_expense, null);
        builder.setView(view);

        EditText etAmount = view.findViewById(R.id.etAmount);
        EditText etName = view.findViewById(R.id.etCategory);
        etName.setHint("EMI Name (e.g., Home Loan)");
        
        view.findViewById(R.id.rgAccount).setVisibility(View.GONE);
        view.findViewById(R.id.cbIsLending).setVisibility(View.GONE);
        Button btnSave = view.findViewById(R.id.btnSave);
        btnSave.setText("Add EMI");

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // applyBlurEffect(dialog.getWindow().getDecorView()); // REMOVED
        }

        btnSave.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            if (amountStr.isEmpty()) return;

            double amount = Double.parseDouble(amountStr);
            String name = etName.getText().toString();

            executorService.execute(() -> {
                db.emiDao().insert(new EMI(name, amount, 1));
                runOnUiThread(() -> {
                    updateUI();
                    dialog.dismiss();
                });
            });
        });
        dialog.show();
    }

    private void updateUI() {
        executorService.execute(() -> {
            Balance b = db.balanceDao().getBalance();
            if (b == null) return;

            double totalEMI = db.emiDao().getTotalEMIAmount();
            double airSpent = db.transactionDao().getAirCashSpent();
            double airReceived = db.transactionDao().getAirCashReceived();
            double solidSpent = db.transactionDao().getSolidCashSpent();
            double solidReceived = db.transactionDao().getSolidCashReceived();
            
            List<Transaction> allTransactions = db.transactionDao().getAllTransactions();

            double currentAir = b.getInitialAir() - airSpent + airReceived;
            double currentSolid = b.getInitialSolid() - solidSpent + solidReceived;
            double safeBalance = currentAir - totalEMI;

            runOnUiThread(() -> {
                tvAirCash.setText("₹" + String.format("%.0f", currentAir));
                tvSolidCash.setText("₹" + String.format("%.0f", currentSolid));
                tvEMITotal.setText("Total: ₹" + String.format("%.0f", totalEMI));
                tvSafeBalance.setText("Safe to Spend: ₹" + String.format("%.0f", safeBalance));
                adapter.setTransactions(allTransactions);
                
                if (safeBalance < 0) {
                    tvSafeBalance.setTextColor(Color.RED);
                } else {
                    tvSafeBalance.setTextColor(Color.parseColor("#007AFF"));
                }
            });
        });
    }
}
