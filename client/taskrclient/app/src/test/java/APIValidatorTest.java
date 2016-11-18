import android.content.Context;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.taskr.api.Api;
import com.taskr.api.LoginResult;
import com.taskr.api.Request;
import com.taskr.api.RequestResult;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by guillaumelam34 on 11/17/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class ApiValidatorTest {
    final String name = randomString(20);
    final String email = randomString(20) + "@test.com";
    final String fbid = randomString(20);

    @Mock
    Context mMockContext;

    @Test
    public void ApiSingletonTest(){
        Api testInstance1 = Api.getInstance();
        assertNotNull(testInstance1);

        Api testInstance2 = Api.getInstance();
        assertNotNull(testInstance2);

        assertEquals(testInstance1, testInstance2);
    }

    @Test
    public void CheckReadyTest(){
        try{
            Api.getInstance().getId();
            Assert.fail("Expected thrown exception when no user logged in");
        }catch(Exception e){}

        try{
            Api.getInstance().getName();
            Assert.fail("Expected thrown exception when no user logged in");
        }catch(Exception e){}

        try{
            Api.getInstance().getEmail();
            Assert.fail("Expected thrown exception when no user logged in");
        }catch(Exception e){}

        try{
            Api.getInstance().getFbid();
            Assert.fail("Expected thrown exception when no user logged in");
        }catch(Exception e){}
    }

    @Test
    public void UserCreationTest(){
        final Api testInstance = Api.getInstance();
        testInstance.init(mMockContext);

        //Test creation of random user
        testInstance.login(name, email, fbid, new Api.ApiCallback<com.taskr.api.LoginResult>() {
            @Override
            public void onSuccess(com.taskr.api.LoginResult returnValue) {
                assertNotNull(returnValue.id);
                final int uid = returnValue.id;

                //Test login of newly created random user
                testInstance.login(name, email, fbid, new Api.ApiCallback<com.taskr.api.LoginResult>() {
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
        final Api testInstance = Api.getInstance();
        testInstance.init(mMockContext);

        testInstance.login(name, email, fbid, new Api.ApiCallback<com.taskr.api.LoginResult>() {
            @Override
            public void onSuccess(com.taskr.api.LoginResult returnValue) {
                testInstance.getUserProfile(returnValue.id, new Api.ApiCallback<com.taskr.api.Profile>() {
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
        final Api testInstance = Api.getInstance();
        testInstance.init(mMockContext);

        testInstance.login(name, email, fbid, new Api.ApiCallback<com.taskr.api.LoginResult>() {
            @Override
            public void onSuccess(com.taskr.api.LoginResult returnValue) {
                Request req = new Request();

                testInstance.createRequest(req, new Api.ApiCallback<RequestResult>() {
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
        final Api testInstance = Api.getInstance();
        testInstance.init(mMockContext);

        testInstance.login(name, email, fbid, new Api.ApiCallback<com.taskr.api.LoginResult>() {
            @Override
            public void onSuccess(com.taskr.api.LoginResult returnValue) {
                final Request req = new Request();

                testInstance.createRequest(req, new Api.ApiCallback<RequestResult>() {
                    @Override
                    public void onSuccess(com.taskr.api.RequestResult returnValue) {
                        assertNotNull(returnValue);
                        assertNotNull(returnValue.id);

                        testInstance.editRequest(req, new Api.ApiCallback<Void>() {
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
        final Api testInstance = Api.getInstance();
        testInstance.init(mMockContext);

        testInstance.login(name, email, fbid, new Api.ApiCallback<com.taskr.api.LoginResult>() {
            @Override
            public void onSuccess(com.taskr.api.LoginResult returnValue) {
                Request r = new Request();
                r.lat = 1;
                r.longitude = 1;
                final Request req = r;

                testInstance.createRequest(req, new Api.ApiCallback<RequestResult>() {
                    @Override
                    public void onSuccess(com.taskr.api.RequestResult returnValue) {
                        assertNotNull(returnValue);
                        assertNotNull(returnValue.id);

                        testInstance.getNearbyRequests(1, 1, 10, new Api.ApiCallback<ArrayList<Request>>() {
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
        final Api testInstance = Api.getInstance();
        testInstance.init(mMockContext);

        testInstance.login(name, email, fbid, new Api.ApiCallback<com.taskr.api.LoginResult>() {
            @Override
            public void onSuccess(com.taskr.api.LoginResult returnValue) {
                final int uid = returnValue.id;

                Request r = new Request();
                r.user_id = uid;
                r.lat = 1;
                r.longitude = 1;
                final Request req = r;

                testInstance.createRequest(req, new Api.ApiCallback<RequestResult>() {
                    @Override
                    public void onSuccess(com.taskr.api.RequestResult returnValue) {
                        assertNotNull(returnValue);
                        assertNotNull(returnValue.id);

                        testInstance.getUserRequests(uid, new Api.ApiCallback<ArrayList<Request>>() {
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
