package ru.netology.servlet;

import ru.netology.controller.PostController;

import ru.netology.exception.NotFoundException;
import ru.netology.handlers.PostHandler;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainServlet extends HttpServlet {
    private static final String GET = "GET";
    private static final String PATH_FOR_ALL_POSTS = "/api/posts";
    private static final String PATH_FOR_POST_WITH_ID = "/api/posts/\\d+";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";
    private Map<String, Map<String, PostHandler>> postHandlers;

    @Override
    public void init() {
        final PostRepository repository = new PostRepository();
        final PostService service = new PostService(repository);
        final PostController controller = new PostController(service);
        postHandlers = new HashMap<>();
        addHandler(GET, PATH_FOR_ALL_POSTS, new PostHandler() {
            @Override
            public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                controller.all(resp);
            }
        });
        addHandler(GET, PATH_FOR_POST_WITH_ID, new PostHandler() {
            @Override
            public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                final long id = Long.parseLong(req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1));
                controller.getById(id, resp);
            }
        });
        addHandler(POST, PATH_FOR_ALL_POSTS, new PostHandler() {
            @Override
            public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                controller.save(req.getReader(), resp);
            }
        });
        addHandler(DELETE, PATH_FOR_POST_WITH_ID, new PostHandler() {
            @Override
            public void handle(HttpServletRequest req, HttpServletResponse resp) {
                final long id = Long.parseLong(req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1));
                controller.removeById(id, resp);
            }
        });
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final String path = req.getRequestURI();
            final String method = req.getMethod();

            for (Map.Entry<String, Map<String, PostHandler>> methodVariants : postHandlers.entrySet()) {
                if (methodVariants.getKey().equals(method)) {
                    Map<String, PostHandler> handlersForMethod = methodVariants.getValue();
                    for (Map.Entry<String, PostHandler> pathVariants : handlersForMethod.entrySet()) {
                        if (path.matches(pathVariants.getKey())) {
                            PostHandler postHandler = pathVariants.getValue();
                            postHandler.handle(req, resp);
                            return;
                        }
                    }
                }
            }
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (NotFoundException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void addHandler(String method, String path, PostHandler postHandler) {
        if (postHandlers.containsKey(method)) {
            for (Map.Entry<String, Map<String, PostHandler>> methodVariants : postHandlers.entrySet()) {
                if (methodVariants.getKey().equals(method)) {
                    Map<String, PostHandler> handlersForMethod = methodVariants.getValue();
                    handlersForMethod.put(path, postHandler);
                }
            }
        } else {
            postHandlers.put(method, new HashMap<String, PostHandler>() {{
                put(path, postHandler);
            }});
        }
    }
}