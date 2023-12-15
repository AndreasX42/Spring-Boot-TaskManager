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
				new User("Harry Potter", "a1@a.com", "123"),
				new User("Ron Weasley", "a2@a.com", "123"),
				new User("Hermione Granger", "a3@a.com", "123"),
				new User("Neville Longbottom", "a4@a.com", "123"),
		};

		Arrays.stream(users).forEach(userRepository::save);
	}

}
