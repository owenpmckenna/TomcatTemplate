FROM tomcat:10.1.7-jdk17-temurin-jammy AS base
WORKDIR /usr/local/tomcat/
RUN apt update
RUN apt upgrade -y
EXPOSE 8080
EXPOSE 8443

#  # https://dev.to/mozenn/build-a-jar-file-inside-a-docker-container-with-maven-1oji
# Build stage
#
FROM maven:3.9.0-eclipse-temurin-11 AS build
COPY . .
RUN rm src/main/webapp/META-INF/context.xml
RUN mvn clean package

FROM base AS final
COPY --from=build /target/ROOT.war ./webapps/
COPY ./tomcatConf/server.xml ./conf/
COPY ./tomcatConf/web.xml ./conf/
COPY ./tomcatConf/site.key ./conf/
COPY ./tomcatConf/site.pem ./conf/
COPY ./src/main/webapp/META-INF/context.xml ./conf/Catalina/localhost/ROOT.xml
COPY ./waitnrun.sh ./waitnrun.sh
RUN chmod +x ./waitnrun.sh
RUN wget https://dlm.mariadb.com/2720710/Connectors/java/connector-java-3.1.2/mariadb-java-client-3.1.2.jar -O ./lib/mariadb-java-client-3.1.2.jar
ENTRYPOINT ["./waitnrun.sh"]
