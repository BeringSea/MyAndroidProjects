package ict.edu.rs.tipster;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    public static final String TIP_AMOUNT = "tipAmount";
    public static final String TOTAL_TO_PAY = "totalToPay";
    public static final String TOTAL_PER_PERSON = "totalPerPerson";

    private EditText edtTotalAmount;
    private EditText edtNumberOfPeople;
    private EditText edtOther;
    private TextView txtTipAmount;
    private TextView txtTotalToPay;
    private TextView txtTotalPerPerson;
    private RadioGroup rgTipPercentage;

    // postavljena polja klase da bismo mogli da ih iskoristimo da sacuvamo vrednos kada se apk prebabi u landscape mod

    private double tipAmount;
    private double totalToPay;
    private double totalPerPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // prvo proveravamo da li je savedInstanceState razlicito od null za prenos podataka u landscape
        if(savedInstanceState != null){
            tipAmount = savedInstanceState.getDouble(TIP_AMOUNT,0);
            totalToPay = savedInstanceState.getDouble(TOTAL_PER_PERSON,0);
            totalPerPerson = savedInstanceState.getDouble(TOTAL_TO_PAY,0);
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        edtNumberOfPeople = (EditText) findViewById(R.id.edtNumberOfPeople);
        edtTotalAmount = (EditText) findViewById(R.id.edtTotalAmount);
        edtOther = (EditText) findViewById(R.id.edtOther);
        txtTipAmount = (TextView) findViewById(R.id.txtTipAmount);
        txtTotalToPay = (TextView) findViewById(R.id.txtTotalToPay);
        txtTotalPerPerson = (TextView) findViewById(R.id.txtTotalPerPerson);
        rgTipPercentage = (RadioGroup) findViewById(R.id.rgTipPercentage);

        rgTipPercentage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                edtOther.setEnabled(checkedId == R.id.rbOther); // logicki izraz koji proverava dali je cekiran rbOther
            }
        });
        // moramo ponovo da prepisemo u metodi onCreate vrednosti da bi se sacuvale u landscape modu ali to mora da se uradi na kraju posle definisanja kontrola

        txtTipAmount.setText(Double.toString(tipAmount));
        txtTotalToPay.setText(Double.toString(totalToPay));
        txtTotalPerPerson.setText(Double.toString(totalPerPerson));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnReset_onClick(View view) {
        edtTotalAmount.setText("");
        edtNumberOfPeople.setText("");
        rgTipPercentage.check(R.id.rb15);
        edtOther.setText("");
        edtTotalAmount.requestFocus();
    }

    public void btnCompute_onClick(View view) {

        Double totalAmount = edtTotalAmount.length() > 0 ?
                Double.parseDouble(edtTotalAmount.getText().toString()) : 0.0;
//        Integer numberOfPeople = edtNumberOfPeople.length() > 0 ?
//                Integer.parseInt(edtNumberOfPeople.getText().toString()) : 0;
        Integer numberOfPeople = 0;
        if(edtNumberOfPeople.length() > 0){
             numberOfPeople = Integer.parseInt(edtNumberOfPeople.getText().toString());
        }
        else{
             numberOfPeople = 0;
        }
        double tipPercentage = 0;
        switch (rgTipPercentage.getCheckedRadioButtonId())
        {
            case R.id.rb15:
                tipPercentage = 15;
                break;
            case  R.id.rb20:
                tipPercentage = 20;
                break;
            case R.id.rbOther:
                tipPercentage = Double.parseDouble(edtOther.getText().toString());
        }

//        if(totalAmount < 1)
//        {
//            new AlertDialog.Builder(this)
//                    .setTitle("Error")
//                    .setMessage("Total amount must be greater than zero!")
//                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            edtNumberOfPeople.requestFocus();
//                        }
//                    }).show();
//        }
//
//        if(numberOfPeople < 1)
//        {
//            new AlertDialog.Builder(this)
//                    .setTitle("Error")
//                    .setMessage("Number of people must be greater than zero!")
//                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            edtNumberOfPeople.requestFocus();
//                        }
//                    }).show();
//        }

        boolean error = false;

        if (error = numberOfPeople < 1)
            showErrorDialog("Number of people must be greater than zero!", edtNumberOfPeople);
        else if (error = totalAmount <= 0)
            showErrorDialog("Total amount must be greater than zero!", edtTotalAmount);
        if (error)
            return;


        tipAmount = totalAmount * tipPercentage / 100;
        // Double tipAmountZaKlasu = totalAmount * tipPercentage / 100;
        totalToPay = totalAmount + tipAmount;
        totalPerPerson = totalToPay / numberOfPeople;

//        txtTipAmount.setText(String.valueOf(tipAmount));
        //Double txtTipAmount.setText(tipAmountZaKlasu.toString()); primer ukoliko nam je tip objekat Double
//        txtTotalToPay.setText(String.valueOf(totalToPay));
//        txtTotalPerPerson.setText(String.valueOf(totalPerPerson));
        // ili
        txtTipAmount.setText(Double.toString(tipAmount));
        txtTotalToPay.setText(Double.toString(totalToPay));
        txtTotalPerPerson.setText(Double.toString(totalPerPerson));
    }

    public void showErrorDialog(String message, final View view){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.requestFocus();
                    }
                }).show();
    }

    // koristimo metodu onSaveInstanceState koja ce pri promeni orijentacije ekrana sacuvati rezultate
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(TIP_AMOUNT,tipAmount);
        outState.putDouble(TOTAL_TO_PAY,totalToPay);
        outState.putDouble(TOTAL_PER_PERSON,totalPerPerson);
    }
}
