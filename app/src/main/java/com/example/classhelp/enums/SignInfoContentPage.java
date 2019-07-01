package com.example.classhelp.enums;

public enum SignInfoContentPage {
    出勤(0),
    旷课(1),
    异常(2);

    private final int position;

    SignInfoContentPage(int pos) {
        position = pos;
    }

    public static SignInfoContentPage getPage(int position) {
        return SignInfoContentPage.values()[position];
    }

    public static int size() {
        return SignInfoContentPage.values().length;
    }

    public static String[] getPageNames() {
        SignInfoContentPage[] pages = SignInfoContentPage.values();
        String[] pageNames = new String[pages.length];
        for (int i = 0; i < pages.length; i++) {
            pageNames[i] = pages[i].name();
        }
        return pageNames;
    }

    public int getPosition() {
        return position;
    }
}
