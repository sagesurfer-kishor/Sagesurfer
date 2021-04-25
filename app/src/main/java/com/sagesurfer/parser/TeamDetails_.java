package com.sagesurfer.parser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.modules.blog.Blog_;
import com.modules.team.Poll_;
import com.sagesurfer.constant.Actions_;
import com.sagesurfer.models.Contacts_;
import com.sagesurfer.models.EmergencyParent_;
import com.sagesurfer.models.Gallery_;
import com.sagesurfer.models.Images_;
import com.sagesurfer.models.Video_;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Monika M (monikam@sagesurfer.com)
 * Created on 13/03/2018
 * Last Modified on
 */

public class TeamDetails_ {

    public static ArrayList<Contacts_> parseContacts(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Contacts_> contactList = new ArrayList<>();
        if (response == null) {
            Contacts_ contacts_ = new Contacts_();
            contacts_.setStatus(11);
            contactList.add(contacts_);
            return contactList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Contacts_ contacts_ = new Contacts_();
            contacts_.setStatus(13);
            contactList.add(contacts_);
            return contactList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            Contacts_ contacts_ = new Contacts_();
            contacts_.setStatus(2);
            contactList.add(contacts_);
            return contactList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Contacts_>>() {
            }.getType();
            contactList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        Collections.sort(contactList, new Comparator<Contacts_>() {
            @Override
            public int compare(Contacts_ s1, Contacts_ s2) {
                return s1.getFirstName().compareToIgnoreCase(s2.getFirstName());
            }
        });
        return contactList;
    }

    public static ArrayList<EmergencyParent_> parseEmergencyContacts(String response, String jsonName,
                                                                     String TAG, Context _context) {
        ArrayList<EmergencyParent_> emergencyParentArrayList = new ArrayList<>();
        if (response == null) {
            EmergencyParent_ emergencyParent = new EmergencyParent_();
            emergencyParent.setStatus(11);
            emergencyParentArrayList.add(emergencyParent);
            return emergencyParentArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            EmergencyParent_ emergencyParent = new EmergencyParent_();
            emergencyParent.setStatus(13);
            emergencyParentArrayList.add(emergencyParent);
            return emergencyParentArrayList;
        }

        if (Error_.noData(response, jsonName, _context) == 2) {
            EmergencyParent_ emergencyParent = new EmergencyParent_();
            emergencyParent.setStatus(2);
            emergencyParentArrayList.add(emergencyParent);
            return emergencyParentArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<EmergencyParent_>>() {
            }.getType();
            emergencyParentArrayList = gson.fromJson(GetJson_.getArray(response, jsonName)
                    .toString(), listType);
        }
        return emergencyParentArrayList;
    }

    public static ArrayList<Poll_> parsePoll(String response, Context _context, String TAG) {
        ArrayList<Poll_> pollList = new ArrayList<>();
        if (response == null) {
            Poll_ poll_ = new Poll_();
            poll_.setStatus(11);
            pollList.add(poll_);
            return pollList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Poll_ poll_ = new Poll_();
            poll_.setStatus(13);
            pollList.add(poll_);
            return pollList;
        }

        if (Error_.noData(response, "poll", _context) == 2) {
            Poll_ poll_ = new Poll_();
            poll_.setStatus(2);
            pollList.add(poll_);
            return pollList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Poll_>>() {
            }.getType();
            pollList = gson.fromJson(GetJson_.getArray(response, "poll")
                    .toString(), listType);
        }
        return pollList;
    }

    public static ArrayList<Gallery_> parseCreateAlbumGallery(String response, Context _context, String TAG) {
        ArrayList<Gallery_> galleryList = new ArrayList<>();
        if (response == null) {
            Gallery_ gallery_ = new Gallery_();
            gallery_.setStatus(11);
            galleryList.add(gallery_);
            return galleryList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Gallery_ gallery_ = new Gallery_();
            gallery_.setStatus(13);
            galleryList.add(gallery_);
            return galleryList;
        }

        if (Error_.noData(response, Actions_.CREATE_GALLERY, _context) == 2) {
            Gallery_ gallery_ = new Gallery_();
            gallery_.setStatus(2);
            galleryList.add(gallery_);
            return galleryList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Gallery_>>() {
            }.getType();
            galleryList = gson.fromJson(GetJson_.getArray(response, Actions_.CREATE_GALLERY)
                    .toString(), listType);
        }
        return galleryList;
    }

    public static ArrayList<Images_> parseUploadAlbumGallery(String response, Context _context, String TAG) {
        ArrayList<Images_> galleryList = new ArrayList<>();
        if (response == null) {
            Images_ gallery_ = new Images_();
            gallery_.setStatus(11);
            galleryList.add(gallery_);
            return galleryList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Images_ gallery_ = new Images_();
            gallery_.setStatus(13);
            galleryList.add(gallery_);
            return galleryList;
        }

        if (Error_.noData(response, Actions_.UPLOAD_GALLERY, _context) == 2) {
            Images_ gallery_ = new Images_();
            gallery_.setStatus(2);
            galleryList.add(gallery_);
            return galleryList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Images_>>() {
            }.getType();
            galleryList = gson.fromJson(GetJson_.getArray(response, Actions_.UPLOAD_GALLERY)
                    .toString(), listType);
        }
        return galleryList;
    }

    public static ArrayList<Gallery_> parseGallery(String response, Context _context, String TAG) {
        ArrayList<Gallery_> galleryList = new ArrayList<>();
        if (response == null) {
            Gallery_ gallery_ = new Gallery_();
            gallery_.setStatus(11);
            gallery_.setSelectImgs(false);
            galleryList.add(gallery_);
            return galleryList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Gallery_ gallery_ = new Gallery_();
            gallery_.setStatus(13);
            gallery_.setSelectImgs(false);
            galleryList.add(gallery_);
            return galleryList;
        }

        if (Error_.noData(response, "gallery", _context) == 2) {
            Gallery_ gallery_ = new Gallery_();
            gallery_.setStatus(2);
            gallery_.setSelectImgs(false);
            galleryList.add(gallery_);
            return galleryList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Gallery_>>() {
            }.getType();
            galleryList = gson.fromJson(GetJson_.getArray(response, "gallery")
                    .toString(), listType);
        }
        return galleryList;
    }

    public static ArrayList<Video_> parseVideo(String response, Context _context, String TAG) {
        ArrayList<Video_> videoList = new ArrayList<>();
        if (response == null) {
            Video_ video_ = new Video_();
            video_.setStatus(11);
            videoList.add(video_);
            return videoList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Video_ video_ = new Video_();
            video_.setStatus(13);
            videoList.add(video_);
            return videoList;
        }

        if (Error_.noData(response, "video", _context) == 2) {
            Video_ video_ = new Video_();
            video_.setStatus(2);
            videoList.add(video_);
            return videoList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Video_>>() {
            }.getType();
            videoList = gson.fromJson(GetJson_.getArray(response, "video")
                    .toString(), listType);
        }
        return videoList;
    }

    public static ArrayList<Images_> parseImages(String response, Context _context, String TAG) {
        ArrayList<Images_> imagesArrayList = new ArrayList<>();
        if (response == null) {
            Images_ images_ = new Images_();
            images_.setStatus(11);
            images_.setSelectImgs(false);
            imagesArrayList.add(images_);
            return imagesArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Images_ images_ = new Images_();
            images_.setStatus(13);
            images_.setSelectImgs(false);
            imagesArrayList.add(images_);
            return imagesArrayList;
        }

        if (Error_.noData(response, "get_gallery_photos", _context) == 2) {
            Images_ images_ = new Images_();
            images_.setStatus(2);
            images_.setSelectImgs(false);
            imagesArrayList.add(images_);
            return imagesArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Images_>>() {
            }.getType();
            imagesArrayList = gson.fromJson(GetJson_.getArray(response, "get_gallery_photos")
                    .toString(), listType);
        }
        return imagesArrayList;
    }

    public static ArrayList<Images_> parseImagesComments(String response, String jsonName, Context _context, String TAG) {
        ArrayList<Images_> imagessArrayList = new ArrayList<>();
        if (response == null) {
            Images_ images_ = new Images_();
            images_.setStatus(12);
            imagessArrayList.add(images_);
            return imagessArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Images_ images_ = new Images_();
            images_.setStatus(13);
            imagessArrayList.add(images_);
            return imagessArrayList;
        }
        if (Error_.noData(response, jsonName, _context) == 2) {
            Images_ images_ = new Images_();
            images_.setStatus(2);
            imagessArrayList.add(images_);
            return imagessArrayList;
        }
        JsonArray jsonArray = GetJson_.getArray(response, jsonName);

        if (jsonArray != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Images_>>() {
            }.getType();
            return gson.fromJson(GetJson_.getArray(response, jsonName).toString(), listType);
        } else {
            Images_ images_ = new Images_();
            images_.setStatus(11);
            imagessArrayList.add(images_);
            return imagessArrayList;
        }
    }

    public static ArrayList<Blog_> parseBlog(String response, Context _context, String TAG) {
        ArrayList<Blog_> blogArrayList = new ArrayList<>();
        if (response == null) {
            Blog_ blog_ = new Blog_();
            blog_.setStatus(11);
            blogArrayList.add(blog_);
            return blogArrayList;
        }
        if (Error_.oauth(response, _context) == 13) {
            Blog_ blog_ = new Blog_();
            blog_.setStatus(13);
            blogArrayList.add(blog_);
            return blogArrayList;
        }

        if (Error_.noData(response, "blog", _context) == 2) {
            Blog_ blog_ = new Blog_();
            blog_.setStatus(2);
            blogArrayList.add(blog_);
            return blogArrayList;
        }
        if (Error_.oauth(response, _context) != 13) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Blog_>>() {
            }.getType();
            blogArrayList = gson.fromJson(GetJson_.getArray(response, "blog")
                    .toString(), listType);
        }
        return blogArrayList;
    }

}
