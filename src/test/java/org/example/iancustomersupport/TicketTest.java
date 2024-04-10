package org.example.iancustomersupport;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TicketTest {

    @Test
    void testTicketCreation() {
        Ticket ticket = new Ticket("Ethan Lau", "Product won't start", "The product won't start.");

        assertEquals("Ethan Lau", ticket.getCustomerName());
        assertEquals("Product won't start", ticket.getSubject());
        assertEquals("The product won't start.", ticket.getBody());
        assertEquals(0, ticket.getNumberOfAttachments());
        assertNotNull(ticket.getAllAttachments());
    }

    @Test
    void testAddAttachment() {
        Ticket ticket = new Ticket();

        byte[] attachmentContent = "Attachment content".getBytes();
        ticket.addAttachment("file.txt", attachmentContent);

        assertEquals(1, ticket.getNumberOfAttachments());
        assertNotNull(ticket.getAttachment(0));
        assertArrayEquals(attachmentContent, ticket.getAttachment(0));
    }

    @Test
    void testGetNumberOfAttachments() {
        Ticket ticket = new Ticket();

        ticket.addAttachment("file1.txt", "Attachment 1 content".getBytes());
        ticket.addAttachment("file2.txt", "Attachment 2 content".getBytes());

        assertEquals(2, ticket.getNumberOfAttachments()); // Number of attachments is correct
    }

    @Test
    void testGetAttachment() {
        Ticket ticket = new Ticket();

        byte[] attachmentContent = "Attachment content".getBytes();
        ticket.addAttachment("file.txt", attachmentContent);

        byte[] retrievedAttachment = ticket.getAttachment(0);

        assertArrayEquals(attachmentContent, retrievedAttachment); // Attachment content is correct
    }

    @Test
    void testGetAllAttachments() {
        Ticket ticket = new Ticket();

        ticket.addAttachment("file1.txt", "Attachment 1 content".getBytes());
        ticket.addAttachment("file2.txt", "Attachment 2 content".getBytes());

        assertEquals(2, ticket.getAllAttachments().size()); // All attachments are retrieved
    }
}
