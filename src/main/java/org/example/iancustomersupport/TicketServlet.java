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
    private volatile int ticket_ID = 1;
    private Map<Integer, Ticket> ticketDB = new LinkedHashMap<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        switch(action) {
            case "createticket" -> showPostForm(request, response);
            case "view" -> viewPost(request, response);
            case "download" -> downloadAttachment(request, response);
            default -> listTickets(request, response); // this the list and any other
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
            default -> response.sendRedirect("ticket"); // this the list and any other
        }
    }



    private void listTickets(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        PrintWriter out = response.getWriter();

        //heading and link to create a ticket
        out.println("<html><body><h2>ticket Posts</h2>");
        out.println("<a href=\"ticket?action=createticket\">Create Post</a><br><br>");

        // list out the tickets
        if (ticketDB.size() == 0) {
            out.println("There are no ticket posts yet...");
        }
        else {
            for (int id : ticketDB.keySet()) {
                Ticket ticket = ticketDB.get(id);
                out.println("ticket #" + id);
                out.println(": <a href=\"ticket?action=view&ticketId=" + id + "\">");
                out.println(ticket.getTitle() + "</a><br>");
            }
        }
        out.println("</body></html>");

    }

    private void createPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // create the ticket and set all values up
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getParameter("title"));
        ticket.setDate();
        ticket.setBody(request.getParameter("body"));

        Part file = request.getPart("file1");
        if (file != null) {
            Attachment attachment = this.processAttachment(file);
            if (attachment != null) {
                ticket.setAttachment(attachment);
            }
        }

        // add and synchronize
        int id;
        synchronized(this) {
            id = this.ticket_ID++;
            ticketDB.put(id, ticket);
        }

        //System.out.println(ticket);  // see what is in the ticket object
        response.sendRedirect("ticket?action=view&ticketId=" + id);
    }

    private Attachment processAttachment(Part file) throws IOException{
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

    private void downloadAttachment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String idString = request.getParameter("ticketId");

        Ticket ticket = getTicket(idString, response);

        String name = request.getParameter("attachment");
        if (name == null) {
            response.sendRedirect("ticket?action=view&ticketId=" + idString);
        }

        Attachment attachment = ticket.getAttachment();
        if (attachment == null) {
            response.sendRedirect("ticket?action=view&ticketId=" + idString);
            return;
        }

        response.setHeader("Content-Disposition", "Attachment; filename=" + attachment.getName());
        response.setContentType("application/octet-stream");

        ServletOutputStream out = response.getOutputStream();
        out.write(attachment.getContents());
    }

    private void viewPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String idString = request.getParameter("ticketId");

            Ticket ticket = getTicket(idString, response);

            PrintWriter out = response.getWriter();
            out.println("<html><body><h2>ticket Post</h2>");
            out.println("<h3>" + ticket.getTitle()+ "</h3>");
            out.println("<p>Date: " + ticket.getDate() + "</p>");
            out.println("<p>" + ticket.getBody() + "</p>");
            if (ticket.hasAttachment()) {
                out.println("<a href=\"ticket?action=download&ticketId=" +
                        idString + "&attachment="+ ticket.getAttachment().getName() + "\">" +
                        ticket.getAttachment().getName() + "</a><br><br>");
            }
            out.println("<a href=\"ticket\">Return to ticket list</a>");
            out.println("</body></html>");

    }

    private void showPostForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        PrintWriter out = response.getWriter();

        out.println("<html><body><h2>Create a ticket Post</h2>");
        out.println("<form method=\"POST\" action=\"ticket\" enctype=\"multipart/form-data\">");
        out.println("<input type=\"hidden\" name=\"action\" value=\"create\">");
        out.println("Title:<br>");
        out.println("<input type=\"text\" name=\"title\"><br><br>");
        out.println("Body:<br>");
        out.println("<textarea name=\"body\" rows=\"25\" cols=\"100\"></textarea><br><br>");
        out.println("<b>attachment</b><br>");
        out.println("<input type=\"file\" name=\"file1\"><br><br>");
        out.println("<input type=\"submit\" value=\"Submit\">");
        out.println("</form></body></html>");

    }

    private Ticket getTicket(String idString, HttpServletResponse response) throws ServletException, IOException{
        // empty string id
        if (idString == null || idString.length() == 0) {
            response.sendRedirect("ticket");
            return null;
        }

        // find in the 'database' otherwise return null
        try {
            int id = Integer.parseInt(idString);
            Ticket ticket = ticketDB.get(id);
            if (ticket == null) {
                response.sendRedirect("ticket");
                return null;
            }
            return ticket;
        }
        catch(Exception e) {
            response.sendRedirect("ticket");
            return null;
        }
    }

}
