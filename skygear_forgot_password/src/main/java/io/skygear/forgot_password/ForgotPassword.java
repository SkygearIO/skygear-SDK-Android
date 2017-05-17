package io.skygear.forgot_password;

import android.content.Context;
import io.skygear.skygear.Container;
import io.skygear.skygear.LambdaResponseHandler;
import java.util.Date;

/**
 * Forgot password for Skygear.
 */

public class ForgotPassword {

    private Container container;

    public ForgotPassword(Context context) {
        this.container = Container.defaultContainer(context);
    }
    public ForgotPassword(Container container) {
        this.container = container;
    }

    /**
     * Call lambda function.
     *
     * @param email   the email which the forgot password email should be sent to
     * @param handler the response handler
     */
    public void forgetPassword(String email, LambdaResponseHandler handler) {
        Object[] argv = new Object[]{email};
        this.container.callLambdaFunction("user:forgot-password", argv, handler);
    }

    /**
     * Call lambda function.
     *
     * @param userID      the user whose is resetting password
     * @param code        the code user received for forgot password
     * @param expireAt    when should the reset password url expire
     * @param newPassword the new password after resetting
     * @param handler the response handler
     */
    public void resetPassword(String userID, String code, Date expireAt, String newPassword, LambdaResponseHandler handler) {
        Object[] argv = new Object[]{userID, code, expireAt, newPassword};
        this.container.callLambdaFunction("user:reset-password", argv, handler);
    }
}
