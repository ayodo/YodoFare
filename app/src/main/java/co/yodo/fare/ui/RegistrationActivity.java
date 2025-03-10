package co.yodo.fare.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;

import co.yodo.fare.R;
import co.yodo.fare.component.YodoHandler;
import co.yodo.fare.data.ServerResponse;
import co.yodo.fare.helper.PrefUtils;
import co.yodo.fare.net.YodoRequest;

public class RegistrationActivity extends AppCompatActivity implements YodoRequest.RESTListener {
    /** The context object */
    private Context ac;

    /** GUI Controllers */
    private EditText password;

    /** Messages Handler */
    private static YodoHandler handlerMessages;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_registration );

        setupGUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch( itemId ) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected( item );
    }

    private void setupGUI() {
        ac = RegistrationActivity.this;

        handlerMessages = new YodoHandler( RegistrationActivity.this );
        YodoRequest.getInstance().setListener( this );

        // GUI global components
        password = (EditText) findViewById( R.id.merchTokenText );

        // Only used at creation
        Toolbar mActionBarToolbar = (Toolbar) findViewById( R.id.registrationBar );

        setSupportActionBar( mActionBarToolbar );
        ActionBar actionbar = getSupportActionBar();
        if( actionbar != null )
            actionbar.setDisplayHomeAsUpEnabled( true );
    }

    /**
     * Realize a registration request
     * @param v View of the button, not used
     */
    public void registrationClick( View v ) {
        String token = password.getText().toString();
        if( token.isEmpty() ) {
            Animation shake = AnimationUtils.loadAnimation( this, R.anim.shake );
            password.startAnimation( shake );
        } else {
            String hardwareToken = PrefUtils.getHardwareToken( ac );
            PrefUtils.hideSoftKeyboard( this );

            YodoRequest.getInstance().createProgressDialog(
                    RegistrationActivity.this ,
                    YodoRequest.ProgressDialogType.NORMAL
            );

            YodoRequest.getInstance().requestRegistration(
                    RegistrationActivity.this,
                    hardwareToken,
                    token
            );
        }
    }

    public void showPasswordClick( View v ) {
        PrefUtils.showPassword( (CheckBox) v, password );
    }

    @Override
    public void onResponse( YodoRequest.RequestType type, ServerResponse response ) {
        YodoRequest.getInstance().destroyProgressDialog();

        switch( type ) {
            case ERROR_NO_INTERNET:
                handlerMessages.sendEmptyMessage( YodoHandler.NO_INTERNET );
                finish();
                break;

            case ERROR_GENERAL:
                handlerMessages.sendEmptyMessage( YodoHandler.GENERAL_ERROR );
                finish();
                break;

            case REG_MERCH_REQUEST:
                String code = response.getCode();

                if( code.equals( ServerResponse.AUTHORIZED_REGISTRATION ) ) {
                    Intent intent = new Intent( ac, SplashActivity.class );
                    startActivity( intent );
                    finish();
                } else {
                    Message msg = new Message();
                    msg.what = YodoHandler.SERVER_ERROR;

                    Bundle bundle = new Bundle();
                    bundle.putString( YodoHandler.CODE, code );
                    bundle.putString( YodoHandler.MESSAGE, response.getMessage() );
                    msg.setData( bundle );

                    handlerMessages.sendMessage( msg );
                }
                break;
        }
    }
}
