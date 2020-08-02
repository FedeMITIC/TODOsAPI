package com.TODOsAPI.Controller;

import com.TODOsAPI.Model.TodoNote;
import com.TODOsAPI.Model.TodoNoteModelAssembler;
import com.TODOsAPI.Model.TodoRepo;
import com.TODOsAPI.TodoNotFoundException;

import lombok.SneakyThrows;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TodoController {
    private final TodoRepo repo;
    private final TodoNoteModelAssembler assembler;

    TodoController(TodoRepo repo, TodoNoteModelAssembler assembler) {
        this.repo = repo;
        this.assembler = assembler;
    }

    @GetMapping("/todos")
    public CollectionModel<EntityModel<TodoNote>> getAllItems() {
        List<EntityModel<TodoNote>> todos = this.repo.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(todos, linkTo(methodOn(TodoController.class).getAllItems()).withSelfRel());
    }

    @SneakyThrows
    @GetMapping("/todos/{id}")
    public EntityModel<TodoNote> getSingleItemById(@PathVariable Long id) {
        TodoNote note = this.repo.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
        return assembler.toModel(note);
    }

    @PostMapping("/todos")
    public ResponseEntity<?> newItem(@RequestBody TodoNote note) {
        note.setStatus(TodoNote.STATUS.CREATED);
        EntityModel<TodoNote> entityModel = assembler.toModel(this.repo.save(note));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/todos/{id}")
    public ResponseEntity<?> editById(@PathVariable Long id, @RequestBody TodoNote note) {
        TodoNote updatedItem = this.repo.findById(id)
                .map(todo -> {
                    todo.setTitle(note.getTitle());
                    todo.setBody(note.getBody());
                    todo.setStatus(todo.getStatus());  // Do not modify the note status, just edit its fields.
                    todo.setLastModification(new java.util.Date().toString());
                    return this.repo.save(todo);
                }).orElseGet(() -> {
                    note.setStatus(TodoNote.STATUS.CREATED);   // Set the status for the new note as CREATED
                    note.setId(id);
                    return this.repo.save(note);
                });
        EntityModel<TodoNote> entityModel = assembler.toModel(updatedItem);
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @SneakyThrows
    @PatchMapping("/todos/{id}/pin")
    public ResponseEntity<?> pinItem(@PathVariable Long id) {
        TodoNote note = this.repo.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
        if (note.getStatus() == TodoNote.STATUS.CREATED) {  // A note can be pinned if and only if is not archived/deleted
            note.setStatus(TodoNote.STATUS.PINNED);         // Do not edit other fields
            return ResponseEntity.ok(assembler.toModel(this.repo.save(note)));
        }
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("You cannot pin the todo note with id " + id + " (the note is in status: " + note.getStatus() + ").")
                );
    }

    @SneakyThrows
    @PatchMapping("/todos/{id}/reset")
    public ResponseEntity<?> resetItem(@PathVariable Long id) {
        TodoNote note = this.repo.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
        note.setStatus(TodoNote.STATUS.CREATED);  // Reset the item to its initial state
        note.setMarkedForDeletion(null);          // Remove the marked for deletion date
        return ResponseEntity.ok(assembler.toModel(this.repo.save(note)));
    }

    @SneakyThrows
    @PatchMapping("/todos/{id}/archive")
    public ResponseEntity<?> archiveItem(@PathVariable Long id) {
        TodoNote note = this.repo.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
        if (note.getStatus() == TodoNote.STATUS.PINNED || note.getStatus() == TodoNote.STATUS.CREATED) {
            note.setStatus(TodoNote.STATUS.ARCHIVED);
            return ResponseEntity.ok(assembler.toModel(this.repo.save(note)));
        }
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withTitle("Method not allowed")
                        .withDetail("You cannot archive the todo note with id " + id + " (the note is in status: " + note.getStatus() + ").")
                );
    }

    @SneakyThrows
    @PatchMapping("/todos/{id}/delete")
    public ResponseEntity<?> markItemForDeletion(@PathVariable Long id) {
        TodoNote note = this.repo.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
        if (note.getStatus() != TodoNote.STATUS.DELETED) {  // A note can be marked for deletion if and only if it is not marked already
            note.setStatus(TodoNote.STATUS.DELETED);
            note.setMarkedForDeletion(new java.util.Date().toString());
            return ResponseEntity.ok(assembler.toModel(this.repo.save(note)));
        }
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                    .withTitle("Method not allowed")
                    .withDetail("You cannot mark for deletion the todo note with id " + id + " (the note is in status: " + note.getStatus() + ").")
                );
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<?> deleteItemById(@PathVariable Long id) {
        this.repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/todos/purgeAll")
    public void deleteAllItems() {
        this.repo.deleteAll();
    }

}
