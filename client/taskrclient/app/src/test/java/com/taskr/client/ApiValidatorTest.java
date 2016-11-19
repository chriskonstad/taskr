package com.taskr.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.taskr.api.Api;
import com.taskr.api.LoginResult;
import com.taskr.api.Request;
import com.taskr.api.RequestResult;
import com.taskr.api.ServerApi;
import com.taskr.api.TestApi;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by guillaumelam34 on 11/17/2016.
 */

@RunWith(PowerMockRunner.class)
public class ApiValidatorTest {
    final String name = randomString(20);
    final String email = randomString(20) + "@test.com";
    final String fbid = randomString(20);
    final String hostname = "taskr130.herokuapp.com";

    @Mock
    Context mMockContext;

    @Test
    public void CheckLoginLogoutTest() {
        final TestApi api = new TestApi(mMockContext);

        // Ensure logged out
        try {
            api.checkReady();
            Assert.fail("Should have thrown an AuthError but didn't :(");
        } catch (Exception e) {}

        // Attempt to login
        api.login(api.profile.name, api.email, api.profile.fbid,
                new Api.ApiCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult returnValue) {
                Assert.assertEquals(api.profile.id, returnValue.id);
                Assert.assertEquals(api.profile.name, api.getName());
                Assert.assertEquals(api.profile.fbid, api.getFbid());
                Assert.assertEquals(api.email, api.getEmail());

                // Ensure logout works
                api.logout();
                try {
                    api.checkReady();
                    Assert.fail("Should have thrown an AuthError but didn't :(");
                } catch (Exception e) {}
            }

            @Override
            public void onFailure(String message) {
                Assert.fail(message);
            }
        });
    }

    @Test
    public void CheckReadyTest(){
        Api api = new TestApi(mMockContext);
        try{
            api.getId();
            Assert.fail("Expected thrown exception when no user logged in");
        }catch(Exception e){}

        try{
            api.getName();
            Assert.fail("Expected thrown exception when no user logged in");
        }catch(Exception e){}

        try{
            api.getEmail();
            Assert.fail("Expected thrown exception when no user logged in");
        }catch(Exception e){}

        try{
            api.getFbid();
            Assert.fail("Expected thrown exception when no user logged in");
        }catch(Exception e){}
    }

    @Test
    public void UserCreationTest(){
        PowerMockito.mockStatic(Log.class);
        final Api testInstance = new ServerApi(mMockContext, hostname);

        //Test creation of random user
        testInstance.login(name, email, fbid, new ServerApi.ApiCallback<com.taskr.api.LoginResult>() {
            @Override
            public void onSuccess(com.taskr.api.LoginResult returnValue) {
                assertNotNull(returnValue.id);
                final int uid = returnValue.id;

                //Test login of newly created random user
                testInstance.login(name, email, fbid, new ServerApi.ApiCallback<com.taskr.api.LoginResult>() {
                    @Override
                    public void onSuccess(com.taskr.api.LoginResult returnValue) {
                        assertEquals(uid, returnValue.id);
                    }

                    @Override
                    public void onFailure(String message) {
                        Assert.fail("Error creating user");
                    } });
            }

            @Override
            public void onFailure(String message) {
                Assert.fail("Error creating user");
            } });
    }

    @Test
    public void GetProfileTest(){
        final Api testInstance = new ServerApi(mMockContext, hostname);

        testInstance.login(name, email, fbid, new ServerApi.ApiCallback<com.taskr.api.LoginResult>() {
            @Override
            public void onSuccess(com.taskr.api.LoginResult returnValue) {
                testInstance.getUserProfile(returnValue.id, new ServerApi.ApiCallback<com.taskr.api.Profile>() {
                    @Override
                    public void onSuccess(com.taskr.api.Profile returnValue) {
                        assertNotNull(returnValue);
                    }

                    @Override
                    public void onFailure(String message) {
                        Assert.fail("Error getting user profile");
                    } });
            }
            @Override public void onFailure(String message) {
                Assert.fail("Error getting user profile");
            } });
    }

    @Test
    public void CreateRequestTest(){
        final Api testInstance = new ServerApi(mMockContext, hostname);

        testInstance.login(name, email, fbid, new ServerApi.ApiCallback<com.taskr.api.LoginResult>() {
            @Override
            public void onSuccess(com.taskr.api.LoginResult returnValue) {
                Request req = new Request();

                testInstance.createRequest(req, new ServerApi.ApiCallback<RequestResult>() {
                    @Override
                    public void onSuccess(com.taskr.api.RequestResult returnValue) {
                        assertNotNull(returnValue);
                        assertNotNull(returnValue.id);
                    }

                    @Override
                    public void onFailure(String message) {
                        Assert.fail("Error creating request");
                    } });
            }
            @Override public void onFailure(String message) {
                Assert.fail("Error creating request");
            } });
    }

    @Test
    public void EditRequestTest(){
        final Api testInstance = new ServerApi(mMockContext, hostname);

        testInstance.login(name, email, fbid, new ServerApi.ApiCallback<com.taskr.api.LoginResult>() {
            @Override
            public void onSuccess(com.taskr.api.LoginResult returnValue) {
                final Request req = new Request();

                testInstance.createRequest(req, new ServerApi.ApiCallback<RequestResult>() {
                    @Override
                    public void onSuccess(com.taskr.api.RequestResult returnValue) {
                        assertNotNull(returnValue);
                        assertNotNull(returnValue.id);

                        testInstance.editRequest(req, new ServerApi.ApiCallback<Void>() {
                            @Override public void onSuccess(Void returnValue) {}
                            @Override public void onFailure(String message){
                                Assert.fail("Error editing request");
                            }
                        });
                    }

                    @Override
                    public void onFailure(String message) {
                        Assert.fail("Error editing request");
                    } });
            }
            @Override public void onFailure(String message) {
                Assert.fail("Error editing request");
            } });
    }

    @Test
    public void NearbyRequestsTest(){
        final Api testInstance = new ServerApi(mMockContext, hostname);

        testInstance.login(name, email, fbid, new ServerApi.ApiCallback<com.taskr.api.LoginResult>() {
            @Override
            public void onSuccess(com.taskr.api.LoginResult returnValue) {
                Request r = new Request();
                r.lat = 1;
                r.longitude = 1;
                final Request req = r;

                testInstance.createRequest(req, new ServerApi.ApiCallback<RequestResult>() {
                    @Override
                    public void onSuccess(com.taskr.api.RequestResult returnValue) {
                        assertNotNull(returnValue);
                        assertNotNull(returnValue.id);

                        testInstance.getNearbyRequests(1, 1, 10, new ServerApi.ApiCallback<ArrayList<Request>>() {
                            @Override public void onSuccess(ArrayList<Request> returnValue) {
                                assertTrue(returnValue.size() >= 1);
                            }
                            @Override public void onFailure(String message){
                                Assert.fail("Error getting nearby requests");
                            }
                        });
                    }

                    @Override
                    public void onFailure(String message) {
                        Assert.fail("Error getting nearby requests");
                    } });
            }
            @Override public void onFailure(String message) {
                Assert.fail("Error getting nearby requests");
            } });
    }

    @Test
    public void UserRequestsTest(){
        final Api testInstance = new ServerApi(mMockContext, hostname);

        testInstance.login(name, email, fbid, new ServerApi.ApiCallback<com.taskr.api.LoginResult>() {
            @Override
            public void onSuccess(com.taskr.api.LoginResult returnValue) {
                final int uid = returnValue.id;

                Request r = new Request();
                r.user_id = uid;
                r.lat = 1;
                r.longitude = 1;
                final Request req = r;

                testInstance.createRequest(req, new ServerApi.ApiCallback<RequestResult>() {
                    @Override
                    public void onSuccess(com.taskr.api.RequestResult returnValue) {
                        assertNotNull(returnValue);
                        assertNotNull(returnValue.id);

                        testInstance.getUserRequests(uid, new ServerApi.ApiCallback<ArrayList<Request>>() {
                            @Override public void onSuccess(ArrayList<Request> returnValue) {
                                assertTrue(returnValue.size() >= 1);
                            }
                            @Override public void onFailure(String message){
                                Assert.fail("Error getting user requests");
                            }
                        });
                    }

                    @Override
                    public void onFailure(String message) {
                        Assert.fail("Error getting user requests");
                    } });
            }
            @Override public void onFailure(String message) {
                Assert.fail("Error getting user requests");
            } });
    }

    //Helper function generate random strings pulled from StackOverflow
    String randomString(final int length) {
        Random r = new Random(); // perhaps make it a class variable so you don't make a new one every time
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            char c = (char)(r.nextInt((int)(Character.MAX_VALUE)));
            sb.append(c);
        }
        return sb.toString();
    }
}
