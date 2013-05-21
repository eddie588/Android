package com.yinong.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CalculatorActivity extends Activity {
	private String lastOp ="";
	private double lastNumber=0;
	private boolean opClicked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculator);
		TextView editText = (TextView) findViewById(R.id.editText1);
		editText.setText("0");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calculator, menu);
		return true;
	}
	
	public void btnClicked(View view) {
		Button btn = (Button)view;
		TextView editText = (TextView) findViewById(R.id.editText1);
		String currentText = editText.getText().toString();
		String btnText = btn.getText().toString();
		
		// Number button
		if( "0123456789.".contains(btnText) )
		{
			if( opClicked ) {
				currentText = "";
				opClicked = false;
			}
			if( !btnText.equals(".") || !currentText.contains(".") )
			{
				if( currentText.equals("0") )
					currentText = "";
				editText.setText(currentText + btnText);
			}
		}
		
		// Operator button
		if( "+-*/".contains(btnText) ) {
			lastOp = btnText;
			lastNumber = Double.parseDouble(currentText);
			opClicked = true;
		}
		
		if( btnText.equals("=")) {
			double thisNumber = Double.parseDouble(currentText);
			if(lastOp.equals("+"))
			{
				editText.setText(Double.toString(lastNumber + thisNumber));
			}
			else if(lastOp.equals("-"))
			{
				editText.setText(Double.toString(lastNumber - thisNumber));
			}
			else if(lastOp.equals("*"))
			{
				editText.setText(Double.toString(lastNumber * thisNumber));
			}
			else if(lastOp.equals("/"))
			{
				editText.setText(Double.toString(lastNumber / thisNumber));
			}
			opClicked = true;
		}
		if( btnText.equals("C")) {
			lastNumber = 0;
			opClicked = false;
			editText.setText("0");
		}
		
	}

}
