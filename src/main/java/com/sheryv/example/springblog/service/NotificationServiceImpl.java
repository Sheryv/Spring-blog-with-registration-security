package com.sheryv.example.springblog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    public static final String NOTIFY_MSG_SESSION_KEY = "notificationMessagesKey";

    private final HttpSession httpSession;

    @Autowired
    public NotificationServiceImpl(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public void addInfo(String msg) {
        addNotificationMessage(NotificationType.INFO, msg);
    }

    @Override
    public void addError(String msg) {
        addNotificationMessage(NotificationType.DANGER, msg);
    }

    @Override
    public void addSuccess(String msg) {
        addNotificationMessage(NotificationType.SUCCESS, msg);
    }

    private void addNotificationMessage(NotificationType type, String msg) {
        List<Message> notifyMessages = (List<Message>)
                httpSession.getAttribute(NOTIFY_MSG_SESSION_KEY);
        if (notifyMessages == null) {
            notifyMessages = new ArrayList<>();
        }
        notifyMessages.add(new Message(type, msg));
        httpSession.setAttribute(NOTIFY_MSG_SESSION_KEY, notifyMessages);
    }

    public enum NotificationType {
        INFO,
        SUCCESS,
        DANGER
    }

    public class Message {
        NotificationType type;
        String text;

        Message(NotificationType type, String text) {
            this.type = type;
            this.text = text;
        }

        public NotificationType getType() {
            return type;
        }

        public String getText() {
            return text;
        }
    }
}
