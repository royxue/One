package com.royxue.one;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Button reg = (Button) findViewById(R.id.reg);
		Button log = (Button) findViewById(R.id.log);
		final EditText email = (EditText) findViewById(R.id.email);
		final EditText pwd = (EditText) findViewById(R.id.pwd);
		final TextView note = (TextView) findViewById(R.id.note);
		final String reg_email[] = null;
		final String reg_pwd[] = null ;
		final String log_email[] = null;
		final String log_pwd[] = null;
		
		reg.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v) {
						reg_email[0] = email.getText().toString();
						reg_pwd[0] = pwd.getText().toString();
						email.setText(" ");
						pwd.setText(" ");
						note.setText("��ϲ,�ɹ�ע���ʺ�!");
						//Toast.makeText(this, R.string.reg , 2000).show();
					}
				});
		
		log.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v){
				log_email[0] = email.getText().toString();
				log_pwd[0] = pwd.getText().toString();
				if (reg_email[0] == null | log_email[0] != reg_email[0]  )
					{note.setText("�û�������, ����ע��");}
				else
				{
					if (log_pwd[0] != reg_pwd[0])
					{	
						note.setText("�������,����������");
						pwd.setText(" ");
					}
					else
					{	
						note.setText("�ɹ���¼,��ת��");
						Intent intent =  new Intent(Login.this, Entrance.class);
						startActivity(intent);
					}
					
				}
			
			}
			
		});
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
