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

    /**
     * Sets transitions based on the fragment being transitioned out of and the fragment being trainsitioned into
     * @param transaction the transaction for which we want to set custom animations
     * @param transitionParams object containing the fragments relevant to the transaction
     */
    public void setCustomAnimations(FragmentTransaction transaction, TransitionParams transitionParams){
        int enter = 0;

        String inFragment = transitionParams.inFragment;

        if(inFragment.equals(mContext.getString(R.string.login_fragment_tag))){
            enter = android.R.anim.fade_in;
        }else if(inFragment.equals(mContext.getString(R.string.profile_fragment_tag))){
            enter = android.R.anim.fade_in;
        }else if(inFragment.equals(mContext.getString(R.string.request_fragment_tag))){
            enter = android.R.anim.fade_in;
        }else if(inFragment.equals(mContext.getString(R.string.request_overview_fragment_tag))){
            enter = android.R.anim.fade_in;
        }else if(inFragment.equals(mContext.getString(R.string.requests_fragment_tag))){
            enter = android.R.anim.fade_in;
        }else if(inFragment.equals(mContext.getString(R.string.settings_fragment_tag))){
            enter = android.R.anim.fade_in;
        }else{
            enter = android.R.anim.fade_in;
        }

        transaction.setCustomAnimations(enter, 0, enter, 0);
    }
}
