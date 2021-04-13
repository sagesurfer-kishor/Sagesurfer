package com.modules.cometchat_7_30.LastConversion;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.uikit.CometChatConversationList;
import com.modules.cometchat_7_30.CometChatFriendsListFragment_;
import com.sagesurfer.adapters.FriendListAdapter;
import com.sagesurfer.collaborativecares.MainActivity;
import com.sagesurfer.collaborativecares.R;
import com.sagesurfer.interfaces.MainActivityInterface;

import java.util.ArrayList;
import java.util.List;

import listeners.OnItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentLastConversation#newInstance} factory method to
 * create an instance of this fragment.
 * created by rahul maske
 */
public class FragmentLastConversation extends Fragment  {
    private static final String TAG = "FragmentLastConversatio";
    ConversationsRequest conversationsRequest;
    private RecyclerView recyclerView;
    private String conversationListType = null;
    private static OnItemClickListener events;
    private List<Conversation> conversationList = new ArrayList<>();
    private AdapterLastConversation adapter;
    Toolbar toolbar;
    private FragmentActivity mContext;
    Activity activity;
    private MainActivityInterface mainActivityInterface;
    public FragmentLastConversation() {
        // Required empty public constructor
    }

    public static FragmentLastConversation newInstance(String param1, String param2) {
        FragmentLastConversation fragment = new FragmentLastConversation();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_last_conversation, container, false);
        recyclerView =view.findViewById(R.id.rv_last_conversion);
        activity=getActivity();
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            /*if (BuildConfig.FLAVOR.equalsIgnoreCase("senjam")) {
                mainActivity.hideLogBookIcon(true);
            } else {
                mainActivity.hideLogBookIcon(false);
            }*/
            mainActivity.showHideBellIcon2(true);
            //mainActivity.showChatIcon(true);
            mainActivity.hidesettingIcon(true);
        }
        makeConversationList();
// Uses to fetch next list of conversations if rvConversationList (RecyclerView) is scrolled in upward direction.
      /*  recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(1)) {
                    makeConversationList();
                }
            }
        });*/
        return view;
    }

    /**
     * This method is used to retrieve list of conversations you have done.
     * For more detail please visit our official documentation {@link "https://prodocs.cometchat.com/docs/android-messaging-retrieve-conversations" }
     *
     * @see ConversationsRequest
     */
    private void makeConversationList() {

        if (conversationsRequest == null) {
            conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setLimit(50).build();
            if (conversationListType!=null)
                conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder()
                        .setConversationType(conversationListType).setLimit(50).build();
        }

        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                conversationList.addAll(conversations);
                if (conversationList.size() != 0) {
                    Log.d(TAG, "onSuccess: makeConversationList "+conversationList);
                    //adapter = new AdapterLastConversation(CometChatFriendsListFragment_.this, getContext(), friendList, al_unreadCountList);
                    adapter = new AdapterLastConversation(FragmentLastConversation.this, conversationList, getActivity() );
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.d(TAG, "onSuccess makeConversationList data not found ");
                    recyclerView.setVisibility(View.GONE);
                    //error.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onError(CometChatException e) {
                Log.i(TAG, "makeConversationList onError: ");
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (FragmentActivity) context;
        mainActivityInterface = (MainActivityInterface) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        mainActivityInterface.setMainTitle("Last Conversation");
        mainActivityInterface.setToolbarBackgroundColor();
    }
}