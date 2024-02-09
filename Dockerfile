# java 17 버전을 가진 이미지를 사용한다.
FROM amazoncorretto:17

# build 한 jar 파일을 app.jar 이름으로 추가한다.
ARG JAR_FILE=/build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 도커 이미지를 실행할 때 실행될 명령어
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTIONS} -jar /app.jar"]
