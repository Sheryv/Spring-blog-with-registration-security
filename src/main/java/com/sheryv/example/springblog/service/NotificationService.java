package com.sheryv.example.springblog.service;

public interface NotificationService {
    void addInfo(String msg);
    void addError(String msg);
    void addSuccess(String msg);
}
