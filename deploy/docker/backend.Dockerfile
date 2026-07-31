FROM eclipse-temurin:8-jre-jammy

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl ca-certificates \
    && rm -rf /var/lib/apt/lists/*

COPY server/aqy-admin.jar /app/aqy-admin.jar

EXPOSE 7070

ENTRYPOINT ["java", "-Duser.timezone=Asia/Shanghai", "-Xms512m", "-Xmx1024m", "-jar", "/app/aqy-admin.jar"]
