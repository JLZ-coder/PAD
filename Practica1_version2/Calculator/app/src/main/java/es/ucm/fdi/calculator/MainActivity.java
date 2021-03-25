package es.ucm.fdi.calculator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Calculator calculator;
    EditText editTextX;
    EditText editTextY;
    public static final int TEXT_REQUEST = 1;
    private TextView mReplyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "method: onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calculator = new Calculator();
        editTextX = findViewById(R.id.editTextNumberDecimal);
        editTextY = findViewById(R.id.editTextNumberDecimal2);
        mReplyTextView = findViewById(R.id.textView2);
    }

    public void addXandY(View view) {
        Log.d("MainActivity", "method: addXandY");
        boolean error = false;

        String str_x = editTextX.getText().toString();
        String str_y = editTextY.getText().toString();

        /*if (str_x.equals("")) {
            editTextX.setError("Introduce un entero");
            error = true;
        }
        if (str_y.equals("")) {
            editTextY.setError("Introduce un entero");
            error = true;
        }*/

        int x = 0;
        try {
            x = Integer.parseInt(str_x);
        } catch(NumberFormatException e) {
            editTextX.setError("Introduce un entero");
            error = true;
        }

        int y = 0;
        try {
            y = Integer.parseInt(str_y);
        } catch(NumberFormatException e) {
            editTextY.setError("Introduce un entero");
            error = true;
        }

        if (error) {
            return;
        }

        String result = calculator.add(x, y).toString();

        Intent intent = new Intent(this, CalculatorResultActivity.class);
        intent.putExtra("res", result);
        startActivityForResult(intent, TEXT_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {
                String res = data.getStringExtra(CalculatorResultActivity.EXTRA_REPLY);
                mReplyTextView.setText("Resultado anterior: " + res);
                mReplyTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}