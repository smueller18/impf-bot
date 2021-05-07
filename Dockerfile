FROM openjdk:14-slim

WORKDIR /app
COPY ./build/libs/impf-bot-1.0-SNAPSHOT-all.jar /app/

CMD ["java", "-jar", "impf-bot-1.0-SNAPSHOT-all.jar"]
