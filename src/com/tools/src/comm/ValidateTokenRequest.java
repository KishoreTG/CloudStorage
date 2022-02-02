package comm;

import core.AuthToken;

import java.io.Serializable;

public class ValidateTokenRequest extends Request implements Serializable {

    private final AuthToken token;

    public ValidateTokenRequest(AuthToken token) {
        this.token = token;
    }

    public AuthToken getToken() {
        return token;
    }

}
