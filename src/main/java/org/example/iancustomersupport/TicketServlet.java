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
        private volatile int ticketID = 1;
        private Map<Integer, Ticket> ticketDB = new LinkedHashMap<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        switch(action) {
            case "createticket" -> showTicketForm(request, response);
            case "view" -> viewTicket(request, response);
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
            case "create" -> createTicket(request, response);
            default -> response.sendRedirect("ticket"); // this the list and any other
        }
    }



    private void listTickets(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        request.setAttribute("ticketDatabase", ticketDB);
        request.getRequestDispatcher("WEB-INF/JSP/view/listTickets.jsp").forward(request, response);

    }

    private void createTicket(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            id = this.ticketID++;
            ticketDB.put(id, ticket);
        }

        //System.out.println(ticket);  // see what is in the ticket object
        response.sendRedirect("ticket?action=view&ticketID=" + id);
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
        String idString = request.getParameter("ticketID");

        Ticket ticket = getTicket(idString, response);

        String name = request.getParameter("attachment");
        if (name == null) {
            response.sendRedirect("ticket?action=view&ticketID=" + idString);
        }

        Attachment attachment = ticket.getAttachment();
        if (attachment == null) {
            response.sendRedirect("ticket?action=view&ticketID=" + idString);
            return;
        }

        response.setHeader("Content-Disposition", "Attachment; filename=" + attachment.getName());
        response.setContentType("application/octet-stream");

        ServletOutputStream out = response.getOutputStream();
        out.write(attachment.getContents());
    }

    private void viewTicket(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idString = request.getParameter("ticketID");
        Ticket ticket = getTicket(idString, response);

        request.setAttribute("Ticket", ticket);
        request.setAttribute("ticketID", idString);

        request.getRequestDispatcher("WEB-INF/JSP/view/viewTicket.jsp").forward(request, response);
    }

        private void showTicketForm (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
            request.getRequestDispatcher("WEB-INF/JSP/view/TicketForm.jsp").forward(request, response);
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
