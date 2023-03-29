# Classroom

### Web application for classroom management. App provides frontend endpoints

## HOW TO RUN APP LOCALLY:

### 1. Clone the project

```bash
  git clone https://github.com/lyingparachute/Classroom.git
```

Go to project directory

```bash
  cd classroom
```

### 2. Build project and perform tests

* Open terminal in project directory
* Type:
  `mvn clean install`

### 3. Start application server with docker-compose

```bash
docker-compose up --build
```

***note** - it might take a while to pull and build docker images*

* go to endpoints and test app:
  * <a href="http://localhost:8080/" target="_blank">Welcome page</a>

    `http://localhost:8080`
  * <a href="http://localhost:8080/dashboard" target="_blank">Dashboard</a>

    `http://localhost:8080/dashboard`
* Finish running app

  ```
  press CTRL+C
  ``` 

### 4. Run app - second time

* START APP
  ```bash
  docker-compose start
  ```
* STOP APP
   ```bash
  docker-compose stop
  ```
* REMOVE NETWORK
  ```bash
  docker-compose down
  ```

## Alternative way of running app locally

Create docker image and run project with IntelliJ:

```bash
docker run -p 3307:3306 --name mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=classroom --rm -d mysql
```

## HOW TO USE APP:

#### Create new account

* Register using email and password
* Sign In to the dashboard and use app

  #### OR

#### Use already existing accounts

1) Student account:
  * Login: student
  * Password: student
2) Teacher account:
  * Login: teacher
  * Password: teacher
3) Dean account:
  * Login: dean
  * Password: dean
4) Admin account:
  * Login: admin
  * Password: admin

