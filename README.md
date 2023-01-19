# Classroom
### Web application for classroom management. App provides frontend endpoints

## HOW TO RUN APP:

### 1. Create Docker image

* Make sure you don't have any existing images with: 
```bash 
docker ps
```
* If so, then run:
* ```bash 
  docker stop $(docker ps -aq)
  ```
* ```bash 
  docker rm $(docker ps -aq)
  ```
* run command to create
  image: 
```bash
docker run -p 3307:3306 --name mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=classroom --rm -d mysql
```
### 2. Build project and perform tests

* Open terminal in project directory
* Type:
  `mvn clean install`

### 3. Start application

* `mvn spring-boot:run`
* go to endpoints and test app:
  * `http://localhost:8080/`
  * `http://localhost:8080/dashboard`

* Press CTRL+C to finish running app

