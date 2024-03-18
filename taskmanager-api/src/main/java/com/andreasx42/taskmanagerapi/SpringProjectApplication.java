package com.andreasx42.taskmanagerapi;

import com.andreasx42.taskmanagerapi.entity.User;
import com.andreasx42.taskmanagerapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@AllArgsConstructor
@SpringBootApplication
public class SpringProjectApplication implements CommandLineRunner {

	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(SpringProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		try {
			User[] users = new User[]{new User("John Casey17", "john.casey17@example.com", "J0hnD!123"),
					new User("Jane Smith123", "jane.smith123@example.com", "JaneS@321"),
					new User("Emily Johnson21", "emily.johnson21@example.com", "Em!lyJ456"),
					new User("Michael Brown65", "michael.brown65@example.com", "M1chaelB$789")};

			Arrays.stream(users)
			      .forEach(userRepository::save);
		} catch(Exception e) {

		}
	}

}
