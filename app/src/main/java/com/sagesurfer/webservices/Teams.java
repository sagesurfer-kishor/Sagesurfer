package com.sagesurfer.webservices;

import android.app.Activity;
import android.os.Handler;

import com.modules.appointment.model.Appointment_;
import com.modules.beahivoural_survey.model.BehaviouralHealth;
import com.modules.fms.FileSharing_;
import com.modules.fms.File_;
import com.modules.journaling.model.Journal_;
import com.modules.postcard.Postcard_;
import com.modules.sows.model.SowsNotes;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.constant.General;
import com.sagesurfer.models.CareUploaded_;
import com.sagesurfer.models.CaseloadPeerNote_;
import com.sagesurfer.models.Content_;
import com.sagesurfer.models.Event_;
import com.sagesurfer.models.Goal_;
import com.sagesurfer.models.Task_;
import com.sagesurfer.network.NetworkCall_;
import com.sagesurfer.network.Urls_;
import com.sagesurfer.parser.Alerts_;
import com.sagesurfer.parser.Appointments_;
import com.sagesurfer.parser.CaseloadParser_;
import com.sagesurfer.parser.Invitations_;
import com.sagesurfer.parser.Journaling_;
import com.sagesurfer.parser.Mail_;
import com.sagesurfer.parser.SelfCare_;
import com.sagesurfer.parser.SelfGoal_;
import com.sagesurfer.parser.SenjamDoctorNoteList_;
import com.storage.database.operations.DatabaseInsert_;
import com.storage.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.RequestBody;

/**
 * Created by Monika on 11/26/2018.
 */

public class Teams {

    public static String getAnnouncement(String TAG, Activity activity) {
        String response = "";
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.ANNOUNCEMENT);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        requestMap.put(General.LAST_DATE, "0");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DATA;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    public static String getTeamTalk(String TAG, Activity activity) {
        String response = "";
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.TEAMTALK);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DATA;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    // Make network call to fetch all invitations list from server
    public static ArrayList<Invitations_> getInvites(String TAG, Activity activity) {
        ArrayList<Invitations_> invitationsArrayList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_INVITATIONS);
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_CALENDAR;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    invitationsArrayList = Alerts_.parseInvite(response, activity.getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return invitationsArrayList;
    }

    public static ArrayList<Task_> getGroupTaskList(String TAG, Activity activity) {
        String response = "";
        ArrayList<Task_> taskList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.TASKLIST);
        requestMap.put(General.LAST_DATE, "0");
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DATA;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                taskList = Alerts_.parseTask(response, Actions_.TASKLIST, General.GROUP_TASK_LIST, activity.getApplicationContext(), TAG);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return taskList;
    }

    public static ArrayList<Task_> getMyTaskList(String TAG, Activity activity) {
        String response = "";
        ArrayList<Task_> taskList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.TASK_LIST);
        requestMap.put(General.LAST_DATE, "0");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.ALERT_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                taskList = Alerts_.parseTask(response, Actions_.TASK_LIST, General.TASK_LIST, activity.getApplicationContext(), TAG);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return taskList;
    }

    // make call to fetch file and folders for root folder
    public static ArrayList<FileSharing_> getFMS(String TAG, Activity activity) {
        ArrayList<FileSharing_> fileArrayList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET);
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));
        requestMap.put(General.LAST_DATE, "0");
        requestMap.put(General.FOLDER_ID, "-1");

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_FMS;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    ArrayList<File_> fileList = Alerts_.parseFiles(response, activity.getApplicationContext(), TAG);
                    saveFileList(fileList, activity);
                    fileArrayList = setFileList(fileList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fileArrayList;
    }

    // Save newly added records to database
    private static void saveFileList(final ArrayList<File_> list, final Activity activity) {
        final Handler handler = new Handler();  //Optional. Define as a variable in your activity.
        Runnable r = new Runnable() {
            @Override
            public void run() {
                DatabaseInsert_ databaseInsert_ = new DatabaseInsert_(activity);
                for (int i = 0; i < list.size(); i++) {
                    databaseInsert_.insertFMS(list.get(i));
                }
                handler.post(new Runnable()  //If you want to update the UI, queue the code on the UI thread
                {
                    public void run() {
                        //Code to update the UI
                    }
                });
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    // make finale list to display with file and folder
    private static ArrayList<FileSharing_> setFileList(ArrayList<File_> fileList) {
        ArrayList<FileSharing_> fileArrayList = new ArrayList<>();
        if (fileList.size() > 0 && fileList.get(0).getStatus() == 1) {
            FileSharing_ sectionOne = new FileSharing_();
            sectionOne.setStatus(1);
            sectionOne.setName("Files");
            sectionOne.setSection(true);
            sectionOne.setIsFile(false);
            fileArrayList.add(sectionOne);
            for (File_ file_ : fileList) {
                FileSharing_ fileSharing_ = new FileSharing_();
                fileSharing_.setStatus(file_.getStatus());
                fileSharing_.setId(file_.getId());
                fileSharing_.setGroupId(file_.getGroupId());
                fileSharing_.setUserId(file_.getUserId());
                fileSharing_.setIsRead(file_.getIsRead());
                fileSharing_.setIsDefault(file_.getIsDefault());
                fileSharing_.setCheckIn(file_.getCheckIn());
                fileSharing_.setPermission(file_.getPermission());
                fileSharing_.setDate(file_.getDate());
                fileSharing_.setTeamName(file_.getTeamName());
                fileSharing_.setUserName(file_.getUserName());
                fileSharing_.setFullName(file_.getFullName());
                fileSharing_.setRealName(file_.getRealName());
                fileSharing_.setStatus(file_.getStatus());
                fileSharing_.setComment(file_.getComment());
                fileSharing_.setDescription(file_.getDescription());
                fileSharing_.setSize(file_.getSize());
                fileSharing_.setIsFile(true);
                fileSharing_.setSection(false);
                fileArrayList.add(fileSharing_);
            }
        }
        return fileArrayList;
    }

    // make call to fetch content list from server
    public static ArrayList<Content_> getSelfCare(String TAG, Activity activity) {
        String personal = "0", like = "0", comment = "0", category = "0", language = "0", age = "0", type = "0", location = "0", state = "0", city = "0";

        ArrayList<Content_> contentArrayList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.TIMEZONE, Preferences.get(General.TIMEZONE));
        requestMap.put(General.ACTION, Actions_.GET_DATA);
        requestMap.put(General.SEARCH, "");
        requestMap.put(General.CATEGORY, category);
        requestMap.put(General.LANGUAGE, language);
        requestMap.put(General.LIKE, like);
        requestMap.put(General.COMMENT, comment);

        requestMap.put(General.TYPE, "" + type);
        requestMap.put(General.COUNTRY, "" + location);
        requestMap.put(General.STATE, "" + state);
        requestMap.put(General.CITY, "" + city);
        requestMap.put(General.PERSONAL, personal);
        requestMap.put(General.AGE, age);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    contentArrayList = SelfCare_.parseEvents(response, activity.getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return contentArrayList;
    }

    // make call to fetch content list from server
    public static ArrayList<CareUploaded_> getSelfCareUploader(String action, String TAG, Activity activity) {
        ArrayList<CareUploaded_> careUploadedArrayList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, action);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.SELF_CARE_URL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    careUploadedArrayList = SelfCare_.parseContent(response, action, TAG, activity.getApplicationContext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return careUploadedArrayList;
    }

    // make network call to fetch all goals
    public static ArrayList<Event_> getEventDetails(String TAG, String id, Activity activity) {
        ArrayList<Event_> eventArrayList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.CALENDAR);
        requestMap.put(General.ID, id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_DETAILS_CALL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    eventArrayList = Alerts_.parseEvents(response, activity.getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return eventArrayList;
    }

    // make network call to fetch all goals
    public static ArrayList<Goal_> getGoalDetails(String TAG, String id, Activity activity) {
        ArrayList<Goal_> goalArrayList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.SELF_GOAL);
        requestMap.put(General.ID, id);

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_DETAILS_CALL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    goalArrayList = SelfGoal_.parseSpams(response, Actions_.SELF_GOAL, activity.getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return goalArrayList;
    }

    //make network call to fetch all Peer Notes
    public static ArrayList<CaseloadPeerNote_> getPeerCaseloadNoteDetails(String TAG, String id, Activity activity) {
        ArrayList<CaseloadPeerNote_> caseloadPeerNoteArrayList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.NOTES);
        requestMap.put(General.ID, String.valueOf(id));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_DETAILS_CALL;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    caseloadPeerNoteArrayList = CaseloadParser_.parsePeerNote(response, Actions_.NOTES, activity.getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return caseloadPeerNoteArrayList;
    }

    // make network call to fetch all messageboard
    public static String getMessageboardDetails(String TAG, Activity activity) {
        //ArrayList<MessageBoard_> messageBoardArrayList = new ArrayList<>();
        String response = "";
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_MESSAGEBOARD);
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.GROUP_ID, Preferences.get(General.GROUP_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_TEAM_DETAILS_PAGE;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                /*if (response != null) {
                    messageBoardArrayList = Alerts_.parseMessageBoard(response, Actions_.GET_MESSAGEBOARD, activity.getApplicationContext(), TAG);
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    //make network call to fetch all journal details
    public static ArrayList<Journal_> getJournalDetails(String TAG, String id, Activity activity) {
        ArrayList<Journal_> journalArrayList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_JOURNAL_DETAILS);
        requestMap.put(General.ID, String.valueOf(id));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_JOURNALING;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    journalArrayList = Journaling_.parseJournalingDetails(response, Actions_.GET_JOURNAL_DETAILS, activity.getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return journalArrayList;
    }

    //make network call to fetch all note details
    public static ArrayList<SowsNotes> getNoteDetails(String TAG, String id, Activity activity) {
        ArrayList<SowsNotes> sowsNotesArrayList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_JOURNAL_DETAILS);
        requestMap.put(General.ID, String.valueOf(id));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_JOURNALING;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    sowsNotesArrayList = SenjamDoctorNoteList_.parseSowsNoteList(response, Actions_.GET_JOURNAL_DETAILS, activity, TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sowsNotesArrayList;
    }

    //make network call to fetch all appointment details
    public static ArrayList<Appointment_> getAppointmentDetails(String TAG, String id, Activity activity) {
        ArrayList<Appointment_> appointmentArrayList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.VIEW_DETAILS);
        requestMap.put(General.ID, String.valueOf(id));

        String url = Preferences.get(General.DOMAIN) + Urls_.MOBILE_MHAW_APPOINTMENT;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    appointmentArrayList = Appointments_.appointmentList(response, Actions_.VIEW_DETAILS, activity.getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return appointmentArrayList;
    }

    //make network call to fetch all journal details
    public static ArrayList<Postcard_> getMessageDetails(String TAG, String messageId, Activity activity) {
        ArrayList<Postcard_> postcardArrayList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_MAILS);
        requestMap.put(General.LAST_DATE, "0");
        requestMap.put(General.TYPE, "inbox");
        // requestMap.put(General.ID, String.valueOf(messageId));
        // requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));
        requestMap.put(General.DOMAIN_CODE, Preferences.get(General.DOMAIN_CODE));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_POSTCARD;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity.getApplicationContext(), activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity.getApplicationContext(), activity);
                if (response != null) {
                    //postcardArrayList = Journaling_.parseMessageDetails(response, Actions_.GET_MAILS, activity.getApplicationContext(), TAG);
                    postcardArrayList = Mail_.parseMail(response, "inbox", activity.getApplicationContext(), TAG);
                    ;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return postcardArrayList;
    }


    //make network call to fetch all journal details
    public static ArrayList<BehaviouralHealth> getBHSDetails(String TAG, String messageId, Activity activity) {
        ArrayList<BehaviouralHealth> behaviouralHealthArrayList = new ArrayList<>();
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put(General.ACTION, Actions_.GET_FORM_DETAILS);
        requestMap.put(General.ID, String.valueOf(messageId));
        requestMap.put(General.USER_ID, Preferences.get(General.USER_ID));

        String url = Preferences.get(General.DOMAIN) + "/" + Urls_.MOBILE_WERHOPE_BEHAVIOURAL_HEALTH_SERVEY;
        RequestBody requestBody = NetworkCall_.make(requestMap, url, TAG, activity, activity);
        if (requestBody != null) {
            try {
                String response = NetworkCall_.post(url, requestBody, TAG, activity, activity);
                if (response != null) {
                    behaviouralHealthArrayList = Journaling_.parseBHSDetails(response, Actions_.GET_FORM_DETAILS, activity.getApplicationContext(), TAG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return behaviouralHealthArrayList;
    }
}
