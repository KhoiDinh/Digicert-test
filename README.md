# Digicert-test

Instructions on how to get webserver up and running:

- Khois-MacBook-Pro:demo khoi$ export M2_HOME="/Users/khoi/Downloads/apache-maven-3.8.7"
- Khois-MacBook-Pro:demo khoi$ PATH="${M2_HOME}/bin:${PATH}"
- Khois-MacBook-Pro:demo khoi$ export PATH
- export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.0.5.jdk/Contents/Home
- Khois-MacBook-Pro:demo khoi$  ./mvnw spring-boot:run



- Used Spring boot rest api with Java to:
  - create api endpoints: Create, Read, Update, and Delete single book and list all books in library
  - contects to Mysql to store and retrieve data
  - unit tests for api usability
