package com.kumarmanoj.bookhive;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;

import com.kumarmanoj.bookhive.author.Author;
import com.kumarmanoj.bookhive.author.AuthorRepository;
import com.kumarmanoj.bookhive.book.Book;
import com.kumarmanoj.bookhive.book.BookRepository;
import com.kumarmanoj.bookhive.connection.DataStaxAstraProperties;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BookhiveApplication {

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private BookRepository bookRepository;

	@Value("${datadump.location.author}")
	private String authorDumpLocation;

	@Value("${datadump.location.works}")
	private String worksDumpLocation;

	public static void main(String[] args) {
		SpringApplication.run(BookhiveApplication.class, args);
	}

	private void initAuthors() {
		Path path = Paths.get(authorDumpLocation);
		// read line be line
		try (Stream<String> lines = Files.lines(path)) {
			lines.forEach(line -> {
				// Read and parse the line
				int firstCurlyBracePos = line.indexOf("{");
				String jsonString = line.substring(firstCurlyBracePos);
				try {
					JSONObject jsonObject = new JSONObject(jsonString);
					// System.out.println(jsonObject.get("name"));

					// Construct the Author Object
					Author author = new Author();
					author.setName(jsonObject.optString("name"));
					author.setPersonName(jsonObject.optString("name"));
					author.setId(jsonObject.optString("key").replace("/authors/", ""));

					// Persists using author repository
					System.out.println("Saving author " + author.getName());
					authorRepository.save(author);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initWorks() {
		Path path = Paths.get(worksDumpLocation);
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
		try (Stream<String> lines = Files.lines(path)) {
			lines.forEach(line -> {
				int firstCurlyBracePos = line.indexOf("{");
				String jsonString = line.substring(firstCurlyBracePos);
				// System.out.println(jsonString);
				try {
					JSONObject jsonObject = new JSONObject(jsonString);
					// System.out.println(jsonObject.get("title"));
					// construct the (works) Books object
					Book book = new Book();
					book.setId(jsonObject.getString("key").replace("/works/", ""));
					book.setName(jsonObject.optString("title"));

					JSONObject descriptionObj = jsonObject.optJSONObject("description");
					if (descriptionObj != null) {
						book.setDescription(descriptionObj.optString("value"));
					}

					JSONObject publishedObj = jsonObject.optJSONObject("created");
					if (publishedObj != null) {
						String dateStr = publishedObj.optString("value");
						// System.out.println("date: "+dateStr);
						LocalDate publishedDate = LocalDate.parse(dateStr, dateFormat);
						book.setPublishedDate(publishedDate);
					}

					JSONArray coversJSONArr = jsonObject.optJSONArray("covers");
					if (coversJSONArr != null) {
						List<String> coverIds = new ArrayList<>();
						for (int i = 0; i < coversJSONArr.length(); i++) {
							coverIds.add(Integer.toString((int)coversJSONArr.get(i)));
						}
						book.setCoverIds(coverIds);
					}

					JSONArray authorsJSONArr = jsonObject.optJSONArray("authors");
					if (authorsJSONArr != null) {
						List<String> authorIds = new ArrayList<>();
						for (int i = 0; i < authorsJSONArr.length(); i++) {
							JSONObject authorsJSONObj = authorsJSONArr.getJSONObject(i);
							if (authorsJSONObj != null) {
								JSONObject auhtorObj = authorsJSONObj.getJSONObject("author");
								if (auhtorObj != null) {
									String authorId = auhtorObj.getString("key");
									authorId = authorId.replace("/authors/", "");
									authorIds.add(authorId);
								}
							}
						}
						// System.out.println("AuthorIds set for the book........");
						book.setAuthorId(authorIds);
						// get author name from author table in cassandra with author id
						List<String> authorNames = authorIds.stream().map(id -> authorRepository.findById(id))
								.map(optionalAuthor -> {
									if (!optionalAuthor.isPresent()) {
										return "Unkwon Author";
									} else {
										return optionalAuthor.get().getName();
									}
								}).collect(Collectors.toList());
						// System.out.println("AuthorNames set for the books......");
						book.setAuthorNames(authorNames);
						System.out.println("Saving Book " + book.getName() + "....");
						bookRepository.save(book);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@PostConstruct
	public void start() {
		System.out.println("----------- Application Started ----------");
		// verifing the location of datadump files
		// System.out.println(authorDumpLocation);
		// lets initialize the authors() and works
		// initAuthors();
		initWorks();
	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}
}
