package com.ptc.ptcworker

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
class PtcWorkerApplication {
	
	//@Bean
	//public BCryptPasswordEncoder bCryptPassEncoder() { return new BCryptPasswordEncoder() }

	static void main(String[] args) {
		SpringApplication.run(PtcWorkerApplication, args)
	}

}
