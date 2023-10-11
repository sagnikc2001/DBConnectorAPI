package com.dhdigital.book.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import com.dhdigital.book.model.BookEnquiry;
import com.dhdigital.book.model.Books;

@Component
public class BooksRouteBuilder extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		
		restConfiguration().bindingMode(RestBindingMode.auto);

		rest("/books")
		
//		Get All Books using GET method at http://localhost:8080/api/books/get/all
		 .get("/get/all")
		  .to("direct:invoke-get-all-books")
		  
		  
//	    To add books in db using POST method at http://localhost:8080/api/books/add
		 .post("/add")
	      .type(Books.class)
		  .consumes("application/json")
		  .to("direct:invoke-add-book")
		  
		  
//		To get books by isbn using POST method at http://localhost:8080/api/books/get
		 .post("/get")
		  .type(BookEnquiry.class)
		  .consumes("application/json")
		  .to("direct:invoke-get-book-by-id");
		
		
//		To get all books from db
		from("direct:invoke-get-all-books")
//		 .log("something-${body}")
		 .to("bean:bookService?method=getBooks");
		
//		To add books in db
		from("direct:invoke-add-book")
		 .to("bean:bookService?method=addBook");
		
//		For fetching book by isbn using POST method
		from("direct:invoke-get-book-by-id")
//		 .unmarshal().json(JsonLibrary.Jackson, BookEnquiry.class)
//		 .log("Entry: ${body}")
//		 .log("Entry 2: ${body}")
		 .to("bean:bookService?method=getBookByIsbn")
//		 .log("Response : ${body}")
//		 .marshal().json(JsonLibrary.Jackson)
		 .choice()
		   .when(body().isNull())
		     .setBody(constant("Book Not found"))	
		   .otherwise()
	            .setHeader("Content-Type", constant("application/json")); 

	}

}
