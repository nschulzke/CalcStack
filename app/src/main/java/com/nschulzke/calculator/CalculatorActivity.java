package com.nschulzke.calculator;

import android.annotation.SuppressLint;
import android.os.Handler;
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
    private boolean inRads = false;
    private boolean secondPanel = false;
    private TextView textViewLine;
    private StackView stackView;

    private Handler handler = new Handler();

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
        findViewById(R.id.buttonSub).setOnLongClickListener(this);
        findViewById(R.id.buttonAdd).setOnLongClickListener(this);
        findViewById(R.id.buttonMult).setOnLongClickListener(this);
        findViewById(R.id.buttonDiv).setOnLongClickListener(this);
        findViewById(R.id.buttonLog).setOnLongClickListener(this);
        findViewById(R.id.buttonSin).setOnLongClickListener(this);
        findViewById(R.id.buttonCos).setOnLongClickListener(this);
        findViewById(R.id.buttonTan).setOnLongClickListener(this);
        findViewById(R.id.buttonDegs).setOnLongClickListener(this);

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
        switch (view.getId())
        {
            case R.id.buttonAdd:
                sum(); break;
            case R.id.buttonSub:
                runOp(view, 1, true); break;
            case R.id.buttonSin:
                runOp(view, 1, true); break;
            case R.id.buttonCos:
                runOp(view, 1, true); break;
            case R.id.buttonTan:
                runOp(view, 1, true); break;
            case R.id.buttonDegs:
                runOp(view, 1, true); break;
            case R.id.buttonMult:
                runOp(view, 2, true); break;
            case R.id.buttonDiv:
                runOp(view, 2, true); break;
            case R.id.buttonLog:
                runOp(view, 2, true); break;
            case R.id.buttonBack:
                clearLine(); break;
            case R.id.buttonClear:
                stackView.clear(); break;
            case R.id.buttonEnter:
                textViewLine.setText(formatter.format(stackView.peek())); break;
            case R.id.stackView:
                stackView.over(); break;
            default: return false;
        }
        return true;
    }

    /**
     * Event handler, pops one item off the stack
     * @param view The view that received the event
     */
    public void pop(View view)
    {
        if (stackView.size() > 0)
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
     * Runs an operation depending on which button is pressed
     * @param view The view that received the event
     * @param items The number of items to pop (1 or 2)
     * @param longPress True if it was a long press
     */
    public void runOp(final View view, int items, boolean longPress) {
        if (textViewLine.getText().length() != 0 || stackView.size() < items)
            pushNum(view);

        if (stackView.size() < items)
            return;

        Double num1 = stackView.pop();
        Double num2 = 1.0;
        if (items >= 2)
            num2 = stackView.pop();

        if (!longPress)
        {
            switch (view.getId())
            {
                case R.id.buttonAdd:
                    stackView.push(num1 + num2); break;
                case R.id.buttonSub:
                    stackView.push(num1 - num2); break;
                case R.id.buttonMult:
                    stackView.push(num1 * num2); break;
                case R.id.buttonDiv:
                    stackView.push(num1 / num2); break;
                case R.id.buttonMod:
                    stackView.push(num1 % num2); break;
                case R.id.buttonPow:
                    stackView.push(Math.pow(num2, num1)); break;
                case R.id.buttonSqrt:
                    stackView.push(Math.sqrt(num1)); break;
                case R.id.buttonSquare:
                    stackView.push(Math.pow(num1, 2)); break;
                case R.id.buttonSin:
                    if (!inRads)
                        num1 = Math.toRadians(num1);
                    stackView.push(Math.sin(num1)); break;
                case R.id.buttonCos:
                    if (!inRads)
                        num1 = Math.toRadians(num1);
                    stackView.push(Math.cos(num1)); break;
                case R.id.buttonTan:
                    if (!inRads)
                        num1 = Math.toRadians(num1);
                    stackView.push(Math.tan(num1)); break;
                case R.id.buttonLog:
                    stackView.push(Math.log(num1)); break;
            }
        }
        else if (longPress)
        {
            switch (view.getId())
            {
                case R.id.buttonSub:
                    stackView.push(num1 * -1); break;
                case R.id.buttonMult:
                    stackView.push(Math.pow(num2, num1)); break;
                case R.id.buttonDiv:
                    stackView.push(num1 % num2); break;
                case R.id.buttonLog:
                    stackView.push(Math.log(num2) / Math.log(num1)); break;
                case R.id.buttonSin:
                    if (inRads)
                        stackView.push(Math.asin(num1));
                    else
                        stackView.push(Math.toDegrees(Math.asin(num1)));
                    break;
                case R.id.buttonCos:
                    if (inRads)
                        stackView.push(Math.acos(num1));
                    else
                        stackView.push(Math.toDegrees(Math.acos(num1)));
                    break;
                case R.id.buttonTan:
                    if (inRads)
                        stackView.push(Math.atan(num1));
                    else
                        stackView.push(Math.toDegrees(Math.atan(num1)));
                    break;
                case R.id.buttonDegs:
                    if (inRads)
                        stackView.push(Math.toRadians(num1));
                    else
                        stackView.push(Math.toDegrees(num1));
            }
        }

        final int delay = getResources().getInteger(R.integer.return_delay);
        if (secondPanel)
        {
            handler.postDelayed(new Runnable() {
                public void run()
                {
                    switchPanel(view);
                }
            }, delay);
        }
    }

    public void toggleRadians(View view) {
        if (inRads)
        {
            ((Button)view).setText(getResources().getString(R.string.button_degs));
            inRads = false;
        }
        else
        {
            ((Button)view).setText(getResources().getString(R.string.button_rads));
            inRads = true;
        }
    }

    /**
     * Sum all values on the stack
     */
    public void sum() {
        Double sum = 0.0;
        while (stackView.size() > 0)
            sum += stackView.pop();
        stackView.push(sum);
    }

    /**
     * Event handler, switches to the secondary panel
     * @param view
     */
    public void switchPanel(final View view) {
        View panel1 = findViewById(R.id.layoutPanel1);
        View panel2 = findViewById(R.id.layoutPanel2);
        if (!secondPanel)
        {
            panel1.setVisibility(View.GONE);
            panel2.setVisibility(View.VISIBLE);
            secondPanel = true;
        }
        else
        {
            panel1.setVisibility(View.VISIBLE);
            panel2.setVisibility(View.GONE);
            secondPanel = false;
        }
    }

    /**
     * Event handler, run a binary operation
     * @param view The view that received the event
     */
    public void binaryOp(final View view) {
        runOp(view, 2, false);
    }

    /**
     * Event handler, run a unary operation
     * @param view The view that received the event
     */
    public void unaryOp(final View view) {
        runOp(view, 1, false);
    }

    /**
     * Event handler, run a binary operation
     * @param view The view that received the event
     */
    public void constant(final View view) {
        switch (view.getId())
        {
            case R.id.buttonPi:
                textViewLine.setText(formatter.format(Math.PI)); break;
            case R.id.buttonE:
                textViewLine.setText(formatter.format(Math.E)); break;
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
