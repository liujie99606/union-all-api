FROM openjdk:17

RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \&& echo 'Asia/Shanghai' >/etc/timezone

WORKDIR /app

COPY union-server.jar /app/app.jar

EXPOSE 48080
ENTRYPOINT ["java", "-jar", "-Xmx512m", \
            "-XX:-OmitStackTraceInFastThrow", \
            "app.jar", "--logging.file.path=/app","--spring.profiles.active=dev"]