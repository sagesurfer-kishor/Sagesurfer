package com.sagesurfer.library;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.google.firebase.BuildConfig;
import com.modules.admin.AdminActivityFragment;
import com.modules.admin.AdminApprovalsFragment;
import com.modules.announcement.AnnouncementListFragment;
import com.modules.appointment.fragments.AppointmentFragment;
import com.modules.assessment.AssessmentListFragment;
import com.modules.assignment.AssignmentFragment;
import com.modules.assignment.werhope.fragments.AssignmentListingFragment;
import com.modules.beahivoural_survey.fragment.BeahivouralSurveyFragment;
import com.modules.blog.BlogListFragment;
import com.modules.calendar.CalendarFragment;
import com.modules.caseload.CaseloadFragment;
import com.modules.caseload.CaseloadPeerNoteFragment;
import com.modules.caseload.fragment.CaseLoadNoteStatusFragment;
import com.modules.cometchat_7_30.ChatFragment_;
import com.modules.consent.MyConsentFragment;
import com.modules.contacts.ContactListFragment;
import com.modules.crisis.CrisisFragment;
import com.modules.dailydosing.dashboard.DailyDosingDashBoardFragment;
import com.modules.fms.FileSharingListFragment;
import com.modules.friend_invitation.FriendInvitationFragment;
import com.modules.goal_assignment.fragment.GoalAssignmentFragment;
import com.modules.home.HomeFragment;
import com.modules.journaling.fragments.JournalListFragment;
import com.modules.leave_management.fragment.LeaveListingFragment;
import com.modules.mood.MoodFragment_;
import com.modules.motivation.fragment.MotivationFragment;
import com.modules.notification.NotificationsFragment;
import com.modules.planner.DailyPlannerFragment;
import com.modules.platform.PlatformMessageListFragment;
import com.modules.postcard.PostcardFragment;
import com.modules.re_assignment.fragment.ReAssignmentFragment;
import com.modules.reports.PlatformUsageReportFragment;
import com.modules.reports.ProcessOutcomeReportFragment;
import com.modules.reports.SelfGoalReportFragment;
import com.modules.reports.appointment_reports.fragment.AppointmentReportFragment;
import com.modules.selfcare.ReviewerListFragment;
import com.modules.selfcare.SelfCareFragment;
import com.modules.selfcare.UploaderFragment;
import com.modules.selfcare.UploaderListFragment;
import com.modules.selfgoal.SelfGoalMainFragment;
import com.modules.selfgoal.werhope_self_goal.fragment.SelfGoalWeRHopeReportFragment;
import com.modules.selfgoal.werhope_self_goal.fragment.StudentReportFragment;
import com.modules.settings.SettingsFragment;
import com.modules.sos.MySosFragment;
import com.modules.sos.SosUpdatesFragment;
import com.modules.supervisor.SupervisorMessageList;
import com.modules.support.SupportFragment;
import com.modules.task.TaskMainFragment;
import com.modules.team.TeamListFragment;
import com.modules.teamtalk.fragment.TeamTalkListFragment;
import com.modules.wall.FeedListFragment;
import com.sagesurfer.constant.General;
import com.storage.preferences.Preferences;

/**
 * @author Kailash Karankal
 */

/*
 * This class return fragment respective to menu id
 */

public class GetFragments {

    public static Fragment get(int id, Bundle bundle) {
        boolean isDailyDosingReport = false;
        switch (id) {
            case 1:
                return new FeedListFragment();
            case 2:
                if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                    return new MySosFragment();
                } else {
                    return new SosUpdatesFragment();
                }
            case 3:
                return new PostcardFragment();//Messages
            case 5:
                return new CrisisFragment();
            case 6:
                return new BlogListFragment();
            case 7:
                return new AssessmentListFragment();
            case 8:
                return new TeamListFragment();
            case 9:
                return new ChatFragment_();//ChatMainFragment();//ChatFragment();//ChatFragment_();
            case 10:
                return new ContactListFragment();
            case 11:
                return new MyConsentFragment();
            case 12:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage023") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage026")
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage027")) {
                    return new UploaderFragment();
                } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage030") ||
                        Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage031")) {
                    return new UploaderFragment();
                } else {
                    return new UploaderListFragment();
                }
            case 13:
                return new SupportFragment();
            case 15:
                return new AnnouncementListFragment();
            case 16:
                return new TaskMainFragment();
            case 17:
                return new FileSharingListFragment();
            case 18:
                return new CalendarFragment();
            case 19:
                return new TeamTalkListFragment();
            case 20:
                return new PlatformMessageListFragment();
            case 21:
                return new SupervisorMessageList();
            case 22:
                if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                    return new MySosFragment();
                } else {
                    return new SosUpdatesFragment();
                }
            case 24:
                return new SelfGoalMainFragment();
            case 25:
                return new SelfCareFragment();
            case 26:
                return new NotificationsFragment();
            case 27:
                return new DailyPlannerFragment();
            case 28:
                return new ReviewerListFragment();
            case 29:
                if (CheckRole.isYouth(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                    return new MySosFragment();
                } else {
                    return new SosUpdatesFragment();
                }
            case 32:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage013") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage015") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage021") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage022")
                        || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage024")) {
                    return new AssignmentFragment();
                } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage023")) {
                    return new AssignmentListingFragment();
                } else {
                    return new AdminApprovalsFragment();
                }
            case 33:
                return new AdminActivityFragment();
            case 34:
                return new MoodFragment_();
            case 35:
                return new TaskMainFragment();
            case 36:
                return new ChatFragment_();//ChatMainFragment();//ChatFragment();//ChatFragment_();
            case 37:
                return new AnnouncementListFragment();
            case 41:
                return new CaseloadFragment();

//                Bundle bundle1 = new Bundle();
//                bundle1.putString("senjam_patient_id","34");
//                CaseloadFragment caseloadFragment = new CaseloadFragment();
//                caseloadFragment.setArguments(bundle1);
//                return caseloadFragment;
            case 47:
                return new TeamListFragment();
            case 48:
                return new PlatformUsageReportFragment();
            case 50:
                return new HomeFragment();
            case 51:
                return new MoodFragment_();
            case 52:
                return new MotivationFragment();
            case 53:
                return new SettingsFragment();
            case 54:
                return new DailyPlannerFragment();
            case 55:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage023")) {
                    if (CheckRole.isWerHope(Integer.parseInt(Preferences.get(General.ROLE_ID)))) {
                        return new StudentReportFragment();
                    }
                } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage030") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage031")) {
                    // this boolean is for check where patient click on daily dosing dashboard
                    bundle = new Bundle();
                    bundle.putBoolean("isDailyDosingReport", isDailyDosingReport);
                    DailyDosingDashBoardFragment dailyDosingDashBoardFragment = new DailyDosingDashBoardFragment();
                    dailyDosingDashBoardFragment.setArguments(bundle);
                    return dailyDosingDashBoardFragment;
                } else {
                    return new SelfGoalReportFragment();
                }
            case 60:
                return new TeamListFragment();
            case 62:
                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage013")) {
                    return new CaseloadPeerNoteFragment(); //only for Notes(Supervisor) instance domain
                } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage023")) {
                    return new SelfGoalWeRHopeReportFragment();
                } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage030") ||
                        Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage031")) {
                    // this boolean is for check where patient click on daily dosing report
                    isDailyDosingReport = true;
                    bundle = new Bundle();
                    bundle.putBoolean("isDailyDosingReport", isDailyDosingReport);
                    DailyDosingDashBoardFragment dailyDosingDashBoardFragment = new DailyDosingDashBoardFragment();
                    dailyDosingDashBoardFragment.setArguments(bundle);
                    return dailyDosingDashBoardFragment;
                } else {
                    return new SelfGoalReportFragment(); //Adult/Youth- self goal report
                }
            case 63:
                return new ProcessOutcomeReportFragment();
            case 64:
                //return new CaseloadPeerNoteFragment();
                return new CaseLoadNoteStatusFragment();
            case 65:

                if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage013")) {
                    return new ProcessOutcomeReportFragment(); //only for instance domain
                } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage008") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage023") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage024") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage025")) {
                    return new JournalListFragment(); //only for instance domain
                } else if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage030") || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage031")) {
                    return new HomeFragment();
                }

            case 66:
                return new ReAssignmentFragment();
            case 67:
                return new ProcessOutcomeReportFragment();
            case 68:
                return new LeaveListingFragment();
            case 71:
                return new SelfCareFragment();
            case 72:
                //multiple user registration
                return new SelfCareFragment();
            case 73:
                //view assignment
                return new AssignmentListingFragment();
            case 74:
                // health servey
                return new BeahivouralSurveyFragment();
            case 75:
                // Gaol Assignment
                return new GoalAssignmentFragment();
            case 76:
                return new SelfGoalReportFragment();
            case 77:
                return new JournalListFragment(); //only for instance domain
            case 78:
                return new AssessmentListFragment();
            case 79:
                return new FriendInvitationFragment();
            case 80:
                return new AppointmentFragment();
            case 81:
                return new AppointmentReportFragment();
            default:
                return null;
        }
    }
}
