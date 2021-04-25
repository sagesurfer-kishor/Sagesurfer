package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.modules.assignment.werhope.model.School;
import com.modules.journaling.model.Student_;
import com.sagesurfer.models.CaseloadPeerNoteAmendments_;
import com.sagesurfer.models.CaseloadPeerNoteComments_;
import com.sagesurfer.models.CaseloadPeerNoteReject_;
import com.sagesurfer.models.CaseloadPeerNoteViewLog_;
import com.sagesurfer.models.CaseloadPeerNote_;
import com.sagesurfer.models.CaseloadProgressNote_;
import com.sagesurfer.models.Caseload_;
import com.sagesurfer.models.Members_;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Monika on 6/1/2018.
 */

public class CaseloadParser_ {

    public static ArrayList<Caseload_> parseCaseload(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Caseload_> caseloadArrayList = new ArrayList<>();
        try {
            if (response == null) {
                Caseload_ caseloadItem_ = new Caseload_();
                caseloadItem_.setStatus(12);
                caseloadArrayList.add(caseloadItem_);
                return caseloadArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                Caseload_ caseloadItem_ = new Caseload_();
                caseloadItem_.setStatus(13);
                caseloadArrayList.add(caseloadItem_);
                return caseloadArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                Caseload_ caseloadItem_ = new Caseload_();
                caseloadItem_.setStatus(2);
                caseloadArrayList.add(caseloadItem_);
                return caseloadArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
//                Gson gson = new Gson();
                Gson gson = new GsonBuilder()
                        .registerTypeAdapterFactory(UNRELIABLE_INTEGER_FACTORY)
                        .create();
                Type listType = new TypeToken<List<Caseload_>>() {
                }.getType();
                caseloadArrayList = gson.fromJson(GetJson_.getArray(response, jsonName), listType);
            } else {
                Caseload_ caseloadItem_ = new Caseload_();
                caseloadItem_.setStatus(11);
                caseloadArrayList.add(caseloadItem_);
                return caseloadArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return caseloadArrayList;
    }

    public static final TypeAdapter<Number> UNRELIABLE_INTEGER = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader in) throws IOException {
            JsonToken jsonToken = in.peek();
            switch (jsonToken) {
                case NUMBER:
                case STRING:
                    String s = in.nextString();
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        return (int)Double.parseDouble(s);
                    } catch (NumberFormatException ignored) {
                    }
                    return null;
                case NULL:
                    in.nextNull();
                    return null;
                case BOOLEAN:
                    in.nextBoolean();
                    return null;
                default:
                    throw new JsonSyntaxException("Expecting number, got: " + jsonToken);
            }
        }
        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value);
        }
    };
    public static final TypeAdapterFactory UNRELIABLE_INTEGER_FACTORY = TypeAdapters.newFactory(int.class, Integer.class, UNRELIABLE_INTEGER);

    public static ArrayList<CaseloadProgressNote_> parseProgressNote(String response, String jsonName, Context _context, String TAG) {
        ArrayList<CaseloadProgressNote_> caseloadProgressNoteArrayList = new ArrayList<>();
        try {
            if (response == null) {
                CaseloadProgressNote_ caseloadProgressNoteItem_ = new CaseloadProgressNote_();
                caseloadProgressNoteItem_.setStatus(12);
                caseloadProgressNoteArrayList.add(caseloadProgressNoteItem_);
                return caseloadProgressNoteArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                CaseloadProgressNote_ caseloadProgressNoteItem_ = new CaseloadProgressNote_();
                caseloadProgressNoteItem_.setStatus(13);
                caseloadProgressNoteArrayList.add(caseloadProgressNoteItem_);
                return caseloadProgressNoteArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                CaseloadProgressNote_ caseloadProgressNoteItem_ = new CaseloadProgressNote_();
                caseloadProgressNoteItem_.setStatus(2);
                caseloadProgressNoteArrayList.add(caseloadProgressNoteItem_);
                return caseloadProgressNoteArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<CaseloadProgressNote_>>() {
                }.getType();
                caseloadProgressNoteArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                CaseloadProgressNote_ caseloadProgressNoteItem_ = new CaseloadProgressNote_();
                caseloadProgressNoteItem_.setStatus(11);
                caseloadProgressNoteArrayList.add(caseloadProgressNoteItem_);
                return caseloadProgressNoteArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return caseloadProgressNoteArrayList;
    }

    public static ArrayList<CaseloadPeerNote_> parsePeerNote(String response, String jsonName, Context _context, String TAG) {
        ArrayList<CaseloadPeerNote_> caseloadPeerNoteArrayList = new ArrayList<>();
        try {
            if (response == null) {
                CaseloadPeerNote_ caseloadPeerNoteItem_ = new CaseloadPeerNote_();
                caseloadPeerNoteItem_.setStatus(12);
                caseloadPeerNoteArrayList.add(caseloadPeerNoteItem_);
                return caseloadPeerNoteArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                CaseloadPeerNote_ caseloadPeerNoteItem_ = new CaseloadPeerNote_();
                caseloadPeerNoteItem_.setStatus(13);
                caseloadPeerNoteArrayList.add(caseloadPeerNoteItem_);
                return caseloadPeerNoteArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                CaseloadPeerNote_ caseloadPeerNoteItem_ = new CaseloadPeerNote_();
                caseloadPeerNoteItem_.setStatus(2);
                caseloadPeerNoteArrayList.add(caseloadPeerNoteItem_);
                return caseloadPeerNoteArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<CaseloadPeerNote_>>() {
                }.getType();
                caseloadPeerNoteArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                CaseloadPeerNote_ caseloadPeerNoteItem_ = new CaseloadPeerNote_();
                caseloadPeerNoteItem_.setStatus(11);
                caseloadPeerNoteArrayList.add(caseloadPeerNoteItem_);
                return caseloadPeerNoteArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return caseloadPeerNoteArrayList;
    }

    public static ArrayList<CaseloadPeerNote_> parseAllPeerNote(String response, String jsonName, Context _context, String TAG) {
        ArrayList<CaseloadPeerNote_> caseloadPeerNoteArrayList = new ArrayList<>();
        try {
            if (response == null) {
                CaseloadPeerNote_ caseloadPeerNoteItem_ = new CaseloadPeerNote_();
                caseloadPeerNoteItem_.setStatus(12);
                caseloadPeerNoteArrayList.add(caseloadPeerNoteItem_);
                return caseloadPeerNoteArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                CaseloadPeerNote_ caseloadPeerNoteItem_ = new CaseloadPeerNote_();
                caseloadPeerNoteItem_.setStatus(13);
                caseloadPeerNoteArrayList.add(caseloadPeerNoteItem_);
                return caseloadPeerNoteArrayList;
            }

            JsonParser parser = new JsonParser();
            JsonObject jObject = parser.parse(response).getAsJsonObject();
            JsonObject JsonObjectAllNotes = jObject.getAsJsonObject(jsonName);
            JsonArray jsonArray = JsonObjectAllNotes.getAsJsonArray("data");

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<CaseloadPeerNote_>>() {
                }.getType();
                caseloadPeerNoteArrayList = gson.fromJson(jsonArray, listType);
            } else {
                CaseloadPeerNote_ caseloadPeerNoteItem_ = new CaseloadPeerNote_();
                caseloadPeerNoteItem_.setStatus(11);
                caseloadPeerNoteArrayList.add(caseloadPeerNoteItem_);
                return caseloadPeerNoteArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return caseloadPeerNoteArrayList;
    }

    public static ArrayList<Members_> parsePeerMentor(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Members_> caseloadPeerMentorArrayList = new ArrayList<>();
        try {
            if (response == null) {
                Members_ caseloadPeerMentorItem_ = new Members_();
                caseloadPeerMentorItem_.setStatus(12);
                caseloadPeerMentorArrayList.add(caseloadPeerMentorItem_);
                return caseloadPeerMentorArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                Members_ caseloadPeerMentorItem_ = new Members_();
                caseloadPeerMentorItem_.setStatus(13);
                caseloadPeerMentorArrayList.add(caseloadPeerMentorItem_);
                return caseloadPeerMentorArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                Members_ caseloadPeerMentorItem_ = new Members_();
                caseloadPeerMentorItem_.setStatus(2);
                caseloadPeerMentorArrayList.add(caseloadPeerMentorItem_);
                return caseloadPeerMentorArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Members_>>() {
                }.getType();
                caseloadPeerMentorArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                Members_ caseloadPeerMentorItem_ = new Members_();
                caseloadPeerMentorItem_.setStatus(11);
                caseloadPeerMentorArrayList.add(caseloadPeerMentorItem_);
                return caseloadPeerMentorArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return caseloadPeerMentorArrayList;
    }


    public static ArrayList<Student_> parseStudentList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Student_> studentArrayList = new ArrayList<>();
        try {
            if (response == null) {
                Student_ student_ = new Student_();
                student_.setStatus(12);
                studentArrayList.add(student_);
                return studentArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                Student_ student_ = new Student_();
                student_.setStatus(13);
                studentArrayList.add(student_);
                return studentArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                Student_ student_ = new Student_();
                student_.setStatus(2);
                studentArrayList.add(student_);
                return studentArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Student_>>() {
                }.getType();
                studentArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                Student_ student_ = new Student_();
                student_.setStatus(11);
                studentArrayList.add(student_);
                return studentArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentArrayList;
    }

    public static ArrayList<School> parseSchoolList(String response, String jsonName, Context _context, String TAG) {
        ArrayList<School> schoolArrayList = new ArrayList<>();
        try {
            if (response == null) {
                School school = new School();
                school.setStatus(12);
                schoolArrayList.add(school);
                return schoolArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                School school = new School();
                school.setStatus(13);
                schoolArrayList.add(school);
                return schoolArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                School school = new School();
                school.setStatus(2);
                schoolArrayList.add(school);
                return schoolArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<School>>() {
                }.getType();
                schoolArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                School school = new School();
                school.setStatus(11);
                schoolArrayList.add(school);
                return schoolArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schoolArrayList;
    }


    public static ArrayList<CaseloadPeerNoteViewLog_> parseViewLog(String response, String jsonName, Context _context, String TAG) {
        ArrayList<CaseloadPeerNoteViewLog_> caseloadPeerNoteViewLogArrayList = new ArrayList<>();
        try {
            if (response == null) {
                CaseloadPeerNoteViewLog_ caseloadPeerNoteViewLogItem_ = new CaseloadPeerNoteViewLog_();
                caseloadPeerNoteViewLogItem_.setStatus(12);
                caseloadPeerNoteViewLogArrayList.add(caseloadPeerNoteViewLogItem_);
                return caseloadPeerNoteViewLogArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                CaseloadPeerNoteViewLog_ caseloadPeerNoteViewLogItem_ = new CaseloadPeerNoteViewLog_();
                caseloadPeerNoteViewLogItem_.setStatus(13);
                caseloadPeerNoteViewLogArrayList.add(caseloadPeerNoteViewLogItem_);
                return caseloadPeerNoteViewLogArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                CaseloadPeerNoteViewLog_ caseloadPeerNoteViewLogItem_ = new CaseloadPeerNoteViewLog_();
                caseloadPeerNoteViewLogItem_.setStatus(2);
                caseloadPeerNoteViewLogArrayList.add(caseloadPeerNoteViewLogItem_);
                return caseloadPeerNoteViewLogArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<CaseloadPeerNoteViewLog_>>() {
                }.getType();
                caseloadPeerNoteViewLogArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                CaseloadPeerNoteViewLog_ caseloadPeerNoteViewLogItem_ = new CaseloadPeerNoteViewLog_();
                caseloadPeerNoteViewLogItem_.setStatus(11);
                caseloadPeerNoteViewLogArrayList.add(caseloadPeerNoteViewLogItem_);
                return caseloadPeerNoteViewLogArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return caseloadPeerNoteViewLogArrayList;
    }

    public static ArrayList<CaseloadPeerNoteAmendments_> parseAmendments(String response, String jsonName, Context _context, String TAG) {
        ArrayList<CaseloadPeerNoteAmendments_> caseloadPeerNoteAmendmentsArrayList = new ArrayList<>();
        try {
            if (response == null) {
                CaseloadPeerNoteAmendments_ caseloadPeerNoteAmendmentsItem_ = new CaseloadPeerNoteAmendments_();
                caseloadPeerNoteAmendmentsItem_.setStatus(12);
                caseloadPeerNoteAmendmentsArrayList.add(caseloadPeerNoteAmendmentsItem_);
                return caseloadPeerNoteAmendmentsArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                CaseloadPeerNoteAmendments_ caseloadPeerNoteAmendmentsItem_ = new CaseloadPeerNoteAmendments_();
                caseloadPeerNoteAmendmentsItem_.setStatus(13);
                caseloadPeerNoteAmendmentsArrayList.add(caseloadPeerNoteAmendmentsItem_);
                return caseloadPeerNoteAmendmentsArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                CaseloadPeerNoteAmendments_ caseloadPeerNoteAmendmentsItem_ = new CaseloadPeerNoteAmendments_();
                caseloadPeerNoteAmendmentsItem_.setStatus(2);
                caseloadPeerNoteAmendmentsArrayList.add(caseloadPeerNoteAmendmentsItem_);
                return caseloadPeerNoteAmendmentsArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<CaseloadPeerNoteAmendments_>>() {
                }.getType();
                caseloadPeerNoteAmendmentsArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                CaseloadPeerNoteAmendments_ caseloadPeerNoteAmendmentsItem_ = new CaseloadPeerNoteAmendments_();
                caseloadPeerNoteAmendmentsItem_.setStatus(11);
                caseloadPeerNoteAmendmentsArrayList.add(caseloadPeerNoteAmendmentsItem_);
                return caseloadPeerNoteAmendmentsArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return caseloadPeerNoteAmendmentsArrayList;
    }

    public static ArrayList<CaseloadPeerNoteComments_> parseComments(String response, String jsonName, Context _context, String TAG) {
        ArrayList<CaseloadPeerNoteComments_> caseloadPeerNoteCommentsArrayList = new ArrayList<>();
        try {
            if (response == null) {
                CaseloadPeerNoteComments_ caseloadPeerNoteCommentsItem_ = new CaseloadPeerNoteComments_();
                caseloadPeerNoteCommentsItem_.setStatus(12);
                caseloadPeerNoteCommentsArrayList.add(caseloadPeerNoteCommentsItem_);
                return caseloadPeerNoteCommentsArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                CaseloadPeerNoteComments_ caseloadPeerNoteCommentsItem_ = new CaseloadPeerNoteComments_();
                caseloadPeerNoteCommentsItem_.setStatus(13);
                caseloadPeerNoteCommentsArrayList.add(caseloadPeerNoteCommentsItem_);
                return caseloadPeerNoteCommentsArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                CaseloadPeerNoteComments_ caseloadPeerNoteCommentsItem_ = new CaseloadPeerNoteComments_();
                caseloadPeerNoteCommentsItem_.setStatus(2);
                caseloadPeerNoteCommentsArrayList.add(caseloadPeerNoteCommentsItem_);
                return caseloadPeerNoteCommentsArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<CaseloadPeerNoteComments_>>() {
                }.getType();
                caseloadPeerNoteCommentsArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                CaseloadPeerNoteComments_ caseloadPeerNoteCommentsItem_ = new CaseloadPeerNoteComments_();
                caseloadPeerNoteCommentsItem_.setStatus(11);
                caseloadPeerNoteCommentsArrayList.add(caseloadPeerNoteCommentsItem_);
                return caseloadPeerNoteCommentsArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return caseloadPeerNoteCommentsArrayList;
    }

    public static ArrayList<CaseloadPeerNoteReject_> parseRejectReasons(String response, String jsonName, Context _context, String TAG) {
        ArrayList<CaseloadPeerNoteReject_> caseloadPeerNoteRejectArrayList = new ArrayList<>();
        try {
            if (response == null) {
                CaseloadPeerNoteReject_ caseloadPeerNoteRejectItem_ = new CaseloadPeerNoteReject_();
                caseloadPeerNoteRejectItem_.setStatus(12);
                caseloadPeerNoteRejectArrayList.add(caseloadPeerNoteRejectItem_);
                return caseloadPeerNoteRejectArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                CaseloadPeerNoteReject_ caseloadPeerNoteRejectItem_ = new CaseloadPeerNoteReject_();
                caseloadPeerNoteRejectItem_.setStatus(13);
                caseloadPeerNoteRejectArrayList.add(caseloadPeerNoteRejectItem_);
                return caseloadPeerNoteRejectArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                CaseloadPeerNoteReject_ caseloadPeerNoteRejectItem_ = new CaseloadPeerNoteReject_();
                caseloadPeerNoteRejectItem_.setStatus(2);
                caseloadPeerNoteRejectArrayList.add(caseloadPeerNoteRejectItem_);
                return caseloadPeerNoteRejectArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<CaseloadPeerNoteReject_>>() {
                }.getType();
                caseloadPeerNoteRejectArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                CaseloadPeerNoteReject_ caseloadPeerNoteRejectItem_ = new CaseloadPeerNoteReject_();
                caseloadPeerNoteRejectItem_.setStatus(11);
                caseloadPeerNoteRejectArrayList.add(caseloadPeerNoteRejectItem_);
                return caseloadPeerNoteRejectArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return caseloadPeerNoteRejectArrayList;
    }

    public static ArrayList<CaseloadProgressNote_> parseProgressNoteDetails(String response, String jsonName, Context _context, String TAG) {
        ArrayList<CaseloadProgressNote_> caseloadProgressNoteArrayList = new ArrayList<>();
        try {
            if (response == null) {
                CaseloadProgressNote_ caseloadProgressNoteItem_ = new CaseloadProgressNote_();
                caseloadProgressNoteItem_.setStatus(12);
                caseloadProgressNoteArrayList.add(caseloadProgressNoteItem_);
                return caseloadProgressNoteArrayList;
            }
            if (Error_.oauth(response, _context) == 13) {
                CaseloadProgressNote_ caseloadProgressNoteItem_ = new CaseloadProgressNote_();
                caseloadProgressNoteItem_.setStatus(13);
                caseloadProgressNoteArrayList.add(caseloadProgressNoteItem_);
                return caseloadProgressNoteArrayList;
            }
            if (Error_.noData(response, jsonName, _context) == 2) {
                CaseloadProgressNote_ caseloadProgressNoteItem_ = new CaseloadProgressNote_();
                caseloadProgressNoteItem_.setStatus(2);
                caseloadProgressNoteArrayList.add(caseloadProgressNoteItem_);
                return caseloadProgressNoteArrayList;
            }
            JsonArray jsonArray = GetJson_.getArray(response, jsonName);

            if (jsonArray != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<CaseloadProgressNote_>>() {
                }.getType();
                caseloadProgressNoteArrayList = gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
            } else {
                CaseloadProgressNote_ caseloadProgressNoteItem_ = new CaseloadProgressNote_();
                caseloadProgressNoteItem_.setStatus(11);
                caseloadProgressNoteArrayList.add(caseloadProgressNoteItem_);
                return caseloadProgressNoteArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return caseloadProgressNoteArrayList;
    }
}
