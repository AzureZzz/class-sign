package com.example.classhelp.enums;

public enum RegisterContentPage {
    学生(0),
    教师(1);

    private final int position;

    RegisterContentPage(int pos) {
        position = pos;
    }

    public static RegisterContentPage getPage(int position) {
        return RegisterContentPage.values()[position];
    }

    public static int size() {
        return RegisterContentPage.values().length;
    }

    public static String[] getPageNames() {
        RegisterContentPage[] pages = RegisterContentPage.values();
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
