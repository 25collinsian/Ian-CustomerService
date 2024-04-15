package org.example.iancustomersupport;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {
    private Ticket ticket;


    @Test
    void getTitle() {
        Ticket ticket = new Ticket("Test Title", "Test Body", null);
        assertEquals("Test Title", ticket.getTitle());
    }

    @Test
    void setTitle() {
        Ticket ticket = new Ticket("Test Title", "Test Body", null);
        ticket.setTitle("New Title");
        assertEquals("New Title", ticket.getTitle());
    }

    @Test
    void getDate() {
        Ticket ticket = new Ticket("Test Title", "Test Body", null);
        assertEquals(LocalDate.now(), ticket.getDate());
    }

    @Test
    void getBody() {
        Ticket ticket = new Ticket("Test Title", "Test Body", null);
        assertEquals("Test Body", ticket.getBody());
    }

    @Test
    void setBody() {
        Ticket ticket = new Ticket("Test Title", "Test Body", null);
        ticket.setBody("New Body");
        assertEquals("New Body", ticket.getBody());
    }
}
