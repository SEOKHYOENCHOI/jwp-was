package webserver.requesthandler;

import controller.*;
import controller.exception.MethodNotAllowedException;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.response.ResponseStatus;
import view.ModelAndView;
import view.View;
import view.ViewResolver;

import java.util.HashMap;
import java.util.Map;

public class DynamicHttpRequestHandler extends AbstractHttpRequestHandler {
    private static final Map<String, Controller> HANDLER_MAP = new HashMap<>();

    static {
        HANDLER_MAP.put("/user/create", UserController.getInstance());
        HANDLER_MAP.put("/user/form", UserFormController.getInstance());
        HANDLER_MAP.put("/user/login", LoginController.getInstance());
        HANDLER_MAP.put("/user/login_failed", LoginFailedController.getInstance());
        HANDLER_MAP.put("/user/list", UserListController.getInstance());
        HANDLER_MAP.put("/", IndexController.getInstance());
    }

    private DynamicHttpRequestHandler() {
    }

    public static DynamicHttpRequestHandler getInstance() {
        return DynamicHttpRequestHandlerLazyHolder.INSTANCE;
    }

    @Override
    public boolean canHandle(String path) {
        return HANDLER_MAP.containsKey(path);
    }

    @Override
    public void handleInternal(HttpRequest httpRequest, HttpResponse httpResponse) {
        Controller controller = HANDLER_MAP.get(httpRequest.getPath());
        try {
            ModelAndView modelAndView = controller.service(httpRequest, httpResponse);
            View view = ViewResolver.getInstance().resolve(modelAndView.getViewName());
            view.render(modelAndView.getModelMap(), httpResponse);
        } catch (MethodNotAllowedException e) {
            e.printStackTrace();
            httpResponse.setResponseStatus(ResponseStatus.METHOD_NOT_ALLOWED);
        }
    }

    private static class DynamicHttpRequestHandlerLazyHolder {
        private static final DynamicHttpRequestHandler INSTANCE = new DynamicHttpRequestHandler();
    }
}
