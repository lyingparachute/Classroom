FROM adoptopenjdk/openjdk17:jdk-17.0.1
COPY target/Classroom-1.0.0-SNAPSHOT.jar /classroom.jar
ENTRYPOINT ["java","-jar","/classroom.jar"]