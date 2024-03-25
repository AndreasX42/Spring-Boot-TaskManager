package com.andreasx42.taskmanagerapi;

import com.andreasx42.taskmanagerapi.repository.TodoRepository;
import com.andreasx42.taskmanagerapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

	}

}
