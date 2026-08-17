package com.fatma.taskmanger.task;

import com.fatma.taskmanger.user.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @ManyToOne User: every task belongs to exactly one user (the owner).
 * FetchType.LAZY on the owning side of a ManyToOne is the course's
 * default recommendation - avoid loading the whole User graph every time
 * a Task is fetched, unless you specifically need it.
 *
 * @ManyToMany Tag: the owning side (this entity holds @JoinTable), Tag is
 * the inverse side (mappedBy = "tags"). A join table task_tags is created
 * automatically with task_id / tag_id columns.
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "task_tags",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public Task() {
    }

    public Task(String title, String description, User user) {
        this.title = title;
        this.description = description;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Tag> getTags() {
        return tags;
    }
}
