FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/clj-atl-meetup-backend.jar /clj-atl-meetup-backend/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/clj-atl-meetup-backend/app.jar"]
