package com.example.springproject;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.springproject.entity.User;
import com.example.springproject.repository.UserRepository;

import java.util.Arrays;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@SpringBootApplication
public class SpringProjectApplication implements CommandLineRunner {

	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(SpringProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		User[] users = new User[] {
				new User("John Casey", "john.casey@example.com", "J0hnD!123"),
				new User("Jane Smith", "jane.smith@example.com", "JaneS@321"),
				new User("Emily Johnson", "emily.johnson@example.com", "Em!lyJ456"),
				new User("Michael Brown", "michael.brown@example.com", "M1chaelB$789")
		};

		Arrays.stream(users).forEach(userRepository::save);
	}

}
