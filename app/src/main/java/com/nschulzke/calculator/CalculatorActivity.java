package com.nschulzke.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Stack;

public class CalculatorActivity extends AppCompatActivity implements View.OnLongClickListener {

    private Stack<Double> stack;
    private Double last;
    private boolean hasLast = false;
    private TextView textViewStack;
    private TextView textViewNum;
    private DecimalFormat formatter = new DecimalFormat("#.#####", DecimalFormatSymbols.getInstance( Locale.ENGLISH ));

    private static final int MAX_IN_DIGITS = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        stack = new Stack<Double>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        textViewStack = (TextView) findViewById(R.id.textViewStack);
        textViewNum = (TextView) findViewById(R.id.textViewNum);

        ((Button) findViewById(R.id.buttonBack)).setOnLongClickListener(this);
        ((Button) findViewById(R.id.buttonClear)).setOnLongClickListener(this);
        ((Button) findViewById(R.id.buttonEnter)).setOnLongClickListener(this);
        ((TextView) findViewById(R.id.textViewStack)).setOnLongClickListener(this);
    }

    protected void clearDigits() {
        textViewNum.setText("");
    }

    protected void updateStack() {
        String newText = "";
        for (Double d : stack)
            newText += "\n" + formatter.format(d);
        newText = newText.replaceFirst("\\n", "");
        textViewStack.setText(newText);
    }

    protected void swapStack()
    {
        if (stack.size() > 1)
        {
            Double val1 = stack.pop();
            Double val2 = stack.pop();
            stack.push(val1);
            stack.push(val2);
            updateStack();
        }
    }

    protected void overStack()
    {
        if (stack.size() > 2)
        {
            Double val1 = stack.pop();
            Double val2 = stack.pop();
            Double val3 = stack.pop();
            stack.push(val2);
            stack.push(val1);
            stack.push(val3);
            updateStack();
        }
    }

    protected void clearStack()
    {
        stack.clear();
        hasLast = false;
        updateStack();
    }

    public boolean onLongClick(View view) {
        if (view.getId() == R.id.buttonBack)
            clearDigits();
        else if (view.getId() == R.id.buttonClear)
            clearStack();
        else if (view.getId() == R.id.buttonEnter)
            textViewNum.setText(formatter.format(stack.peek()));
        else if (view.getId() == R.id.textViewStack)
            overStack();
        return true;
    }

    public void clearStack(View view)
    {
        stack.pop();
        updateStack();
    }

    public void swapStack(View view)
    {
        swapStack();
    }

    public void eraseOne(View view)
    {
        String text = textViewNum.getText().toString();
        if (text.length() > 1)
            textViewNum.setText(text.substring(0, text.length() - 1));
        else
            textViewNum.setText("");
    }

    public void pushNum(View view)
    {
        String currText = textViewNum.getText().toString();

        Double currDouble;

        // Empty string can't be sent
        if (currText.equals("")) {
            if (hasLast)
                textViewNum.setText(formatter.format(last));
            return;
        }
        else if (currText.equals("."))
            return;

        currDouble = Double.valueOf(currText);

        last = currDouble;
        hasLast = true;

        stack.push(currDouble);
        updateStack();
        clearDigits();
    }

    public void runOp(View view) {
        if (textViewNum.getText().length() != 0 || stack.size() < 2)
            pushNum(view);

        if (stack.size() < 2)
            return;

        Double num2 = stack.pop();
        Double num1 = stack.pop();
        switch (view.getId())
        {
            case R.id.buttonAdd:
                stack.push(num1 + num2);
                break;
            case R.id.buttonSub:
                stack.push(num1 - num2);
                break;
            case R.id.buttonMult:
                stack.push(num1 * num2);
                break;
            case R.id.buttonDiv:
                stack.push(num1 / num2);
                break;
        }
        updateStack();
    }

    public void addDigit(View view) {
        Button button = (Button) view;
        String currText = textViewNum.getText().toString();
        String digit = button.getText().toString();

        // Only allow one decimal point
        if (digit.equals(".") && currText.contains("."))
            return;
        if (currText.replace(".", "").length() >= MAX_IN_DIGITS)
            return;

        // If it's already just "0", replace, otherwise append
        if (currText.equals("0") && !digit.equals("."))
            textViewNum.setText(digit);
        else
            textViewNum.setText(textViewNum.getText() + digit);
    }
}
