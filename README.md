# LOGISTICS PROJECT

## Project Description

This project is an information system for a logistics company, focusing on the transportation of goods. It leverages technologies like Java, and Thymeleaf providing a platform for managing logistics operations with two types of users: Logisitcs Employee and Driver. 

The credentials for accessing the view of the Logistics Employee is:
  - **User**: jhondoe@gmail.com
  - **Password**: House1234

Any of the Drivers created, can access automatically to their page with:
- **User**: Personal Number
- **Password**: password

NOTE: The application and database have been uploaded on DockerHub, to facilitate the replication of the project on other computers. 
## Technologies

The list of technologies used are:

* Spring Core
* Spring Data
* Spring Boot
* Spring Security
* MVC
* Apache Tomcat
* HTML, CSS - Thymeleaf
* Bootstrap
* Maven
* Git
* Lombok
* Junit and Mockito
* PostgreSQL
* JPA/Hibernate
* Docker

## Installation and Setup

To clone this repository:

    git clone https://github.com/noocaaa/LogisticsProject

To initialize the project:

    cd postgres
    docker-compose up -d

Then, the page is easily access with _`localhost:5000`_

The project has use environment variables. If execution of `maven clean install` is desired, it should be done with the `setenv.sh` provided.

    chmod +x setenv.sh
    ./setenv.sh

## License

This project is licensed under the MIT License - se e the LICENSE file for details.