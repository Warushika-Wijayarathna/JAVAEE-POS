import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/main")
public class Main extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ServletPath = req.getServletPath();
        String ContextPath = req.getContextPath();
        String PathInfo = req.getPathInfo();
        String QueryString = req.getQueryString();
        String RequestURI = req.getRequestURI();
        String RequestURL = req.getRequestURL().toString();
        String name = req.getParameter("name");
        String RequestMethod = req.getMethod();
        String RemoteUser = req.getRemoteUser();

        System.out.println("ServletPath: " + ServletPath);
        System.out.println("ContextPath: " + ContextPath);
        System.out.println("PathInfo: " + PathInfo);
        System.out.println("QueryString: " + QueryString);
        System.out.println("RequestURI: " + RequestURI);
        System.out.println("RequestURL: " + RequestURL);
        System.out.println("name: " + name);
        System.out.println("RequestMethod: " + RequestMethod);
        System.out.println("RemoteUser: " + RemoteUser);

    }
}