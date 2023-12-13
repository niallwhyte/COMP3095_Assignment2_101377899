#------------------------------
#Dockerfile for comment-service
#------------------------------

#-----------
#BUILD stage
#-----------

FROM gradle:8-jdk17-alpine AS builder

COPY --chown=gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle build -x test

#------------------------
#Package Stage
#------------------------

FROM openjdk:17-alpine

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/friendship-service.jar

ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "/app/friendship-service.jar"]