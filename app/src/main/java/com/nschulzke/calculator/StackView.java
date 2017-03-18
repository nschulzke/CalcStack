package com.nschulzke.calculator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Stack;

import static java.security.AccessController.getContext;

/**
 * Basically a TextView with an internal stack
 */
public class StackView extends AppCompatTextView {
    private DecimalFormat formatter;
    private Stack<Double> stack;

    /**
     * Displays the top items in the stack in textViewStack
     * Number of lines is set as max_stack_lines in values.xml
     */
    private void displayStack() {
        String newText = "";

        int start = Math.max(0, (stack.size() - getResources().getInteger(R.integer.max_stack_lines)));
        for (int i = start; i < stack.size(); i++)
            newText += "\n" + formatter.format(stack.get(i));
        newText = newText.replaceFirst("\\n", "");
        setText(newText);
    }

    /**
     * Pops the top item off the stack and returns it
     * @return The top item of the stack
     */
    public Double pop()
    {
        Double retVal = stack.pop();
        displayStack();
        return retVal;
    }

    /**
     * Pushes an item onto the top of the stack
     * @param val The value to push to the stack
     */
    public void push(Double val)
    {
        stack.push(val);
        displayStack();
    }

    /**
     * Returns the top item on the stack without removing it
     * @return The top item on the stack
     */
    public Double peek()
    {
        return stack.peek();
    }

    /**
     * Getter for the size of the stack
     * @return The size of the stack
     */
    public int size()
    {
        return stack.size();
    }

    /**
     * Clears all items in the stack
     */
    public void clear()
    {
        stack.clear();
        displayStack();
    }

    /**
     * Swaps the top two items in the stack and displays the new results
     */
    public void swap()
    {
        if (stack.size() > 1)
        {
            Double val1 = stack.pop();
            Double val2 = stack.pop();
            stack.push(val1);
            stack.push(val2);
            displayStack();
        }
    }

    /**
     * Moves the third item on the stack to the top of the stack
     */
    public void over()
    {
        if (stack.size() > 2)
        {
            Double val1 = stack.pop();
            Double val2 = stack.pop();
            Double val3 = stack.pop();
            stack.push(val2);
            stack.push(val1);
            stack.push(val3);
            displayStack();
        }
    }

    /**
     * Sets the formatter to use for display
     * @param format The formatter to use
     */
    public void setFormatter(DecimalFormat format)
    {
        formatter = format;
    }

    private void init(Context context) {
        stack = new Stack<Double>();
        formatter = new DecimalFormat();
    }

    public StackView(Context context) {
        super(context);
        init(context);
    }

    public StackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StackView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
}
