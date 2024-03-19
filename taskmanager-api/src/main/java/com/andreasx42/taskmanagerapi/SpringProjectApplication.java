package com.andreasx42.taskmanagerapi;

import com.andreasx42.taskmanagerapi.entity.Todo;
import com.andreasx42.taskmanagerapi.entity.User;
import com.andreasx42.taskmanagerapi.repository.TodoRepository;
import com.andreasx42.taskmanagerapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@SpringBootApplication
public class SpringProjectApplication implements CommandLineRunner {

	private UserRepository userRepository;

	private TodoRepository todoRepository;

	public static void main(String[] args) {
		SpringApplication.run(SpringProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		if(userRepository.count() > 0 && todoRepository.count() > 0)
			return;

		try {
			User[] users = {new User("John Casey17", "john.casey17@example.com", "J0hnD!123"),
					new User("Jane Smith123", "jane.smith123@example.com", "JaneS@321"),
					new User("Emily Johnson21", "emily.johnson21@example.com", "Em!lyJ456")};

			List<Todo> todos = Arrays.stream(users)
			                         .map(user -> {
				                         Todo todo = new Todo();
				                         todo.setName("Sample Todo of user " + user.getUsername());
				                         todo.setPriority(Todo.Priority.MID);
				                         todo.setStatus(Todo.Status.OPEN);
				                         todo.setUntilDate(LocalDate.now()
				                                                    .plusDays(7));
				                         todo.setUser(user);
				                         return todo;
			                         })
			                         .toList();

			Arrays.stream(users)
			      .forEach(userRepository::save);

			todos.forEach(todoRepository::save);

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
