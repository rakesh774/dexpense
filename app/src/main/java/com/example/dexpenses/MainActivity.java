package com.example.dexpenses;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity implements EMIAdapter.OnEMIClickListener {

    private TextView tvAirCash, tvSolidCash, tvSafeBalance, tvEMITotal;
    private AppDatabase db;
    private TransactionAdapter adapter;
    private EMIAdapter emiAdapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View mainLayout = findViewById(R.id.main);

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
        CardView cvSafe = findViewById(R.id.cvSafeToSpend);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        TextView btnManageEMI = findViewById(R.id.btnManageEMI);
        TextView btnTransfer = findViewById(R.id.btnTransfer);

        setupLiquidInteraction(cvAir);
        setupLiquidInteraction(cvSolid);
        setupLiquidInteraction(cvEMI);
        setupLiquidInteraction(cvSafe);
        setupLiquidInteraction(fabAdd);

        RecyclerView rvTransactions = findViewById(R.id.rvTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(new ArrayList<>());
        rvTransactions.setAdapter(adapter);

        RecyclerView rvEMIs = findViewById(R.id.rvEMIs);
        rvEMIs.setLayoutManager(new LinearLayoutManager(this));
        emiAdapter = new EMIAdapter(new ArrayList<>(), this);
        rvEMIs.setAdapter(emiAdapter);

        cvAir.setOnClickListener(v -> showEditBalanceDialog("Air"));
        cvSolid.setOnClickListener(v -> showEditBalanceDialog("Solid"));
        
        initializeBalance();
        updateUI();

        fabAdd.setOnClickListener(v -> showAddExpenseDialog());
        btnManageEMI.setOnClickListener(v -> showAddEMIDialog(null));
        btnTransfer.setOnClickListener(v -> showTransferDialog());
    }

    private void setupLiquidInteraction(View view) {
        if (view == null) return;
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
            if (b != null) {
                if (type.equals("Air")) b.setInitialAir(newAmount);
                else b.setInitialSolid(newAmount);
                db.balanceDao().update(b);
                runOnUiThread(this::updateUI);
            }
        });
    }

    private void showAddExpenseDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_expense);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

    private void showAddEMIDialog(EMI existingEMI) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(existingEMI == null ? "Add New EMI" : "Edit EMI");

        View view = getLayoutInflater().inflate(R.layout.dialog_add_expense, null);
        builder.setView(view);

        EditText etAmount = view.findViewById(R.id.etAmount);
        EditText etName = view.findViewById(R.id.etCategory);
        etName.setHint("EMI Name (e.g., Home Loan)");
        
        if (existingEMI != null) {
            etAmount.setText(String.valueOf(existingEMI.getAmount()));
            etName.setText(existingEMI.getName());
        }
        
        view.findViewById(R.id.rgAccount).setVisibility(View.GONE);
        view.findViewById(R.id.cbIsLending).setVisibility(View.GONE);
        view.findViewById(R.id.rgType).setVisibility(View.GONE);
        
        Button btnSave = view.findViewById(R.id.btnSave);
        btnSave.setText(existingEMI == null ? "Add EMI" : "Update EMI");

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnSave.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            if (amountStr.isEmpty()) return;

            double amount = Double.parseDouble(amountStr);
            String name = etName.getText().toString();

            executorService.execute(() -> {
                if (existingEMI == null) {
                    db.emiDao().insert(new EMI(name, amount, 1));
                } else {
                    existingEMI.setName(name);
                    existingEMI.setAmount(amount);
                    db.emiDao().update(existingEMI);
                }
                runOnUiThread(() -> {
                    updateUI();
                    dialog.dismiss();
                });
            });
        });
        dialog.show();
    }

    @Override
    public void onEdit(EMI emi) {
        showAddEMIDialog(emi);
    }

    @Override
    public void onDelete(EMI emi) {
        new AlertDialog.Builder(this)
                .setTitle("Delete EMI")
                .setMessage("Are you sure you want to delete this EMI?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    executorService.execute(() -> {
                        db.emiDao().delete(emi);
                        runOnUiThread(this::updateUI);
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showTransferDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_transfer);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        EditText etAmount = dialog.findViewById(R.id.etTransferAmount);
        RadioGroup rgFrom = dialog.findViewById(R.id.rgTransferFrom);
        TextView tvTo = dialog.findViewById(R.id.tvTransferTo);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirmTransfer);

        rgFrom.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbFromAir) {
                tvTo.setText("Solid Cash");
            } else {
                tvTo.setText("Air Cash");
            }
        });

        btnConfirm.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            if (amountStr.isEmpty()) return;

            double amount = Double.parseDouble(amountStr);
            boolean fromAir = rgFrom.getCheckedRadioButtonId() == R.id.rbFromAir;

            executorService.execute(() -> {
                long now = System.currentTimeMillis();
                String category = fromAir ? "Interchanged - Air to Solid" : "Interchanged - Solid to Air";
                db.transactionDao().insert(new Transaction(amount, category, fromAir ? "Air" : "Solid", "Transfer", false, now));
                db.transactionDao().insert(new Transaction(amount, "Internal Transfer", fromAir ? "Solid" : "Air", "TransferHidden", false, now + 1));
                
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
            List<EMI> allEMIs = db.emiDao().getAllEMIs();
            
            double airSpent = db.transactionDao().getAirCashSpent();
            double airReceived = db.transactionDao().getAirCashReceived();
            double solidSpent = db.transactionDao().getSolidCashSpent();
            double solidReceived = db.transactionDao().getSolidCashReceived();

            List<Transaction> allTransactions = db.transactionDao().getAllTransactions();
            List<Transaction> displayTransactions = new ArrayList<>();
            
            double extraAirIn = 0, extraAirOut = 0, extraSolidIn = 0, extraSolidOut = 0;
            
            for (Transaction t : allTransactions) {
                if (!t.getTransactionType().equals("TransferHidden")) {
                    displayTransactions.add(t);
                }
                if (t.getTransactionType().equals("Transfer")) {
                    if (t.getAccountType().equals("Air")) extraAirOut += t.getAmount();
                    else extraSolidOut += t.getAmount();
                } else if (t.getTransactionType().equals("TransferHidden")) {
                    if (t.getAccountType().equals("Air")) extraAirIn += t.getAmount();
                    else extraSolidIn += t.getAmount();
                }
            }

            double currentAir = b.getInitialAir() - airSpent + airReceived - extraAirOut + extraAirIn;
            double currentSolid = b.getInitialSolid() - solidSpent + solidReceived - extraSolidOut + extraSolidIn;
            double safeBalance = currentAir - totalEMI;

            runOnUiThread(() -> {
                tvAirCash.setText("₹" + String.format("%.0f", currentAir));
                tvSolidCash.setText("₹" + String.format("%.0f", currentSolid));
                tvEMITotal.setText("Total: ₹" + String.format("%.0f", totalEMI));
                tvSafeBalance.setText("₹" + String.format("%.2f", safeBalance));
                adapter.setTransactions(displayTransactions);
                emiAdapter.setEMIs(allEMIs);
                
                if (safeBalance < 0) {
                    tvSafeBalance.setTextColor(Color.parseColor("#FF3B30"));
                } else {
                    tvSafeBalance.setTextColor(Color.parseColor("#007AFF"));
                }
            });
        });
    }
}
