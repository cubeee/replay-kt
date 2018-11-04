FROM gradle:jdk8-alpine AS BUILD_IMAGE
ENV APP_HOME=/home/gradle/project/
RUN mkdir -p $APP_HOME/src
WORKDIR $APP_HOME
COPY build.gradle gradlew $APP_HOME
COPY gradle $APP_HOME/gradle
COPY src $APP_HOME/src
RUN gradle jar -PjarName=replay-kt.jar

FROM openjdk:8-jre
COPY --from=BUILD_IMAGE /home/gradle/project/build/libs/replay-kt.jar /replay-kt/
ENTRYPOINT ["java", "-cp", "/replay-kt/replay-kt.jar"]
