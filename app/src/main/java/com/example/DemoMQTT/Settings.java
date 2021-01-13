package com.example.DemoMQTT;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class Settings extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Button ok = findViewById(R.id.button);
        ok.setOnClickListener((View.OnClickListener) this);
    }
    public void onClick(View v) {
        Switch Switch1 = (Switch) findViewById(R.id.switch1);
        Switch Switch2 = (Switch) findViewById(R.id.switch2);
        Switch Switch3 = (Switch) findViewById(R.id.switch3);
        Boolean SwitchState1 = Switch1.isChecked();
        Boolean SwitchState2 = Switch2.isChecked();
        Boolean SwitchState3 = Switch3.isChecked();
        switch (v.getId()) {
            case R.id.button:
                boolean state=SwitchState1;
                Intent i = new Intent(this,MainActivity.class);
                i.putExtra("state1", SwitchState1);
                i.putExtra("state2", SwitchState2);
                i.putExtra("state3", SwitchState3);
                startActivity(i);
                break;
        }


    }

    }