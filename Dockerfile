FROM openjdk:8-jdk-alpine
LABEL maintainer="chandrakanthreddy.mamillapalli@aexp.com"
COPY build/unpacked/dist /usr/src/
EXPOSE 8080
CMD [ "/usr/src/<project>/bin/<compiled-src>" ]
