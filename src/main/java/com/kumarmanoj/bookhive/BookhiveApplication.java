package com.kumarmanoj.bookhive;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.kumarmanoj.bookhive.author.Author;
import com.kumarmanoj.bookhive.author.AuthorRepository;
import com.kumarmanoj.bookhive.connection.DataStaxAstraProperties;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BookhiveApplication {

	@Autowired AuthorRepository authorRepository;

	public static void main(String[] args) {
		SpringApplication.run(BookhiveApplication.class, args);
	}

	@PostConstruct
	public void start() {
		System.out.println("----------- Application Started ----------");
		//create a new author and persist it
		Author author = new Author();
		author.setId("id");
		author.setName("Name");
		author.setPersonName("personal name");
		authorRepository.save(author);
	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}
}
