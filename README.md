# jukeboxes

###### How to run
Build the project with maven

`mvn clean install package`

Prepare your docker environment. Please note that I only tested this on docker tool box. So I suppose your docker is the same as mine.

a. Go to docker-toolbox console

b. Pull cassandra image to your local

`docker pull cassandra`

c. Run cassandra and expose its port to host as tcp:9045

`docker run --name cas1 -p 9045:9042 cassandra:latest`

Now cassandra is running on your local at your VM IP (my case is 192.168.99.100:9045)

##### Prepare your application
a. Go to `<project-dir>/scripts/` and the following command to create application keyspace

`cqlsh -f jukebox.cql`

b. Run `.\python3 populate_cassandra_from_rest_api.py` to populate data from Rest-apis to your docker cassandra.

c. Build docker image

**Note**: these commands must be executed in project base directory and using a docker-friendly console

`mvn dockerfile:build -f pom.xml`


d. Run jukebox docker image

`docker run --name=jukeboxes jukeboxes/jukeboxes:latest -p 8080:8080`

Jukeboxes app is now running on 192.168.99.100:8080, you can start to exploit it through `http://192.168.99.100:8080/swagger-ui.html` to see its APIs

####Technologies.

- Spring Boot: Spring data for cassandra driver, Spring Rest
- Lombok: To make pojo less painful
- Swagger: To make entry page of API
- Cassandra: To serve as application's data store. The limitation of this choice is pagingation only support linear jump from page to page using offsets which are the previous paging states. The benefit of this choice is performance.
- Docker: to dockerize the solution and cassandra data store

