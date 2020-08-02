package com.TODOsAPI.Model;

import com.TODOsAPI.Controller.TodoController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TodoNoteModelAssembler implements RepresentationModelAssembler<TodoNote, EntityModel<TodoNote>> {
    @Override
    public EntityModel<TodoNote> toModel(TodoNote todo) {
        EntityModel<TodoNote> entityModel = EntityModel.of(todo,
            linkTo(methodOn(TodoController.class).getSingleItemById(todo.getId())).withSelfRel(),
            linkTo(methodOn(TodoController.class).getAllItems()).withRel("todos"));

        if (todo.getStatus() == TodoNote.STATUS.CREATED) {
            entityModel.add(linkTo(methodOn(TodoController.class).pinItem(todo.getId())).withRel("pin"));
            entityModel.add(linkTo(methodOn(TodoController.class).archiveItem(todo.getId())).withRel("archive"));
            entityModel.add(linkTo(methodOn(TodoController.class).markItemForDeletion(todo.getId())).withRel("delete"));
        }

        if (todo.getStatus() == TodoNote.STATUS.PINNED) {
            entityModel.add(linkTo(methodOn(TodoController.class).resetItem(todo.getId())).withRel("reset"));
            entityModel.add(linkTo(methodOn(TodoController.class).archiveItem(todo.getId())).withRel("archive"));
            entityModel.add(linkTo(methodOn(TodoController.class).markItemForDeletion(todo.getId())).withRel("delete"));
        }

        if (todo.getStatus() == TodoNote.STATUS.ARCHIVED) {
            entityModel.add(linkTo(methodOn(TodoController.class).resetItem(todo.getId())).withRel("reset"));
            entityModel.add(linkTo(methodOn(TodoController.class).markItemForDeletion(todo.getId())).withRel("delete"));
        }

        if (todo.getStatus() == TodoNote.STATUS.DELETED) {
            entityModel.add(linkTo(methodOn(TodoController.class).resetItem(todo.getId())).withRel("reset"));
        }

        return entityModel;
    }
}
