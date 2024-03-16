package com.andreasx42.taskmanagerapi;

import com.andreasx42.taskmanagerapi.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AllArgsConstructor
@SpringBootApplication
public class SpringProjectApplication implements CommandLineRunner {

	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(SpringProjectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		/*try {
			User[] users = new User[] {
					new User("John Casey", "john.casey@example.com", "J0hnD!123"),
					new User("Jane Smith", "jane.smith@example.com", "JaneS@321"),
					new User("Emily Johnson", "emily.johnson@example.com", "Em!lyJ456"),
					new User("Michael Brown", "michael.brown@example.com", "M1chaelB$789")
			};

			Arrays.stream(users).forEach(userRepository::save);
		}

		catch (Exception e) {

		}*/
	}

}
