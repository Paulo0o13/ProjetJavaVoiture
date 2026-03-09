package org.example.session;

import lombok.Getter;
import lombok.Setter;
import org.example.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;

@Component
@SessionScope
@Getter
@Setter
public class UserSession implements Serializable {
    private User user;

    public boolean isLoggedOut() {
        return this.user == null;
    }
}
