package com.modules.support;

import com.sagesurfer.constant.General;
import com.storage.preferences.Preferences;

import java.util.ArrayList;

/**
 * @author Monika M(monikam@sagesurfer.com)
 * Created on 4/3/2018
 * Last Modified on 4/3/2018
 */

class FaqList {

    static ArrayList<String> questionList() {

        ArrayList<String> questionsList = new ArrayList<>();
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage015")
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage013")) {
            questionsList.add("Why would I use the Hope app?");
            questionsList.add("How do I get started?");
            questionsList.add("What information do I have access to in the app?");
            questionsList.add("Do peer participants, providers and outside stakeholders have access to the same information in the app?");
            questionsList.add("What is the first thing I see when I open the app?");
            questionsList.add("How do I navigate around the app?");
            //questionsList.add("How do I set-up my crisis plan?");
            questionsList.add("How do I access the SOS updates and check on my SOS messages?");
            questionsList.add("What are the messages in the Received SOS section?");
            questionsList.add("What are the messages in the My SOS section?");
            questionsList.add("How do I set-up my SOS emergency contact");
            questionsList.add("How can I sent an emergency message?");
            questionsList.add("What happens once an SOS message is sent?");
            questionsList.add("What does “Attending” mean for an SOS message?");
            questionsList.add("What does “Completed” mean for an SOS message?");
            questionsList.add("How do I know when an SOS message is responded to?");
            questionsList.add("What happens to missed SOS messages?");
            questionsList.add("Can an SOS message replace 911?");
            questionsList.add("How do I access latest updates from my teams?");
            questionsList.add("What do I use Announcements for?");
            questionsList.add("How to I access the Tasks?");
            questionsList.add("Where do I view and share files?");
            questionsList.add("How do I access the Events?");
            questionsList.add("How do I add events and reminders to the calendar?");
            questionsList.add("How do I access Team Discussion and why do I use it?");
            questionsList.add("How is Team Discussion different than online chats and wall posts?");
            //questionsList.add("What is the Platform Messages?");
            //questionsList.add("What is the Mentor coordinator section and how to I access?");
            //questionsList.add("What is the Blog?");
            questionsList.add("Can I survey my teams?");
            questionsList.add("Can I view highlight statistics and interact with the team?");
        } else {
            questionsList.add("Why would I use the SageSurfer app?");
            questionsList.add("How do I get started?");
            questionsList.add("What information do I have access to in the app?");
            questionsList.add("Do consumers, providers and outside stakeholders have access to the same information in the app?");
            questionsList.add("What is the first thing I see when I open the app?");
            questionsList.add("How do I navigate around the app?");
            questionsList.add("How do I set-up my crisis plan?");
            questionsList.add("How do I access the SOS updates and check on my SOS messages?");
            questionsList.add("What are the messages in the Received SOS section?");
            questionsList.add("What are the messages in the My SOS section?");
            questionsList.add("How do I set-up my SOS emergency contact");
            questionsList.add("How can I sent an emergency message?");
            questionsList.add("What happens once an SOS message is sent?");
            questionsList.add("What does “Attending” mean for an SOS message?");
            questionsList.add("What does “Completed” mean for an SOS message?");
            questionsList.add("How do I know when an SOS message is responded to?");
            questionsList.add("What happens to missed SOS messages?");
            questionsList.add("Can an SOS message replace 911?");
            questionsList.add("How do I access latest updates from my teams?");
            questionsList.add("What do I use Announcements for?");
            questionsList.add("How to I access the Tasks?");
            questionsList.add("Where do I view and share files?");
            questionsList.add("How do I access the Events?");
            questionsList.add("How do I add events and reminders to the calendar?");
            questionsList.add("How do I access Team Talk and why do I use it?");
            questionsList.add("How is Team Talk different than online chats and wall posts?");
            //questionsList.add("What is the Platform Messages?");
            //questionsList.add("What is the Supervisor section and how to I access?");
            //questionsList.add("What is the Blog?");
            questionsList.add("Can I survey my teams?");
            questionsList.add("Can I view highlight statistics and interact with the team?");
        }

        return questionsList;
    }

    static ArrayList<String> ansList() {
        ArrayList<String> ansList = new ArrayList<>();
        if (Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage015")
                || Preferences.get(General.DOMAIN_CODE).equalsIgnoreCase("sage013")) {
            ansList.add("For providers, you can easily communicate with your team, a peer participant and " +
                    "outside stakeholders involved in the treatment of the people you serve. " +
                    "It’s meant for real-time collaboration and care coordination and has social " +
                    "media type tools also available inside the platform to make this easy. " +
                    "The app is HIPAA compliant and confidential and can sync with your web platform desktop. ");

            ansList.add("Once you have a user name and password for the Hope platform, " +
                    "download the free app from the Google PlayStore or iTunes. " +
                    "Open the app and log-in with your credentials and platform code.  " +
                    "Contact the team at support@sagesurfer.com with any questions.\n" +
                    "Don’t forget to enter correct platform code. " +
                    "If you don’t know the code please reach out to support team at support@sagesurfer.com " +
                    "with your platform instance details. ");

            ansList.add("The app syncs to all the same information available in your desktop " +
                    "version of the Hope platform to include SOS Updates, team contacts, " +
                    "crisis plan and team sub-modules such as task lists, document files, events, team discussion and announcement. " +
                    "You also have the ability to launch online individual and group chats on your mobile device. ");

            ansList.add("No, the app, like the platform, is role based and people are assigned certain " +
                    "access to pertinent information as designated by the peer participant and secondarily by the platform owner.  " +
                    "There is a great deal of flexibility for the care team but, " +
                    "with peer participants in control, are primarily in charge of an inviting people into the platform," +
                    " customize roles and designating who has access to information.");

            ansList.add("Your Home is the default view for all users which provides you with 6 tile easy navigation for different modules based on roles. " +
                    "You can easily access the core functionality of application, such as Daily Planner, SOS, Reports etc.");

            ansList.add("There are two ways. First through the Home 6 tile screen, there are shortcut menus that displays the most important and frequently accessed features. " +
                    "On the upper left hand corner, there are three lines which when clicked pops up the full menu of features for the app. ");

            /*ansList.add("Crisis plan can be extracted from EHR and made available to restricted " +
                        "individuals within team on consent of the peer participant. \n" +
                        "They are no specific pre-planning process required. \n" +
                        "Main reason for bringing that crisis plan from EHR three fold:\n" +
                        "\n- Now collaborative care plan could be created. Folks who don’t have access to EHR can also provide input.\n" +
                        "\n- Data / information in the crisis plan can capture latest snapshot of items which works for peer participant.\n" +
                        "\n- Folks who don’t have access to EHR who are in field with the peer participant " +
                        "and get latest and greatest information on how to amicably resolve crisis effectively.\n");*/

            ansList.add("Go to SOS Updates from the home screen > SOS Updates to view either Received SOS " +
                    "or My SOS for incoming or outgoing messages and check the progress of when and how messages are addressed.");

            ansList.add("The Received SOS section are the messages sent by a person or " +
                    "family support person on family member in behalf of a peer participant who is in crisis and sent to you " +
                    "as because you are designed emergency contact in the crisis plan. ");

            ansList.add("These are messages sent by peer participants who are in crisis and reaching out to their " +
                    "selected SOS team contacts as designated from the Crisis Plan in the emergency contact list.");

            ansList.add("Emergency contact list is setup from Team contact widget on web platform. " +
                    "In order to change emergency contact, a peer participant needs to navigate to his/her team page." +
                    " On the team details page change the emergency contact list from team contact widget.");

            ansList.add("Click on the SOS Update section > Click on SOS fab icon > You can either type your own message or click on message icon which will move " +
                    "you to a screen with a list of prepopulate messages > Select a team for which you want to send a message > Select a priority for emergency message " +
                    "from a drop down > Click on Send and the message will be delivered to the first person designated by the crisis plan emergency contact list.");

            ansList.add("The SOS message is sent to the first person designated on the SOS list. " +
                    "If they don’t respond within five minutes, the message is automatically sent to the next person on the list.  " +
                    "If no one on the SOS list responds to the message within the designated time period, " +
                    "the peer mentor is contacted. If an SOS team is not designed, the message goes directly to the peer mentor. " +
                    "Peer mentors are notified in the SOS Update section of all messages sent. " +
                    "When an SOS message is received, you can mark it “attending” “completed” or “forward” update its status. ");

            ansList.add("When you mark a message as “attending”, you will be offered options for reaching " +
                    "out to the person who sent the message. " +
                    "Select your primary method of either “call,” “meet,” “chat,” or “other”. " +
                    "You can use more than one method for responding to an SOS message but for reporting purposes, " +
                    "you need to select a primary method. " +
                    "Once you select a method you will have a time-limited window, set by your team  20 minutes to respond.");

            ansList.add("When you are attending to an SOS message, once you complete the interaction" +
                    " with the person in crisis, select “completed” methods and select “call,”  “meet,” “chat,” or “other”. " +
                    "You can add more than one method and type notes for more detail. " +
                    "Once you submit your feedback, the SOS message will be marked as addressed will be indicated by a green color. ");

            ansList.add("In both the Received SOS and My SOS sections, each message is color coded to indicate progress." +
                    "Red indicates that a person was unable to be contacted.  " +
                    "Yellow indicates a message is in the process of being addressed. " +
                    "Green indicates a message was responded to and resolved. ");

            ansList.add("SOS messages move through a specific short window of time. " +
                    "If someone misses a message within the designated time period, it is automatically " +
                    "moved on to the next person on the emergency contact list and removed from their Received SOS section.  " +
                    "You will still see the progress of the message but will not be able to respond . " +
                    "In the future, reports will be available to capture information about missed and addressed messages.");

            ansList.add("No, never and the SOS message is not a 911 replacement. " +
                    "If a peer participant is in a crisis and cannot wait, 911 should be called immediately.");

            ansList.add("In the Team Details screen of a particular Team, all the latest updates from team members are posted and includes " +
                    "the task list, announcements, events, files sharing and team discussion.");

            ansList.add("From the Team Details screen > Announcements and here you can post general updates for your specific teams.");

            ansList.add("There are 2 types of Tasks, My Task and Team Task. \n" +
                    "1. From the Daily Planer screen > You can view status for which tasks are due, completed and in progress based on date through a calendar view.\n" +
                    "2. From the Team Details screen > Task List, you can check the status of tasks which are due, completed and in progress.");

            ansList.add("From Team Details Screen > File share > For files there are permission options to read or download. " +
                    "Check with the peer participant and your team administrator if you are not able to access a file and to review  for permission options.");

            ansList.add("There are 2 types of Events, My Events and Team Events. You can view My/Team Events based on a date through a calendar view. " +
                    "Once in this section you can view Reminders for upcoming events, Today’s Events and Future Events. ");

            ansList.add("While in the calendar view, click on the plus sign in the bottom right corner. " +
                    "Add event description, start and end date and time, select team and participants.  " +
                    "You can only select one team per event or select multiple participants. " +
                    "You cannot select both a team and participants.");

            ansList.add("From Team Details Screen > Team Discussion, to view or start discussions " +
                    "on any topic related to the team activities. " +
                    "To start a discussion click on the plus sign located in the lower right-hand side. " +
                    "Type your message, select a team to start a discussion with and then post.");

            ansList.add("Team Discussion is a general discussion group that can be started on any topic and " +
                    "people can comment in a threaded conversation specific to the individual team. " +
                    "This is different than online individual and group chat conversations which happen in real time. " +
                    "General_ announcements posted don’t facilitate comment or responses and wall posts " +
                    "within teams are only seen by other team members and friends.  " +
                    "meant for everyone in the platform and for many teams.");

            /*ansList.add("Platform Messages are accessed in the Alerts section from the main menu and is " +
                        "the place Hope administrators notifies users of important software updates or maintenance to the system.");

            ansList.add("In the Alert sections with permissions, mentor coordinators can interact and message individual employees. " +
                    "This functions like a text message.");

            ansList.add("Each team member can write blog about whatever topics interest them, share " +
                    "knowledge and experience and show encouragement. " +
                    "This is a free expression section that allows the team to freely write and share " +
                    "content in a narrative format. " +
                    "Blog are typically for the providers only and on the app, you can read blog created" +
                    " previously but not write new posts.");*/

            ansList.add("Yes, the survey feature allows for all team members to upload surveys from our " +
                    "repository or create surveys from scratch to gather input from a team regarding a care plan or other intervention. " +
                    "You can launch a survey for the entire team or select individuals." +
                    "Responses are available through the platform only and to the person who created the survey.");

            ansList.add("Yes, Select Team > Team Details > Statistics review stats such as time spent, the number of crisis, " +
                    "team members, and supports. Go to Quick Poll to create and launch to ask the team a simple question such as, " +
                    "“What’s Your Mood today? The poll can only be responded to when logging on the platform for now.  ");
        } else {
            ansList.add("For providers, you can easily communicate with your team, a consumer and " +
                    "outside stakeholders involved in the treatment of the people you serve. " +
                    "It’s meant for real-time collaboration and care coordination and has social " +
                    "media type tools also available inside the platform to make this easy. " +
                    "The app is HIPAA compliant and confidential and can sync with your web platform desktop. ");

            ansList.add("Once you have a user name and password for the SageSurfer platform, " +
                    "download the free app from the Google PlayStore or iTunes. " +
                    "Open the app and log-in with your credentials and platform code.  " +
                    "Contact the team at support@sagesurfer.com with any questions.\n" +
                    "Don’t forget to enter correct platform code. " +
                    "If you don’t know the code please reach out to support team at support@sagesurfer.com " +
                    "with your platform instance details. ");

            ansList.add("The app syncs to all the same information available in your desktop " +
                    "version of the SageSurfer platform to include SOS Updates, team contacts, " +
                    "crisis plan and team sub-modules such as task lists, document files, events, team talk and announcement. " +
                    "You also have the ability to launch online individual and group chats on your mobile device. ");

            ansList.add("No, the app, like the platform, is role based and people are assigned certain " +
                    "access to pertinent information as designated by the consumer and secondarily by the platform owner.  " +
                    "There is a great deal of flexibility for the care team but, " +
                    "with consumers in control, are primarily in charge of an inviting people into the platform," +
                    " customize roles and designating who has access to information.");

            ansList.add("Your Home is the default view for all users which provides you with 6 tile easy navigation for different modules based on roles. " +
                    "You can easily access the core functionality of application, such as Daily Planner, SOS, Reports etc.");

            ansList.add("There are two ways. First through the Home 6 tile screen, there are shortcut menus that displays the most important and frequently accessed features. " +
                    "On the upper left hand corner, there are three lines which when clicked pops up the full menu of features for the app. ");

            ansList.add("Crisis plan can be extracted from EHR and made available to restricted " +
                    "individuals within team on consent of the consumer. \n" +
                    "They are no specific pre-planning process required. \n" +
                    "Main reason for bringing that crisis plan from EHR three fold:\n" +
                    "\\n- Now collaborative care plan could be created. Folks who don’t have access to EHR can also provide input.\n" +
                    "\\n- Data / information in the crisis plan can capture latest snapshot of items which works for consumer.\n" +
                    "\\n- Folks who don’t have access to EHR who are in field with the consumer " +
                    "and get latest and greatest information on how to amicably resolve crisis effectively.\n");

            ansList.add("Go to SOS Updates from the home screen > SOS Updates to view either Received SOS " +
                    "or My SOS for incoming or outgoing messages and check the progress of when and how messages are addressed.");

            ansList.add("The Received SOS section are the messages sent by a person or " +
                    "caregiver on family member in behalf of a consumer who is in crisis and sent to you " +
                    "as because you are designed emergency contact in the crisis plan. ");

            ansList.add("These are messages sent by consumers who are in crisis and reaching out to their " +
                    "selected SOS team contacts as designated from the Crisis Plan in the emergency contact list.");

            ansList.add("Emergency contact list is setup from Team contact widget on web platform. " +
                    "In order to change emergency contact, a consumer needs to navigate to his/her team page." +
                    " On the team details page change the emergency contact list from team contact widget.");

            ansList.add("Click on the SOS Update section > Click on SOS fab icon > You can either type your own message or click on message icon which will move " +
                    "you to a screen with a list of prepopulate messages > Select a team for which you want to send a message > Select a priority for emergency message " +
                    "from a drop down > Click on Send and the message will be delivered to the first person designated by the crisis plan emergency contact list.");

            ansList.add("The SOS message is sent to the first person designated on the SOS list. " +
                    "If they don’t respond within five minutes, the message is automatically sent to the next person on the list.  " +
                    "If no one on the SOS list responds to the message within the designated time period, " +
                    "the care coordinator is contacted. If an SOS team is not designed, the message goes directly to the care coordinator. " +
                    "Care coordinators are notified in the SOS Update section of all messages sent. " +
                    "When an SOS message is received, you can mark it “attending” “completed” or “forward” update its status. ");

            ansList.add("When you mark a message as “attending”, you will be offered options for reaching " +
                    "out to the person who sent the message. " +
                    "Select your primary method of either “call,” “meet,” “chat,” or “other”. " +
                    "You can use more than one method for responding to an SOS message but for reporting purposes, " +
                    "you need to select a primary method. " +
                    "Once you select a method you will have a time-limited window, set by your team  20 minutes to respond.");

            ansList.add("When you are attending to an SOS message, once you complete the interaction" +
                    " with the person in crisis, select “completed” methods and select “call,”  “meet,” “chat,” or “other”. " +
                    "You can add more than one method and type notes for more detail. " +
                    "Once you submit your feedback, the SOS message will be marked as addressed will be indicated by a green color. ");

            ansList.add("In both the Received SOS and My SOS sections, each message is color coded to indicate progress." +
                    "Red indicates that a person was unable to be contacted.  " +
                    "Yellow indicates a message is in the process of being addressed. " +
                    "Green indicates a message was responded to and resolved. ");

            ansList.add("SOS messages move through a specific short window of time. " +
                    "If someone misses a message within the designated time period, it is automatically " +
                    "moved on to the next person on the emergency contact list and removed from their Received SOS section.  " +
                    "You will still see the progress of the message but will not be able to respond . " +
                    "In the future, reports will be available to capture information about missed and addressed messages.");

            ansList.add("No, never and the SOS message is not a 911 replacement. " +
                    "If a consumer is in a crisis and cannot wait, 911 should be called immediately.");

            ansList.add("In the Team Details screen of a particular Team, all the latest updates from team members are posted and includes " +
                    "the task list, announcements, events, files sharing and team talk.");

            ansList.add("From the Team Details screen > Announcements and here you can post general updates for your specific teams.");

            ansList.add("There are 2 types of Tasks, My Task and Team Task. \n" +
                    "1. From the Daily Planer screen > You can view status for which tasks are due, completed and in progress based on date through a calendar view.\n" +
                    "2. From the Team Details screen > Task List, you can check the status of tasks which are due, completed and in progress.");

            ansList.add("From Team Details Screen > File share > For files there are permission options to read or download. " +
                    "Check with the consumer and your team administrator if you are not able to access a file and to review  for permission options.");

            ansList.add("There are 2 types of Events, My Events and Team Events. You can view My/Team Events based on a date through " +
                    "a calendar view. Once in this section you can view Reminders for upcoming events, Today’s Events and Future Events.");

            ansList.add("While in the calendar view, click on the plus sign in the bottom right corner. " +
                    "Add event description, start and end date and time, select team and participants.  " +
                    "You can only select one team per event or select multiple participants. " +
                    "You cannot select both a team and participants.");

            ansList.add("From Team Details Screen > Team Talk, to view or start discussions " +
                    "on any topic related to the team activities. " +
                    "To start a discussion click on the plus sign located in the lower right-hand side. " +
                    "Type your message, select a team to start a discussion with and then post.");

            ansList.add("Team Talk is a general discussion group that can be started on any topic and " +
                    "people can comment in a threaded conversation specific to the individual team. " +
                    "This is different than online individual and group chat conversations which happen in real time. " +
                    "General_ announcements posted don’t facilitate comment or responses and wall posts " +
                    "within teams are only seen by other team members and friends.  " +
                    "meant for everyone in the platform and for many teams.");

            /*ansList.add("Platform Messages are accessed in the Alerts section from the main menu and is " +
                        "the place SageSurfer administrators notifies users of important software updates or maintenance to the system.");

            ansList.add("In the Alert sections with permissions, supervisors can interact and message individual employees. " +
                    "This functions like a text message.");

            ansList.add("Each team member can write blog about whatever topics interest them, share " +
                    "knowledge and experience and show encouragement. " +
                    "This is a free expression section that allows the team to freely write and share " +
                    "content in a narrative format. " +
                    "Blog are typically for the providers only and on the app, you can read blog created" +
                    " previously but not write new posts.");*/

            ansList.add("Yes, the survey feature allows for all team members to upload surveys from our " +
                    "repository or create surveys from scratch to gather input from a team regarding a care plan or other intervention. " +
                    "You can launch a survey for the entire team or select individuals." +
                    "Responses are available through the platform only and to the person who created the survey.");

            ansList.add("Yes, Select Team > Team Details > Statistics review stats such as time spent, the number of crisis, " +
                    "team members, and supports. Go to Quick Poll to create and launch to ask the team a simple question such as, " +
                    "“What’s Your Mood today? The poll can only be responded to when logging on the platform for now.  ");
        }

        return ansList;
    }
}
