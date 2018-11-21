FROM gradle:4.0-jdk8-alpine as builder
USER root
COPY . .
RUN gradle --no-daemon build

FROM openjdk:8-jre-alpine
RUN apk add --update tzdata && rm -rf /var/cache/apk/*
COPY --from=builder /home/gradle/build/libs/fint-pwfa-consumer-*.jar /data/app.jar
CMD ["java", "-jar", "/data/app.jar"]
