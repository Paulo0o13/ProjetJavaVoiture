package org.example.session;


import org.example.model.User;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Component
@SessionScope
@Getter
@Setter
public class UserSession implements Serializable {
    private User user;

    public boolean isLoggedIn() {
        return user != null;
    }
}
