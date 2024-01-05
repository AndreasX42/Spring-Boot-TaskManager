package com.example.springproject.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springproject.dto.UserDTO;
import com.example.springproject.entity.User;
import com.example.springproject.exception.ErrorResponse;
import com.example.springproject.service.api.IUserService;
import com.example.springproject.service.utils.UserDTOMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@Tag(name = "User Controller", description = "Endpoints to create and manage users")
public class UserController {

    private final IUserService userService;
    private final UserDTOMapper userDTOMapper;

    @Operation(summary = "Returns a user based on an ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "User doesn't exist", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "200", description = "Successful retrieval of user", content = @Content(schema = @Schema(implementation = UserDTO.class))),
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {

        User user = userService.getById(id);
        return new ResponseEntity<>(userDTOMapper.apply(user), HttpStatus.OK);
    }

    @Operation(summary = "Retrieves paged list of users")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of all users", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))))
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UserDTO>> getAllTodos(Pageable pageable) {

        Page<User> todos = userService.getAll(pageable);
        Page<UserDTO> response = todos.map(userDTOMapper);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Creates a user from provided payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful creation of user", content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserDTO userDTO) {

        User user = userService.create(new User(userDTO.username(), userDTO.email(), userDTO.password()));
        return new ResponseEntity<>(userDTOMapper.apply(user), HttpStatus.CREATED);
    }

    @Operation(summary = "Updates a user from provided payload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful update of user", content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("#id == principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO,
            @PathVariable Long id) {

        User user = userService.update(id, userDTO);
        return new ResponseEntity<>(userDTOMapper.apply(user), HttpStatus.OK);
    }

    @Operation(summary = "Deletes user with given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful deletion of user", content = @Content(schema = @Schema(implementation = HttpStatus.class))),
            @ApiResponse(responseCode = "400", description = "Bad request: unsuccessful submission", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("#id == principal.id or hasAuthority('ADMIN')")
    public ResponseEntity<HttpStatus> delete(@PathVariable Long id) {

        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
