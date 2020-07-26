FROM gcr.io/distroless/java:11

ADD /build/libs/messaging-0.0.1-SNAPSHOT.jar messaging.jar

ENTRYPOINT ["java", "-jar", "messaging.jar"]

EXPOSE 8080
