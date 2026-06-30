
package com.project.account_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;




@SpringBootApplication
public class AccountServiceApplication {

	final private TcpSocket tcpSocket;

	public AccountServiceApplication(TcpSocket tcpSocket) {
		this.tcpSocket = tcpSocket;
	}

	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

	// @Bean
	// public CommandLineRunner commandLineRunner() {

	// 	return args -> {
	// 		new Thread(()->tcpSocket.listen()).start();
	// 	};
	// }

}
