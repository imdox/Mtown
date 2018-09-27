package com.mtown.app.support;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
	private static final String EMAIL_MSG = "That doesn't seem right";
	private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String PHONE_MSG = "A 10-digit number please!";
	private static final String PHONE_REGEX = "\\d{0}-\\d{9}";//"^((\\+91-?)|0)?[0-9]{10}$";//
	private static final String Mobile_REGEX = "^((\\+91-?)|0)?[0-9]{10}$";//
	private static final String REQUIRED_MSG = "You missed this one!";

	public static boolean hasText(EditText editText) {
		String text = editText.getText().toString().trim();
		editText.setError(null);
		if (text.length() == 0) {
			editText.setError(REQUIRED_MSG);
			return false;
		}
		return true;
	}

	public static boolean hasMobileText(EditText editText) {
		String text = editText.getText().toString().trim();
		editText.setError(null);
		if (text.length() == 0 || text.length()<10) {
			editText.setError(PHONE_MSG);
			return false;
		}
		return true;
	}

	public static boolean isEmailAddress(EditText editText, boolean required) {
		return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required);
	}

	public static boolean isPhoneNumber(EditText editText, boolean required) {
		return isValid(editText, PHONE_REGEX, PHONE_MSG, required);
	}
	
	public static boolean isPhoneLoginNumber(EditText editText, boolean required) {
		return isValidLogin(editText, Mobile_REGEX, PHONE_MSG, required);
	}
	
	public static boolean isValidLogin(EditText editText, String regex,
									   String errMsg, boolean required) {
		try {
			String text = editText.getText().toString().trim();
			editText.setError(null);

			if (required && !hasText(editText))
				return false;
			if (required && !Pattern.matches(regex, text)) {
				editText.setError(errMsg);
				return false;
			}
		} catch (Exception e) {
		}

		return true;
	}

	public static boolean isValid(EditText editText, String regex,
								  String errMsg, boolean required) {
		try {
			String text = editText.getText().toString().trim();
			editText.setError(null);
			if(text.length()<10) {
				editText.setError(EMAIL_MSG);
				return false;
			}
				
			if (required && !hasText(editText)){
				editText.setError(REQUIRED_MSG);
				return false;
			}
			if (required && !Pattern.matches(regex, text)) {
				editText.setError(errMsg);
				return false;
			}
				
		} catch (Exception e) {
		}

		return true;
	}

	private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
	private static Pattern pattern = Pattern.compile(EMAIL_PATTERN);
	private static Matcher matcher;

	public static boolean validateEmail(String email) {
		matcher = pattern.matcher(email);
		return matcher.matches();
	}
}