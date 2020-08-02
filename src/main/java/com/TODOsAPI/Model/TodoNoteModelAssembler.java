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
    }
}
