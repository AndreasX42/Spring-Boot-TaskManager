package com.andreasx42.taskmanagerapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Nonnull
	@NotBlank(message = "username cannot be blank")
	@Column(nullable = false, unique = true)
	private String username;
	@Nonnull
	@NotBlank(message = "email cannot be blank")
	@Email(message = "email must be a valid email address")
	@Column(nullable = false, unique = true)
	private String email;
	@Nonnull
	@NotBlank(message = "password cannot be blank")
	@Column(nullable = false)
	private String password;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, updatable = false)
	private Role role = Role.USER;
	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Todo> todos;

	public enum Role {
		USER, ADMIN
	}
}
