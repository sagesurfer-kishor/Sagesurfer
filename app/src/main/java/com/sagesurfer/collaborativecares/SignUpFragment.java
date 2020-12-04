package com.sagesurfer.collaborativecares;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.appcompat.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.sagesurfer.captcha.TextCaptcha;
import com.sagesurfer.constant.General;
import com.sagesurfer.datetime.GetTime;
import com.sagesurfer.interfaces.SignUpInterface;
import com.sagesurfer.library.GetCounters;
import com.sagesurfer.snack.ShowSnack;
import com.sagesurfer.snack.ShowToast;
import com.sagesurfer.validator.LoginValidator;
import com.storage.preferences.SignUpPreferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Monika M (monikam@sagesurfer.com)
 *         Created on 16/03/2018
 *         Last Modified on
 */

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = SignUpFragment.class.getSimpleName();

    @BindView(R.id.edittext_signup_role)
    EditText editTextSignupRole;
    @BindView(R.id.edittext_signup_firstname)
    EditText editTextSignupFirstName;
    @BindView(R.id.edittext_signup_lastname)
    EditText editTextSignupLastName;
    @BindView(R.id.edittext_signup_email)
    EditText editTextSignupEmail;
    @BindView(R.id.edittext_signup_username)
    EditText editTextSignupUserName;
    @BindView(R.id.edittext_signup_password)
    EditText editTextSignupPassword;
    @BindView(R.id.edittext_signup_confirm_password)
    EditText editTextSignupConfirmPassword;
    @BindView(R.id.textview_signup_dob)
    TextView textViewSignupDOB;
    @BindView(R.id.textview_signup_security_question)
    TextView textViewSignupSecurityQuestion;
    @BindView(R.id.edittext_signup_security_answer)
    EditText editTextSignupSecurityAnswer;
    @BindView(R.id.imageview_signup_captcha_image)
    ImageView imageViewSignupCaptchaImage;
    @BindView(R.id.button_signup_captcha_reset)
    Button buttonSignupCaptchaReset;
    @BindView(R.id.edittext_signup_captcha)
    EditText editTextSignupCaptcha;
    @BindView(R.id.checkedtextview_signup_terms)
    CheckedTextView checkedTextViewSignupTerms;
    @BindView(R.id.button_signup)
    Button buttonSignup;
    @BindView(R.id.textview_signup_account_already)
    TextView textViewSignupAccountAlready;

    private int mYear = 0, mMonth = 0, mDay = 0;
    private int sYear, sMonth, sDay;
    private String date_of_birth = "";

    private Activity activity;
    private SignUpInterface signUpInterface;

    private TextCaptcha textCaptcha;
    private String question = "";
    private Unbinder unbinder;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        signUpInterface = (SignUpInterface) activity;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, null);
        unbinder = ButterKnife.bind(this, view);

        activity = getActivity();

        if (SignUpPreferences.contains(General.ROLE) && SignUpPreferences.get(General.ROLE) != null) {
            editTextSignupRole.setText(SignUpPreferences.get(General.ROLE));
        }

        if (SignUpPreferences.contains(General.FIRST_NAME) && SignUpPreferences.get(General.FIRST_NAME) != null) {
            editTextSignupFirstName.setText(SignUpPreferences.get(General.FIRST_NAME));
        }

        if (SignUpPreferences.contains(General.EMAIL) && SignUpPreferences.get(General.EMAIL) != null) {
            editTextSignupEmail.setText(SignUpPreferences.get(General.EMAIL));
        }

        VectorDrawableCompat roleDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_user_name, editTextSignupRole.getContext().getTheme());
        editTextSignupRole.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, roleDrawable, null);

        VectorDrawableCompat firstNameDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_user_name, editTextSignupFirstName.getContext().getTheme());
        editTextSignupFirstName.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, firstNameDrawable, null);

        VectorDrawableCompat lastNameDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_user_name, editTextSignupLastName.getContext().getTheme());
        editTextSignupLastName.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, lastNameDrawable, null);

        VectorDrawableCompat emailDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_user_email, editTextSignupEmail.getContext().getTheme());
        editTextSignupEmail.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, emailDrawable, null);

        VectorDrawableCompat userNameDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_user_name, editTextSignupUserName.getContext().getTheme());
        editTextSignupUserName.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, userNameDrawable, null);

        VectorDrawableCompat passwordDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_user_password, editTextSignupPassword.getContext().getTheme());
        editTextSignupPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, passwordDrawable, null);

        VectorDrawableCompat confirmPasswordDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_user_password, editTextSignupConfirmPassword.getContext().getTheme());
        editTextSignupConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, confirmPasswordDrawable, null);

        VectorDrawableCompat dobDrawable = VectorDrawableCompat.create(this.getResources(), R.drawable.vi_calendar, textViewSignupDOB.getContext().getTheme());
        textViewSignupDOB.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, dobDrawable, null);

        editTextSignupFirstName.setOnFocusChangeListener(onFocusChange);
        editTextSignupLastName.setOnFocusChangeListener(onFocusChange);
        editTextSignupEmail.setOnFocusChangeListener(onFocusChange);
        editTextSignupUserName.setOnFocusChangeListener(onFocusChange);
        editTextSignupPassword.setOnFocusChangeListener(onFocusChange);
        editTextSignupConfirmPassword.setOnFocusChangeListener(onFocusChange);

        textViewSignupDOB.setOnClickListener(this);
        buttonSignupCaptchaReset.setOnClickListener(this);
        textViewSignupSecurityQuestion.setOnClickListener(this);
        checkedTextViewSignupTerms.setOnClickListener(this);
        buttonSignup.setOnClickListener(this);
        textViewSignupAccountAlready.setOnClickListener(this);

        setCaptchaImage(false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }



    // set captcha code image
    private void setCaptchaImage(boolean isReset) {
        if (isReset) {
            imageViewSignupCaptchaImage.setImageBitmap(null);
        }
        textCaptcha = new TextCaptcha(600, 150, 4, TextCaptcha.TextOptions.LETTERS_ONLY);
        imageViewSignupCaptchaImage.setImageBitmap(textCaptcha.getImage());
    }

    // remove extra white space from string
    private String removeSpace(String textString) {
        textString = textString.trim();
        textString = textString.replaceAll("\\s+", " ");
        return textString;
    }

    private final View.OnFocusChangeListener onFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.edittext_signup_firstname:
                    editTextSignupFirstName.setText(removeSpace(editTextSignupFirstName.getText().toString()));
                    break;

                case R.id.edittext_signup_lastname:
                    editTextSignupLastName.setText(removeSpace(editTextSignupLastName.getText().toString()));
                    break;

                case R.id.edittext_signup_email:
                    editTextSignupEmail.setText(removeSpace(editTextSignupEmail.getText().toString()));
                    if (editTextSignupEmail.getText().toString().trim().length() > 0)
                        if (!LoginValidator.isEmail(editTextSignupEmail.getText().toString())) {
                            editTextSignupEmail.setError("Invalid Email");
                        }
                    break;

                case R.id.edittext_signup_username:
                    editTextSignupUserName.setText(removeSpace(editTextSignupUserName.getText().toString()));
                    break;

                case R.id.edittext_signup_password:
                    if (editTextSignupPassword.getText().toString().trim().length() > 0)
                        if (!LoginValidator.isPassword(editTextSignupPassword.getText().toString())) {
                            editTextSignupPassword.setError("Invalid Password");
                        }
                    break;

                case R.id.edittext_signup_confirm_password:
                    if (editTextSignupConfirmPassword.getText().toString().trim().length() > 0)
                        if (!LoginValidator.isPassword(editTextSignupConfirmPassword.getText().toString())) {
                            editTextSignupConfirmPassword.setError("Password not matching");
                        }
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textview_signup_dob:
                Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                monthOfYear = (monthOfYear + 1);
                                sDay = dayOfMonth;
                                sMonth = monthOfYear;
                                sYear = year;

                                date_of_birth = sYear + "-" + GetCounters.checkDigit(sMonth) + "-" + GetCounters.checkDigit(sDay);
                                try {
                                    int result = compareDate(mYear + "-" + (mMonth + 1) + "-" + mDay, date_of_birth);
                                    if (result == 1) {
                                        textViewSignupDOB.setText(GetTime.month_DdYyyy(date_of_birth));
                                    } else {
                                        date_of_birth = null;
                                        textViewSignupDOB.setText("");
                                        ShowToast.toast("Invalid Date of Birth",activity.getApplicationContext());
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePicker.show();
                break;

            case R.id.button_signup_captcha_reset:
                setCaptchaImage(true);
                break;

            case R.id.textview_signup_security_question:
                showQuestions();
                break;

            case R.id.checkedtextview_signup_terms:
                if (checkedTextViewSignupTerms.isChecked()) {
                    checkedTextViewSignupTerms.setChecked(false);
                } else {
                    checkedTextViewSignupTerms.setChecked(true);
                }
                break;

            case R.id.button_signup:
                if (checkValidity()) {
                    signUpInterface.doSignUp();
                }
                break;

            case R.id.textview_signup_account_already:
                break;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private int compareDate(String today, String selected_date) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        Date date1 = dateFormat.parse(today);
        Date date2 = dateFormat.parse(selected_date);

        calendar1.setTime(date1);
        calendar2.setTime(date2);

        return calendar1.compareTo(calendar2);
    }

    //check all fields for validity
    private boolean checkValidity() {
        String fist_name = editTextSignupFirstName.getText().toString().trim();
        String last_name = editTextSignupLastName.getText().toString().trim();
        String email = editTextSignupEmail.getText().toString().trim();
        String user_name = editTextSignupUserName.getText().toString().trim();
        String password = editTextSignupPassword.getText().toString();
        String confirm_password = editTextSignupConfirmPassword.getText().toString();

        if (!LoginValidator.isName(fist_name)) {
            editTextSignupFirstName.setError("Invalid First Name");
            return false;
        }
        if (!LoginValidator.isName(last_name)) {
            editTextSignupLastName.setError("Invalid Last Name");
            return false;
        }
        if (!LoginValidator.isEmail(email)) {
            editTextSignupEmail.setError("Invalid Email");
            return false;
        }
        if (!LoginValidator.isUsername(user_name)) {
            editTextSignupUserName.setError("Invalid Username");
            return false;
        }
        if (!LoginValidator.isPassword(password)) {
            editTextSignupPassword.setError("Invalid Password");
            return false;
        }
        if (!password.equalsIgnoreCase(confirm_password)) {
            editTextSignupConfirmPassword.setError("Password not matching");
            return false;
        }
        if (date_of_birth.equalsIgnoreCase("") || date_of_birth.length() <= 0) {
            ShowSnack.viewWarning(textViewSignupDOB, "Invalid  Date of Birth",activity.getApplicationContext());
            return false;
        }
        if (question.length() <= 0) {
            ShowSnack.viewWarning(textViewSignupSecurityQuestion, "Invalid security question",
                    activity.getApplicationContext());
            return false;
        }
        String answer = editTextSignupSecurityAnswer.getText().toString().trim();
        if (answer.length() <= 0) {
            editTextSignupSecurityAnswer.setError("Invalid security question");
            return false;
        }
        if (!checkedTextViewSignupTerms.isChecked()) {
            ShowSnack.viewWarning(textViewSignupSecurityQuestion, "Terms of use not accepted",
                    activity.getApplicationContext());
            return false;
        }
        if (!textCaptcha.checkAnswer(editTextSignupCaptcha.getText().toString().trim())) {
            editTextSignupCaptcha.setError("Captcha doesn't match");
            setCaptchaImage(true);
            return false;
        }

        SignUpPreferences.save(General.FIRST_NAME, fist_name, TAG);
        SignUpPreferences.save(General.LAST_NAME, last_name, TAG);
        SignUpPreferences.save(General.START_DATE, date_of_birth, TAG);
        SignUpPreferences.save(General.EMAIL, email, TAG);
        SignUpPreferences.save(General.USERNAME, user_name, TAG);
        SignUpPreferences.save(General.PASSWORD, password, TAG);
        SignUpPreferences.save("confirm_" + General.PASSWORD, confirm_password, TAG);
        SignUpPreferences.save(General.POLL_QUESTION, question, TAG);
        SignUpPreferences.save(General.POLL_ANSWER, answer, TAG);
        return true;
    }

    //show default security question pop up window
    private void showQuestions() {
        final PopupMenu popup = new PopupMenu(activity, textViewSignupSecurityQuestion);
        popup.getMenuInflater().inflate(R.menu.security_question_list, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.security_question_four) {
                    addQuestion();
                } else {
                    question = item.getTitle().toString();
                    textViewSignupSecurityQuestion.setText(question);
                }
                return true;
            }
        });
        popup.show();
    }

    // open dialog box to add security question
    private void addQuestion() {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.self_goal_details_delete_confirmation);
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView subTitle = (TextView) dialog.findViewById(R.id.textview_selfgoaldetails_deleteconfirmation_subtitle);
        subTitle.setVisibility(View.GONE);

        final EditText inputBox = (EditText) dialog.findViewById(R.id.edittext_selfgoaldetails_deleteconfirmation_inputbox);
        inputBox.setVisibility(View.VISIBLE);
        inputBox.setHint("Type question here....");

        TextView fwdName = (TextView) dialog.findViewById(R.id.textview_selfgoaldetails_deleteconfirmation_title);
        fwdName.setText("Own Question");
        TextView okButton = (TextView) dialog.findViewById(R.id.textview_selfgoaldetails_deleteconfirmation_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputBox.getText().toString().trim();
                if (validQuestion(name, inputBox)) {
                    dialog.dismiss();
                    question = name;
                    textViewSignupSecurityQuestion.setText(question);
                }
            }
        });

        AppCompatImageButton fwdDecline = (AppCompatImageButton)
                dialog.findViewById(R.id.imagebutton_selfgoaldetails_deleteconfirmation_cancel);
        fwdDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // check question for it's valid min/max question
    private boolean validQuestion(String question, EditText editText) {
        if (question.length() <= 0) {
            editText.setError("Min 3 char required");
            return false;
        }
        if (question.length() > 150) {
            editText.setError("Max 150 char allowed");
            return false;
        }
        return true;
    }
}
