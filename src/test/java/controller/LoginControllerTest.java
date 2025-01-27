package controller;

import http.common.HttpHeader;
import http.request.HttpRequest;
import http.request.RequestBody;
import http.request.RequestLine;
import http.response.HttpResponse;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.ModelAndView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static http.common.ContentType.FORM_URLENCODED;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginControllerTest extends AbstractControllerTest {
    private LoginController loginController = LoginController.getInstance();
    private User signUpUser = new User("signUpId", "password", "name", "email");

    @BeforeEach
    void setUp() {
        signUp(signUpUser);
    }

    @Test
    void DoPost_로그인_성공() {
        ModelAndView modelAndView = login(signUpUser.getUserId(), signUpUser.getPassword());

        assertEquals(modelAndView.getViewName(), String.format("redirect: %s", "/"));
        assertEquals(modelAndView.getModelMap(), Collections.emptyMap());
    }

    @Test
    void DoPost_로그인_실패() {
        ModelAndView modelAndView = login("none", "none");

        assertEquals(modelAndView.getViewName(), String.format("redirect: %s", "/user/login_failed"));
        assertEquals(modelAndView.getModelMap(), Collections.emptyMap());
    }

    private ModelAndView login(String id, String password) {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("userId", id);
        loginData.put("password", password);

        HttpRequest httpRequest = new HttpRequest(
                new RequestLine("POST /user/login HTTP/1.1"),
                new HttpHeader(),
                new RequestBody(getQueryString(loginData), FORM_URLENCODED));
        HttpResponse httpResponse = new HttpResponse(httpRequest);

        return loginController.doPost(httpRequest, httpResponse);
    }
}