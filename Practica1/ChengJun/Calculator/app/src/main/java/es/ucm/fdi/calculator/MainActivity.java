package es.ucm.fdi.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Calculator calculator;
    EditText editTextX;
    EditText editTextY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "method: onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calculator = new Calculator();
        editTextX = findViewById(R.id.editTextNumberDecimal);
        editTextY = findViewById(R.id.editTextNumberDecimal2);
    }

    public void addXandY(View view) {
        Log.d("MainActivity", "method: addXandY");
        boolean error = false;

        String str_x = editTextX.getText().toString();
        String str_y = editTextY.getText().toString();

        if (str_x.equals("")) {
            editTextX.setError("Introduce un entero");
            error = true;
        }
        if (str_y.equals("")) {
            editTextY.setError("Introduce un entero");
            error = true;
        }
        if (error) {
            return;
        }

        int x = Integer.parseInt(str_x);
        int y = Integer.parseInt(str_y);
        String result = calculator.add(x, y).toString();

        Intent intent = new Intent(this, CalculatorResultActivity.class);
        intent.putExtra("res", result);
        startActivity(intent);
    }
}