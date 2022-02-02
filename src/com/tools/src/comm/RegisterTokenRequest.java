package comm;

import core.AuthToken;

import java.io.Serializable;

public class RegisterTokenRequest extends Request implements Serializable {

    private final AuthToken token;

    public RegisterTokenRequest(AuthToken token) {
        this.token = token;
    }

    public AuthToken getToken() {
        return token;
    }

}
