package com.pifss.myway;

import java.net.URI;
import java.text.BreakIterator;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.pifss.myway.Fragment_Login.ForgetPassword;

import android.R.bool;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class Fragment_Register extends Fragment {

	public final static String PREF_NAME = "userInformation";
	private View v;
	private EditText userName;
	private EditText passWord;
	private EditText email;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		v = inflater.inflate(R.layout.fragment_register, null);

		userName = (EditText) v.findViewById(R.id.editTextUsernameReg);
		passWord = (EditText) v.findViewById(R.id.editTextPasswordReg);
		email = (EditText) v.findViewById(R.id.EditTextEmailReg);

		ImageView Register = (ImageView) v
				.findViewById(R.id.imageButtonRegister);

		Register.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (validate()) {
					
					String [] stringArray = {userName.getText().toString()};
					new UsernameAvailable().execute(stringArray);
				}

			}
		});

		return v;
	}

	boolean flag1;
	boolean flag2;
	boolean flag3;

	public boolean validate() {

		if (userName.getText().length() < 5) {
			userName.setError("Username too short");
			flag1 = false;
		} else {
			flag1 = true;
		}

		if (passWord.getText().length() < 8) {
			passWord.setError("Password must be more than 8 Characters");
			flag2 = false;
		} else {
			flag2 = true;
		}

		if (!android.util.Patterns.EMAIL_ADDRESS.matcher(
				email.getText().toString()).matches()) {
			email.setError("Invalid email address");
			flag3 = false;
		} else {
			flag3 = true;
		}

		return flag1 == true && flag2 == true && flag3 == true;
	} // end of method
	
	class Registration extends AsyncTask<User, Void, Void> {

		User user;

		JSONObject jsonData;
		
		@Override
		protected Void doInBackground(User... params) {
			// TODO Auto-generated method stub

			user = params[0];
			
			try {
				URI uri = new URI("http://54.88.107.56:80/MyWayWeb/registerUser");
				System.out.print("helllllllllloooooooooooooomaaarrrr1");
				DefaultHttpClient client = new DefaultHttpClient();
				
				HttpPost postRequest = new HttpPost(uri);
				System.out.print("helllllllllloooooooooooooomaaarrrr2");
				int imageresource = getResources().getIdentifier("@drawable/default_profile_picture.png", "drawable", getActivity().getPackageName());
				Bitmap image = BitmapFactory.decodeResource(getResources(), imageresource);
				System.out.print("helllllllllloooooooooooooomaaarrrr3");
				ArrayList<BasicNameValuePair> arrayList = new ArrayList<BasicNameValuePair>();
				arrayList.add(new BasicNameValuePair("username", params[0].getUsername()));
				arrayList.add(new BasicNameValuePair("password", params[0].getPassword()));
				arrayList.add(new BasicNameValuePair("email", params[0].getEmail()));
				arrayList.add(new BasicNameValuePair("profilepicture", ""));
				//arrayList.add(new BasicNameValuePair("profilepicture", InformationManager.encodeTobase64(image)));
				System.out.print("helllllllllloooooooooooooomaaarrrr4");
				UrlEncodedFormEntity urlEntinty = new UrlEncodedFormEntity(arrayList);
				System.out.print("hellllllllllooooooooooooo");
				System.out.print(urlEntinty.toString());
				postRequest.setEntity(urlEntinty);
				
				HttpResponse response = client.execute(postRequest);

				HttpEntity entity = response.getEntity();
				String jsonString = EntityUtils.toString(entity);

				jsonData = new JSONObject(jsonString);


			} catch (Exception e) {
				
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			Toast.makeText(v.getContext(), "json = " + jsonData.toString(), Toast.LENGTH_LONG).show();
		}
		
	}
	
	class UsernameAvailable extends AsyncTask<String, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			Boolean usernameAvailable = true;
			
			try {
				URI uri = new URI("http://54.88.107.56:80/MyWayWeb/checkUsername");
				
				DefaultHttpClient client = new DefaultHttpClient();
				
				HttpPost postRequest = new HttpPost(uri);
				
				ArrayList<BasicNameValuePair> arrayList = new ArrayList<BasicNameValuePair>();
				arrayList.add(new BasicNameValuePair("username", params[0]));
				
				HttpResponse response = client.execute(postRequest);
				
				HttpEntity entity = response.getEntity();
			    String jsonString = EntityUtils.toString(entity);
			    
			    JSONObject jsonData = new JSONObject(jsonString);
			    
			    if (jsonData.getString("result_code").equals("0"))
			    	usernameAvailable = false;


			} catch(Exception e) {
				
			}

			//Toast.makeText(v.getContext(),"result code = " + jsonData.getString("result_code"), Toast.LENGTH_LONG).show();

			return usernameAvailable;
		}
		
		@Override
		protected void onPostExecute(Boolean usernameAvailable) {
			// TODO Auto-generated method stub
			super.onPostExecute(usernameAvailable);
			
			if (usernameAvailable) {
				
				try {
					//Toast.makeText(v.getContext(), "Register success", Toast.LENGTH_LONG).show();

					User user = new User(userName.getText().toString(),passWord.getText().toString(),email.getText().toString());

					User[] userArray = {user};
					Toast.makeText(v.getContext(), "username = " + user.getUsername(), Toast.LENGTH_LONG).show();
					new Registration().execute(userArray);

					Intent i = new Intent(getActivity(), ProfileHomeActivity.class);

					InformationManager im = new InformationManager(v.getContext());
					im.logIn(getActivity(), userName.getText().toString());

					startActivity(i);
					getActivity().finish();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {
				Toast.makeText(v.getContext(), "Username not available. Try different username", Toast.LENGTH_LONG).show();
			}
		}
		
	}
}