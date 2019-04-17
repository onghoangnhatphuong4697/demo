package com.framgia.music.screen.testDB;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.framgia.music.R;
import com.framgia.music.data.model.Account;
import com.framgia.music.screen.main.MainActivity;
import com.framgia.music.utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditTextUsername, mEditTextPassword, mEditTextNameRG, mEditTextPassRG,
            mEditTextEmail, mEditTextAddress;
    private Button mButton, mButtonRegister;
    private List<Account> listAccount = new ArrayList<>();
    private boolean isCheck, isUp = true;
    private TextView mTextView;
    private ConstraintLayout login, register, bigLayout;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        anhxa();
        initView();
        handleEvent();
    }

    private void initView() {
        boolean isCheckLogin = getIntent().getBooleanExtra(Constant.BUNDLE_CHECK_LOGIN, true);
        if (!isCheckLogin) {
            register.setVisibility(View.VISIBLE);
            mTextView.setText("Login");
            login.setVisibility(View.INVISIBLE);
        }
    }

    private void handleEvent() {
        mButton.setOnClickListener(v -> {
            checkValidate();
        });
        mButtonRegister.setOnClickListener(v -> {
            register(mEditTextNameRG.getText().toString(), mEditTextPassRG.getText().toString(),
                    mEditTextEmail.getText().toString(), mEditTextAddress.getText().toString());
        });
        mTextView.setOnClickListener(v -> {
            boolean isCheckLogin = getIntent().getBooleanExtra(Constant.BUNDLE_CHECK_LOGIN, true);

            if (!isCheckLogin) {
                onSlideViewButtonClick(register, login);
            } else {
                onSlideViewButtonClick(login, register);
            }
        });
        bigLayout.setOnClickListener(view -> hideKeyboardFrom(this, view));
        login.setOnClickListener(view -> hideKeyboardFrom(this, view));
        register.setOnClickListener(view -> hideKeyboardFrom(this, view));
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void register(String name, String pass, String address, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);

        user.put("pass", pass);
        user.put("address", address);
        user.put("email", email);

        db.collection("user")
                .document(name)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("phuongds", "thanh cong1");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("phuongds", "that bai" + e.getMessage());
                    }
                });
    }

    private void anhxa() {
        mEditTextUsername = findViewById(R.id.ed_username);
        mEditTextPassword = findViewById(R.id.ed_password);
        mButton = findViewById(R.id.bt_login);
        mTextView = findViewById(R.id.tv_tranfer);
        login = findViewById(R.id.layout_login);
        register = findViewById(R.id.layout_register);
        mButtonRegister = findViewById(R.id.bt_sign_up);
        bigLayout = findViewById(R.id.big_layout);
        mEditTextNameRG = findViewById(R.id.ed_username_rg);
        mEditTextPassRG = findViewById(R.id.ed_password_rg);
        mEditTextAddress = findViewById(R.id.address);
        mEditTextEmail = findViewById(R.id.email);
    }

    private void checkValidate() {
        if (mEditTextUsername.getText() == null || mEditTextPassword.getText() == null) {
            Snackbar.make(findViewById(R.id.bt_login), "Vui lòng điền đầy đủ thông tin !! ",
                    Snackbar.LENGTH_SHORT).show();
        } else {
            getListItem();
        }
    }

    private void getListItem() {
        Intent intent = new Intent(this, MainActivity.class);
        db.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot doc : task.getResult()) {
                    if (doc.exists()) {
                        // convert document to POJO
                        Account mAccount112 = doc.toObject(Account.class);
                        Log.d("phuong123",mAccount112.getName() + "123123");
                    }
                   

                    Account mAccount = new Account(doc.getString("name"), doc.getString("pass"));

                    Log.d("phuong123",mAccount.getAddress() +"123123213");
                    listAccount.add(mAccount);
                }
                login(new Account(mEditTextUsername.getText().toString(),
                        mEditTextPassword.getText().toString()));
                getUserDetail();
                if (isCheck) {
                    Snackbar.make(findViewById(R.id.bt_login), "Đăng nhập thành công !!",
                            Snackbar.LENGTH_SHORT).show();
                    startActivity(MainActivity.newInstance(LoginActivity.this, true));
                } else {
                    Snackbar.make(findViewById(R.id.bt_login), "Đăng nhập thất bại !!",
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("phuongdz", " fail roi cu !!");
            }
        });
    }

    private void getUserDetail() {
        db.collection("user")
                .document(mEditTextUsername.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            //Account account = documentSnapshot.toObject(Account.class);
                            SharedPreferences sharedPreferences =
                                    getSharedPreferences(Constant.USER_DETAIL,
                                            Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constant.USER_NAME_,
                                    documentSnapshot.getString("name"));
                            editor.putString(Constant.USER_PASS,
                                    documentSnapshot.getString("pass"));
                            editor.putString(Constant.USER_EMAIL,
                                    documentSnapshot.getString("email"));
                            editor.putString(Constant.USER_ADDRESS,
                                    documentSnapshot.getString("address"));

                            editor.putBoolean(Constant.BOOLEAN_SIGN_IN, true);
                            editor.apply();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void login(Account account) {
        int i;
        for (i = 0; i < listAccount.size(); i++) {
            if (account.getName().equals(listAccount.get(i).getName()) && account.getPass()
                    .equals(listAccount.get(i).getPass())) {
                isCheck = true;
                return;
            } else {
                isCheck = false;
            }
        }
    }

    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        // animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view) {
        view.setVisibility(View.GONE);

        TranslateAnimation animate = new TranslateAnimation(0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        // animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void onSlideViewButtonClick(View view, View view2) {
        if (isUp) {
            slideDown(view);
            slideUp(view2);
            mTextView.setText("Login");
            view.setClickable(false);
            view2.setClickable(true);
        } else {
            slideUp(view);
            slideDown(view2);
            view.setClickable(true);
            view2.setClickable(false);
            mTextView.setText("Register new account");
        }
        isUp = !isUp;
    }

    public static Intent newInstance(Context context, Boolean isCheckLogin) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(Constant.BUNDLE_CHECK_LOGIN, isCheckLogin);
        return intent;
    }

    public static Intent LogOut(Context context, Boolean isCheckLogOut) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(Constant.IS_CHECK_LOCK_OUT, isCheckLogOut);
        return intent;
    }
}
