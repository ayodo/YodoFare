package co.yodo.fare.component;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import co.yodo.fare.R;

/**
 * Created by luis on 25/07/13.
 * Custom View
 */
public class ClearEditText extends EditText {
    //The image we are going to use for the Clear button
    private Drawable imgCloseButton = getResources().getDrawable( R.drawable.clear_button_image );

    public ClearEditText(Context context) {
        super( context );
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super( context, attrs, defStyle );
        init();
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super( context, attrs );
        init();
    }

    void init() {
        // Set bounds of the Clear button so it will look ok
        imgCloseButton.setBounds( 0, 0, imgCloseButton.getIntrinsicWidth(), imgCloseButton.getIntrinsicHeight() );

        // There may be initial text in the field, so we may need to display the  button
        handleClearButton();

        // If the Close image is displayed and the user remove his finger from the button, clear it. Otherwise do nothing
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClearEditText et = ClearEditText.this;

                if( et.getCompoundDrawables()[2] == null )
                    return false;

                if( event.getAction() != MotionEvent.ACTION_UP )
                    return false;

                if( event.getX() > et.getWidth() - et.getPaddingRight() - imgCloseButton.getIntrinsicWidth() ) {
                    et.setText("");
                    ClearEditText.this.handleClearButton();
                }
                return false;
            }
        });

        // If text changes, take care of the button
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ClearEditText.this.handleClearButton();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
    }

    void handleClearButton() {
        if( this.getText().toString().equals("") ) {
            // add the clear button
            this.setCompoundDrawables( this.getCompoundDrawables()[0], this.getCompoundDrawables()[1], null, this.getCompoundDrawables()[3] );
        }
        else {
            //remove clear button
            this.setCompoundDrawables( this.getCompoundDrawables()[0], this.getCompoundDrawables()[1], imgCloseButton, this.getCompoundDrawables()[3] );
        }
    }
}
