#------------------------------
#Dockerfile for user-service
#------------------------------

#-----------
#BUILD stage
#-----------

#base image
FROM gradle:8-jdk17-alpine AS builder

#Copy the application files from the host machine to the image filesystem
COPY --chown=gradle . /home/gradle/src

# set the directory within the image, for executing future commands
WORKDIR /home/gradle/src

# run gradle build within the container - NOT the host machine
RUN gradle build -x test

#------------------------
# Package Stage
#------------------------

from openjdk:17-alpine

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/user-service.jar

ENV MONGO_DB_USERNAME=rootadmin \
    MONGO_DB_PASSWORD=password

# Setting the Spring Boot profile to "docker"
ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]