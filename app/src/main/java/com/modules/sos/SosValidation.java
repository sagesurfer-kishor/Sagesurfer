package com.modules.sos;

public class SosValidation {
//group role = 6 - CC
    public static boolean checkId(int role_id) {
        return role_id == 6;
    }

    public static boolean checkParentId(int role_id) {
        return role_id == 34;
    }

    public static boolean checkNaturalSupportId(int role_id) {
        return role_id == 24;
    }

}
