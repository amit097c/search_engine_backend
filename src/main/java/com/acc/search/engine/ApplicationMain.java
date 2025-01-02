package com.acc.search.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@CrossOrigin
//@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.acc.search.engine")
//@ComponentScan({ "com.acc.search.engine.controller" })
public class ApplicationMain {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationMain.class, args);
	}
	
	@Configuration
	public class WebConfig implements WebMvcConfigurer {

	    @Override
	    public void addCorsMappings(CorsRegistry registry) {
	        registry.addMapping("/**")
	                .allowedOrigins("http://localhost:4200") // Angular frontend
	                .allowedMethods("GET", "POST", "PUT", "DELETE");
	    }
	}
	
/*	@Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(10);
        pool.setMaxPoolSize(10);
        pool.setThreadNamePrefix("task-Thread-");
        pool.setWaitForTasksToCompleteOnShutdown(true);
        return pool;
    }*/

}
