package org.example.iancustomersupport;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "ticket", value="/ticket")
@MultipartConfig(fileSizeThreshold = 5_242_880, maxFileSize = 20_971_520L, maxRequestSize = 41_943_040L)
public class TicketServlet extends HttpServlet{
    private volatile int BLOG_ID = 1;
    private Map<Integer, Ticket> blogDB = new LinkedHashMap<>();

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
                Ticket ticket = blogDB.get(id);
                out.println("Blog #" + id);
                out.println(": <a href=\"blog?action=view&blogId=" + id + "\">");
                out.println(ticket.getTitle() + "</a><br>");
            }
        }
        out.println("</body></html>");

    }

    private void createPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // create the blog and set all values up
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getParameter("title"));
        ticket.setDate();
        ticket.setBody(request.getParameter("body"));

        Part file = request.getPart("file1");
        if (file != null) {
            Attachment attachment = this.processImage(file);
            if (attachment != null) {
                ticket.setAttachment(attachment);
            }
        }

        // add and synchronize
        int id;
        synchronized(this) {
            id = this.BLOG_ID++;
            blogDB.put(id, ticket);
        }

        //System.out.println(blog);  // see what is in the blog object
        response.sendRedirect("blog?action=view&blogId=" + id);
    }

    private Attachment processImage(Part file) throws IOException{
        InputStream in = file.getInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // processing the binary data to bytes
        int read;
        final byte[] bytes = new byte[1024];
        while ((read = in.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }

        Attachment attachment = new Attachment();
        attachment.setName(file.getSubmittedFileName());
        attachment.setContents(out.toByteArray());

        return attachment;
    }

    private void downloadImage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String idString = request.getParameter("blogId");

        Ticket ticket = getBlog(idString, response);

        String name = request.getParameter("image");
        if (name == null) {
            response.sendRedirect("blog?action=view&blogId=" + idString);
        }

        Attachment attachment = ticket.getAttachment();
        if (attachment == null) {
            response.sendRedirect("blog?action=view&blogId=" + idString);
            return;
        }

        response.setHeader("Content-Disposition", "image; filename=" + attachment.getName());
        response.setContentType("application/octet-stream");

        ServletOutputStream out = response.getOutputStream();
        out.write(attachment.getContents());
    }

    private void viewPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String idString = request.getParameter("blogId");

            Ticket ticket = getBlog(idString, response);

            PrintWriter out = response.getWriter();
            out.println("<html><body><h2>Blog Post</h2>");
            out.println("<h3>" + ticket.getTitle()+ "</h3>");
            out.println("<p>Date: " + ticket.getDate() + "</p>");
            out.println("<p>" + ticket.getBody() + "</p>");
            if (ticket.hasAttachment()) {
                out.println("<a href=\"blog?action=download&blogId=" +
                        idString + "&image="+ ticket.getAttachment().getName() + "\">" +
                        ticket.getAttachment().getName() + "</a><br><br>");
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

    private Ticket getBlog(String idString, HttpServletResponse response) throws ServletException, IOException{
        // empty string id
        if (idString == null || idString.length() == 0) {
            response.sendRedirect("blog");
            return null;
        }

        // find in the 'database' otherwise return null
        try {
            int id = Integer.parseInt(idString);
            Ticket ticket = blogDB.get(id);
            if (ticket == null) {
                response.sendRedirect("blog");
                return null;
            }
            return ticket;
        }
        catch(Exception e) {
            response.sendRedirect("blog");
            return null;
        }
    }

}
