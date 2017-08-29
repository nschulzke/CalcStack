package com.nschulzke.calculator;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
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
     * Handles short presses for views in this activity
     * @param view The view that received the event
     */
    public void onClick(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        switch ( view.getId())
        {
            case R.id.stackView:
                stackView.swap(); break;
            case R.id.buttonClear:
                if (stackView.size() > 0)
                    stackView.pop();
                break;
            case R.id.buttonBack:
                backSpace();
                break;
            case R.id.buttonSwap:
                switchPanel(); break;
            case R.id.buttonPi:
                setLine(Math.PI); break;
            case R.id.buttonE:
                setLine(Math.E); break;
            case R.id.buttonEnter:
                pushNum(); break;
        }
    }

    /**
     * Event handler, run a binary operation
     * @param view The view that received the event
     */
    public void binaryOp(final View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        runOp(view, 2, false);
    }

    /**
     * Event handler, run a unary operation
     * @param view The view that received the event
     */
    public void unaryOp(final View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        runOp(view, 1, false);
    }

    /**
     * Event handler, adds a character to textViewLine
     * @param view The view that received the event
     */
    public void addDigit(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        Button button = (Button) view;
        String currText = textViewLine.getText().toString();
        String digit = button.getText().toString();

        // Only allow one decimal point
        if (digit.equals(".") && currText.contains("."))
            return;
        if (currText.replace(".", "").length() >= getResources().getInteger(R.integer.max_in_digits))
            return;

        // If it's already just "0", replace, otherwise append
        CharSequence text = textViewLine.getText() + digit;
        if (currText.equals("0") && !digit.equals("."))
            text = digit;

        textViewLine.setText(text);
    }

    /**
     * Event handler, toggle between degrees and radians
     * @param view The view that received the event
     */
    public void toggleRadians(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

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
     * Runs an operation depending on which button is pressed
     * @param view The view that received the event
     * @param items The number of items to pop (1 or 2)
     * @param longPress True if it was a long press
     */
    private void runOp(final View view, int items, boolean longPress) {
        if (textViewLine.getText().length() != 0 || stackView.size() < items)
            pushNum();

        if (stackView.size() < items)
            return;

        ArrayList<Double> nums = new ArrayList<>();
        for (int i = 0; i < items; i++)
            nums.add(stackView.pop());

        if (!longPress)
        {
            switch (view.getId())
            {
                case R.id.buttonAdd:
                    stackView.push(nums.get(1) + nums.get(0)); break;
                case R.id.buttonSub:
                    stackView.push(nums.get(1) - nums.get(0)); break;
                case R.id.buttonMult:
                    stackView.push(nums.get(1) * nums.get(0)); break;
                case R.id.buttonDiv:
                    stackView.push(nums.get(1) / nums.get(0)); break;
                case R.id.buttonMod:
                    stackView.push(nums.get(1) % nums.get(0)); break;
                case R.id.buttonPow:
                    stackView.push(Math.pow(nums.get(1), nums.get(0))); break;
                case R.id.buttonSqrt:
                    stackView.push(Math.sqrt(nums.get(0))); break;
                case R.id.buttonSquare:
                    stackView.push(Math.pow(nums.get(0), 2)); break;
                case R.id.buttonSin:
                    if (!inRads)
                        nums.set(0, Math.toRadians(nums.get(0)));
                    stackView.push(Math.sin(nums.get(0))); break;
                case R.id.buttonCos:
                    if (!inRads)
                        nums.set(0, Math.toRadians(nums.get(0)));
                    stackView.push(Math.cos(nums.get(0))); break;
                case R.id.buttonTan:
                    if (!inRads)
                        nums.set(0, Math.toRadians(nums.get(0)));
                    stackView.push(Math.tan(nums.get(0))); break;
                case R.id.buttonLog:
                    stackView.push(Math.log(nums.get(0))); break;
            }
        } else {
            switch (view.getId())
            {
                case R.id.buttonSub:
                    stackView.push(nums.get(0) * -1); break;
                case R.id.buttonMult:
                    stackView.push(Math.pow(nums.get(1), nums.get(0))); break;
                case R.id.buttonDiv:
                    stackView.push(nums.get(1) % nums.get(0)); break;
                case R.id.buttonLog:
                    stackView.push(Math.log(nums.get(1)) / Math.log(nums.get(0))); break;
                case R.id.buttonSin:
                    if (inRads)
                        stackView.push(Math.asin(nums.get(0)));
                    else
                        stackView.push(Math.toDegrees(Math.asin(nums.get(0))));
                    break;
                case R.id.buttonCos:
                    if (inRads)
                        stackView.push(Math.acos(nums.get(0)));
                    else
                        stackView.push(Math.toDegrees(Math.acos(nums.get(0))));
                    break;
                case R.id.buttonTan:
                    if (inRads)
                        stackView.push(Math.atan(nums.get(0)));
                    else
                        stackView.push(Math.toDegrees(Math.atan(nums.get(0))));
                    break;
                case R.id.buttonDegs:
                    if (inRads)
                        stackView.push(Math.toRadians(nums.get(0)));
                    else
                        stackView.push(Math.toDegrees(nums.get(0)));
            }
        }

        final int delay = getResources().getInteger(R.integer.return_delay);
        if (secondPanel)
        {
            handler.postDelayed(new Runnable() {
                public void run()
                {
                    switchPanel();
                }
            }, delay);
        }
    }

    /**
     * Pushes the number in textViewLine to the stack
     */
    private void pushNum()
    {
        String currText = textViewLine.getText().toString();

        Double currDouble;

        // Empty string can't be sent
        if (currText.equals("")) {
            if (hasLast)
                setLine(last);
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
     * Sum all values on the stack
     */
    private void sum() {
        Double sum = 0.0;
        while (stackView.size() > 0)
            sum += stackView.pop();
        stackView.push(sum);
    }

    /**
     * Switches to the secondary panel
     */
    private void switchPanel() {
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
     * Sets the line to the specified double.
     * @param num The number to set the line to.
     */
    private void setLine(double num) {
        textViewLine.setText(formatter.format(num));
    }

    /**
     * Clears out the digits on the number entry line
     */
    private void clearLine() {
        textViewLine.setText("");
    }

    /**
     * Removes the last digit from the number entry line
     */
    private void backSpace() {
        String text = textViewLine.getText().toString();
        if (text.length() > 1)
            textViewLine.setText(text.substring(0, text.length() - 1));
        else
            textViewLine.setText("");
    }
}
