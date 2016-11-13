package com.taskr.client;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.transition.Transition;

/**
 * Created by guillaumelam34 on 11/12/2016.
 */

public class TransitionFactory {
    private static Context mContext;

    public TransitionFactory(Context context){
        mContext = context;
    }

    //WIP
    //inFragment is the fragment that is transitioned FROM
    //outFragment is the fragment that is transitioned TO
    public void setCustomAnimations(FragmentTransaction transaction, TransitionParams transitionParams){
        int enter = 0;
        int exit = 0;
        int popEnter = 0;
        int popExit = 0;

        String inFragment = transitionParams.inFragment;
        String outFragment = transitionParams.outFragment;

        if(inFragment.equals(mContext.getString(R.string.login_fragment_tag))){
            exit = R.anim.slide_out_right;
            popEnter = R.anim.slide_in_left;
        }else if(inFragment.equals(mContext.getString(R.string.profile_fragment_tag))){
            exit = R.anim.slide_out_bottom;
            popEnter = R.anim.slide_in_bottom;
        }else if(inFragment.equals(mContext.getString(R.string.request_fragment_tag))){
            exit = R.anim.slide_out_bottom;
            popEnter = R.anim.slide_in_bottom;
        }else if(inFragment.equals(mContext.getString(R.string.request_overview_fragment_tag))){
            exit = R.anim.slide_out_bottom;
            popEnter = R.anim.slide_in_bottom;
        }else if(inFragment.equals(mContext.getString(R.string.requests_fragment_tag))){
            exit = R.anim.slide_out_right;
            popEnter = R.anim.slide_in_left;
        }else if(inFragment.equals(mContext.getString(R.string.settings_fragment_tag))){
            exit = R.anim.slide_out_right;
            popEnter = R.anim.slide_in_left;
        }else{
            exit = R.anim.slide_out_right;
            popEnter = R.anim.slide_in_left;
        }

        if(outFragment.equals(mContext.getString(R.string.login_fragment_tag))){
            enter = R.anim.slide_in_left;
            popExit = R.anim.slide_out_right;
        }else if(outFragment.equals(mContext.getString(R.string.profile_fragment_tag))){
            enter = R.anim.slide_in_bottom;
            popExit = R.anim.slide_out_bottom;
        }else if(outFragment.equals(mContext.getString(R.string.request_fragment_tag))){
            enter = R.anim.slide_in_bottom;
            popExit = R.anim.slide_out_bottom;
        }else if(outFragment.equals(mContext.getString(R.string.request_overview_fragment_tag))){
            enter = R.anim.slide_in_bottom;
            popExit = R.anim.slide_out_bottom;
        }else if(outFragment.equals(mContext.getString(R.string.requests_fragment_tag))){
            enter = R.anim.slide_in_left;
            popExit = R.anim.slide_out_right;
        }else if(outFragment.equals(mContext.getString(R.string.settings_fragment_tag))){
            enter = R.anim.slide_in_left;
            popExit = R.anim.slide_out_right;
        }else{
            enter = R.anim.slide_in_left;
            popExit = R.anim.slide_out_right;
        }

        transaction.setCustomAnimations(enter, exit, popEnter, popExit);
    }
}
