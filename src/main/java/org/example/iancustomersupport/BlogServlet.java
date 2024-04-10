package org.example.iancustomersupport;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "blog", value="/blog")
@MultipartConfig(fileSizeThreshold = 5_242_880, maxFileSize = 20_971_520L, maxRequestSize = 41_943_040L)
public class BlogServlet extends HttpServlet{
    private volatile int BLOG_ID = 1;
    private Map<Integer, Blog> blogDB = new LinkedHashMap<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        switch(action) {
            case "createBlog" -> showPostForm(request, response);
            case "view" -> viewPost(request, response);
            case "download" -> downloadImage(request, response);
            default -> listPosts(request, response); // this the list and any other
        }
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }
        switch(action) {
            case "create" -> createPost(request, response);
            default -> response.sendRedirect("blog"); // this the list and any other
        }
    }



    private void listPosts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        PrintWriter out = response.getWriter();

        //heading and link to create a blog
        out.println("<html><body><h2>Blog Posts</h2>");
        out.println("<a href=\"blog?action=createBlog\">Create Post</a><br><br>");

        // list out the blogs
        if (blogDB.size() == 0) {
            out.println("There are no blog posts yet...");
        }
        else {
            for (int id : blogDB.keySet()) {
                Blog blog = blogDB.get(id);
                out.println("Blog #" + id);
                out.println(": <a href=\"blog?action=view&blogId=" + id + "\">");
                out.println(blog.getTitle() + "</a><br>");
            }
        }
        out.println("</body></html>");

    }

    private void createPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // create the blog and set all values up
        Blog blog = new Blog();
        blog.setTitle(request.getParameter("title"));
        blog.setDate();
        blog.setBody(request.getParameter("body"));

        Part file = request.getPart("file1");
        if (file != null) {
            Image image = this.processImage(file);
            if (image != null) {
                blog.setImage(image);
            }
        }

        // add and synchronize
        int id;
        synchronized(this) {
            id = this.BLOG_ID++;
            blogDB.put(id, blog);
        }

        //System.out.println(blog);  // see what is in the blog object
        response.sendRedirect("blog?action=view&blogId=" + id);
    }

    private Image processImage(Part file) throws IOException{
        InputStream in = file.getInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // processing the binary data to bytes
        int read;
        final byte[] bytes = new byte[1024];
        while ((read = in.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }

        Image image = new Image();
        image.setName(file.getSubmittedFileName());
        image.setContents(out.toByteArray());

        return image;
    }

    private void downloadImage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String idString = request.getParameter("blogId");

        Blog blog = getBlog(idString, response);

        String name = request.getParameter("image");
        if (name == null) {
            response.sendRedirect("blog?action=view&blogId=" + idString);
        }

        Image image = blog.getImage();
        if (image == null) {
            response.sendRedirect("blog?action=view&blogId=" + idString);
            return;
        }

        response.setHeader("Content-Disposition", "image; filename=" + image.getName());
        response.setContentType("application/octet-stream");

        ServletOutputStream out = response.getOutputStream();
        out.write(image.getContents());
    }

    private void viewPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String idString = request.getParameter("blogId");

            Blog blog = getBlog(idString, response);

            PrintWriter out = response.getWriter();
            out.println("<html><body><h2>Blog Post</h2>");
            out.println("<h3>" + blog.getTitle()+ "</h3>");
            out.println("<p>Date: " + blog.getDate() + "</p>");
            out.println("<p>" + blog.getBody() + "</p>");
            if (blog.hasImage()) {
                out.println("<a href=\"blog?action=download&blogId=" +
                        idString + "&image="+ blog.getImage().getName() + "\">" +
                        blog.getImage().getName() + "</a><br><br>");
            }
            out.println("<a href=\"blog\">Return to blog list</a>");
            out.println("</body></html>");

    }

    private void showPostForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        PrintWriter out = response.getWriter();

        out.println("<html><body><h2>Create a Blog Post</h2>");
        out.println("<form method=\"POST\" action=\"blog\" enctype=\"multipart/form-data\">");
        out.println("<input type=\"hidden\" name=\"action\" value=\"create\">");
        out.println("Title:<br>");
        out.println("<input type=\"text\" name=\"title\"><br><br>");
        out.println("Body:<br>");
        out.println("<textarea name=\"body\" rows=\"25\" cols=\"100\"></textarea><br><br>");
        out.println("<b>Image</b><br>");
        out.println("<input type=\"file\" name=\"file1\"><br><br>");
        out.println("<input type=\"submit\" value=\"Submit\">");
        out.println("</form></body></html>");

    }

    private Blog getBlog(String idString, HttpServletResponse response) throws ServletException, IOException{
        // empty string id
        if (idString == null || idString.length() == 0) {
            response.sendRedirect("blog");
            return null;
        }

        // find in the 'database' otherwise return null
        try {
            int id = Integer.parseInt(idString);
            Blog blog = blogDB.get(id);
            if (blog == null) {
                response.sendRedirect("blog");
                return null;
            }
            return blog;
        }
        catch(Exception e) {
            response.sendRedirect("blog");
            return null;
        }
    }

}
