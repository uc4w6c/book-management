FROM openjdk:14-alpine
COPY build/libs/book-management-*-all.jar book-management.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "book-management.jar"]