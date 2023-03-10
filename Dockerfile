FROM openjdk:17
COPY target/Classroom-1.0.0-SNAPSHOT.jar /classroom.jar
ENTRYPOINT ["java","-jar","/classroom.jar"]