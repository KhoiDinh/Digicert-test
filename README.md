# Digicert-test

Instructions on how to get webserver up and running:
Note: MySQL serveer must be up before service can start

- Khois-MacBook-Pro:demo khoi$ export M2_HOME="/Users/khoi/Downloads/apache-maven-3.8.7"
- Khois-MacBook-Pro:demo khoi$ PATH="${M2_HOME}/bin:${PATH}"
- Khois-MacBook-Pro:demo khoi$ export PATH
- export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.0.5.jdk/Contents/Home
- Khois-MacBook-Pro:demo khoi$  ./mvnw spring-boot:run



- Used Spring boot rest api with Java to:
  - create api endpoints: Create, Read, Update, and Delete single book and list all books in library
  - contects to Mysql to store and retrieve data, using JpaRepository to execute db queries, and read in JSON formatted queries.
  - used Postman to execute REST API Calls
  - unit tests for api usability
  
  
  
 - Example Api Execution
  - CREATE: POST http://localhost:9090/api/book
    {
    "isbn":"978-3-36-159411-0",
    "title":"pokemon 3rd season",
    "author": "japan",
    "genre": "fantasy"
    }
    - return code generated id used to search, delete, and update (e.g: 3937382d332d31362d3134383431302d52)
   
  - Get all books in library(table): GET http://localhost:9090/api/book
  - Get single book based on database id: http://localhost:9090/api/book/<book's db id>
  - Delete a book from library: DELETE http://localhost:9090/api/book/<book's db id>
  - Update the fields of an existing book. requires the book's db id and update field values: PUT http://localhost:9090/api/book/<book's db id> and
    {
    "isbn":"978-3-36-159411-0",
    "title":"Romeo and Juliet",
    "author": "Kate Dinh",
    "genre": "fantasy"
    }
