package com.example.quxian.brainwave.utils;

import com.example.quxian.brainwave.model.UserBean;

public class SaveAccountUtil {
    private static UserBean userBean;

    public static UserBean getUserBean() {
        return userBean;
    }

    public static void setUserBean(UserBean userBean) {
        SaveAccountUtil.userBean = userBean;
    }
}
