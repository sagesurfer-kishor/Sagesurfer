package com.sagesurfer.library;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

/*
 * This file is used to get platform role of user
 */

public class CheckRole {

    public static boolean isYouth(int role_id) {
        return role_id == 22 || role_id == 31 || role_id == 32 || role_id == 35 || role_id == 36;
    }

    public static boolean isClient(int role_id) {
        return role_id == 31;
    }

    public static boolean isCA_CAA(int role_id) {
        return role_id == 1 || role_id == 13;
    }

    public static boolean isWerHope(int role_id) {
        //Executive director (mhsad) -> 1
        //Director of operations -> 5
        //Coach  -> 6
        //Student-> 32
        //Parent -> 34
        //Provider -> 28
        //Pediatrician -> 19
        //Civilian -> 18
        //Teacher -> 17
        //Principal -> 16
        //Superintendent -> 15
        //School nurse -> 14
        //member->27
        return role_id == 1 || role_id == 5 || role_id == 6 || role_id == 34 || role_id == 28 || role_id == 19 || role_id == 18 || role_id == 17 || role_id == 16 || role_id == 15 || role_id == 14 || role_id == 27;
    }

    public static boolean isMhaw(int role_id) {
      /*  kUserRoleMHAW_ComplianceAdministrator       = 1, //CA
                kUserRoleMHAW_SystemAdministrator           = 6, //SA, CC
                kUserRoleMHAW_ClinicalApplicationsAdministrator = 13, //CAA
                kUserRoleMHAW_ProgramDirector               = 29, //PD
                kUserRoleMHAW_PeerStaff                     = 15, //(Direct Service Staff)
                kUserRoleMHAW_PeerSupervisor                = 16, //(Direct Service Staff)
                kUserRoleMHAW_NursePractitioner             = 17, //(Direct Service Staff)
                kUserRoleMHAW_ProfessionalStaff             = 18, //(Direct Service Staff)
                kUserRoleMHAW_CareManager                   = 19, //(Direct Service Staff)
                kUserRoleMHAW_QACoordinator                 = 23, //(Administrative Support Staff )
                kUserRoleMHAW_AdminAssistant                = 24, //(Administrative Support Staff )
                kUserRoleMHAW_Client                        = 31,*/
        return role_id == 1 || role_id == 6 || role_id == 29 || role_id == 13 || role_id == 19 || role_id == 18 || role_id == 17 || role_id == 16 || role_id == 15 || role_id == 24 || role_id == 23 || role_id == 31 ;
    }


    public static boolean showInviteMember(int role_id) {
        return role_id == 1 || role_id == 5 || role_id == 14;
    }

    public static boolean showFabIcon(int role_id) {
        return role_id == 1 || role_id == 5 || role_id == 34;
    }

    public static boolean showFabIconOne(int role_id) {
        return role_id == 1 || role_id == 5 || role_id == 6 || role_id == 34 || role_id == 32 || role_id == 31 || role_id == 35 || role_id == 36;
    }

    public static boolean showFabIconOneTarzana(int role_id) {
        return role_id == 1 || role_id == 14 || role_id == 5 || role_id == 6;
    }

    public static boolean showFabIconOneMhaw(int role_id) {
        return role_id == 1 || role_id == 13 || role_id == 6;
    }

    public static boolean showFabIconOneWerhope(int role_id) {
        return role_id == 34 || role_id == 1 || role_id == 5;
    }

    public static boolean showFabIconOneTrail(int role_id) {
        return role_id == 34 || role_id == 1 || role_id == 5 || role_id == 6;
    }

    public static boolean EDDOIcon(int role_id) {
        return role_id == 1 || role_id == 5;
    }

    public static boolean isYouthOne(int role_id) {
        return role_id == 22 || role_id == 31 || role_id == 32 || role_id == 35 || role_id == 36 || role_id == 45 || role_id == 54;
    }



    public static boolean isSupervisor(int role_id) {
        return role_id == 5;
    } //Mentor Coordinator

    public static boolean isMHSADS(int role_id) {
        return role_id == 1;
    }

    public static boolean isCoordinator(int role_id) { //Peer Mentor
        return role_id == 6;
    }

    public static boolean isParentId(int role_id) {
        return role_id == 34;
    }

    public static boolean isNaturalSupportId(int role_id) {
        return role_id == 24;
    }

    public static boolean isInstanceAdmin(int role_id) {
        return role_id == 66;
    }

/*
    public static boolean isCoordinator(int role_id) {
        return role_id == 6;
    }

    public static boolean isSupervisor(int role_id) {
        return role_id == 5;
    }

    public static boolean isParent(int role_id) {
        return role_id == 34;
    }

    public static boolean isProvider(int role_id) {
        return role_id == 28;
    }

    public static boolean isPolicy(int role_id) {
        return role_id == 1;
    }

    public static boolean isMember(int role_id) {
        return role_id == 27 || role_id == 7 || role_id == 8 || role_id == 9 || role_id == 10 || role_id == 11 || role_id == 12 ||
                role_id == 13 || role_id == 14 || role_id == 15 || role_id == 16 || role_id == 17 || role_id == 18 || role_id == 19 ||
                role_id == 23 || role_id == 29 || role_id == 33 || role_id == 39 || role_id == 42 || role_id == 24;
    }

    public static boolean isCareGiver(int role_id) {
        //34,33,39,24,48
        return role_id == 24 || role_id == 33 || role_id == 34 || role_id == 48;
    }

    */
}

