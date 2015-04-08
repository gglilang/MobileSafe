package com.lilang.mobilesafe.domain;

/**
 * 黑名单的业务bean
 * Created by 朗 on 2015/4/5.
 */
public class BlackNumberInfo {
    private String number;
    private String mode;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "BlackNumberInfo{" +
                "number='" + number + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }
}
