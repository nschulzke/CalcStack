package com.nschulzke.calculator;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CalculatorActivity extends AppCompatActivity implements View.OnLongClickListener {

    private DecimalFormat formatter;

    private Double last;
    private boolean hasLast = false;
    private TextView textViewStack;
    private TextView textViewLine;
    private StackView stackView;

    /**
     * Sets up the calculator activity
     * @param savedInstanceState The bundle containing the previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        formatter = new DecimalFormat(getResources().getString(R.string.decimal_format), DecimalFormatSymbols.getInstance( Locale.ENGLISH ));

        textViewLine = (TextView) findViewById(R.id.textViewLine);
        stackView = (StackView) findViewById(R.id.stackView);

        stackView.setFormatter(formatter);

        findViewById(R.id.buttonBack).setOnLongClickListener(this);
        findViewById(R.id.buttonClear).setOnLongClickListener(this);
        findViewById(R.id.buttonEnter).setOnLongClickListener(this);
        findViewById(R.id.stackView).setOnLongClickListener(this);
    }

    /**
     * Clears out the digits on the number entry line
     */
    private void clearLine() {
        textViewLine.setText("");
    }

    /**
     * Handles long presses for views in this activity
     * @param view The view that received the event
     * @return True if the event is consumed, false if not
     */
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.buttonBack)
            clearLine();
        else if (view.getId() == R.id.buttonClear)
        {
            stackView.clear();
            hasLast = false;
        }
        else if (view.getId() == R.id.buttonEnter)
            textViewLine.setText(formatter.format(stackView.peek()));
        else if (view.getId() == R.id.stackView)
            stackView.over();
        return true;
    }

    /**
     * Event handler, pops one item off the stack
     * @param view The view that received the event
     */
    public void pop(View view)
    {
        stackView.pop();
    }

    /**
     * Event handler, swaps the top two items on the stack
     * @param view The view that received the event
     */
    public void swap(View view)
    {
        stackView.swap();
    }

    /**
     * Event handler, deletes the last character from textViewLine
     * @param view The view that received the event
     */
    public void backspace(View view)
    {
        String text = textViewLine.getText().toString();
        if (text.length() > 1)
            textViewLine.setText(text.substring(0, text.length() - 1));
        else
            textViewLine.setText("");
    }

    /**
     * Event handler, pushes the number in textViewLine to the stack
     * @param view The view that received the event
     */
    public void pushNum(View view)
    {
        String currText = textViewLine.getText().toString();

        Double currDouble;

        // Empty string can't be sent
        if (currText.equals("")) {
            if (hasLast)
                textViewLine.setText(formatter.format(last));
            return;
        }
        else if (currText.equals("."))
            return;

        currDouble = Double.valueOf(currText);

        last = currDouble;
        hasLast = true;

        stackView.push(currDouble);
        clearLine();
    }

    /**
     * Event handler, runs an operation depending on which button is pressed
     * @param view The view that received the event
     */
    public void runOp(View view) {
        if (textViewLine.getText().length() != 0 || stackView.size() < 2)
            pushNum(view);

        if (stackView.size() < 2)
            return;

        Double num2 = stackView.pop();
        Double num1 = stackView.pop();
        switch (view.getId())
        {
            case R.id.buttonAdd:
                stackView.push(num1 + num2);
                break;
            case R.id.buttonSub:
                stackView.push(num1 - num2);
                break;
            case R.id.buttonMult:
                stackView.push(num1 * num2);
                break;
            case R.id.buttonDiv:
                stackView.push(num1 / num2);
                break;
        }
    }

    /**
     * Event handler, adds a character to textViewLine
     * @param view The view that received the event
     */
    @SuppressLint("SetTextI18n")
    public void addDigit(View view) {
        Button button = (Button) view;
        String currText = textViewLine.getText().toString();
        String digit = button.getText().toString();

        // Only allow one decimal point
        if (digit.equals(".") && currText.contains("."))
            return;
        if (currText.replace(".", "").length() >= getResources().getInteger(R.integer.max_in_digits))
            return;

        // If it's already just "0", replace, otherwise append
        if (currText.equals("0") && !digit.equals("."))
            textViewLine.setText(digit);
        else
            textViewLine.setText(textViewLine.getText() + digit);
    }
}
