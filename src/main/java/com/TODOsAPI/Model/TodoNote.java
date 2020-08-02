package com.TODOsAPI.Model;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class TodoNote {
    private @Id @GeneratedValue Long id;
    private String title;
    private String body;
    private Enum<STATUS> status;
    private String lastModification;
    private String markedForDeletion;
    private static final int deleteAfterDay = 30;

    public enum STATUS {
        CREATED,    // A TODOnote has been created
        PINNED,     // A TODOnote is pinned at the top of the UI
        ARCHIVED,   // A TODOnote is archived (hidden from the main view)
        DELETED     // A TODOnote is deleted (hidden from the main view and from the archive)
    }

    public TodoNote() {}

    public TodoNote(String title, String body, Enum<STATUS> status) {
        this.title = title;
        this.body = body;
        this.status = status;
        this.lastModification = new java.util.Date().toString();
    }
}
