package es.ucm.fdi.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CalculatorResultActivity extends AppCompatActivity {

    TextView textView;
    public static final String EXTRA_REPLY =
            "algo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_result);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra("res");

        // Capture the layout's TextView and set the string as its text
        textView = findViewById(R.id.textView);
        textView.setText(message);
    }

    public void devolver_res(View view) {
        String res = textView.getText().toString();
        Intent dev_intent = new Intent();
        dev_intent.putExtra(EXTRA_REPLY, res);
        setResult(RESULT_OK, dev_intent);
        finish();
    }
}