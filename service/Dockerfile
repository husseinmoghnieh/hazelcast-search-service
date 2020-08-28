FROM openjdk:11-jre-stretch
RUN apt update && apt install -y python python-pip && rm -rf /var/lib/apt/lists/*

RUN pip install boto3

VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
COPY . /service

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

RUN rm -rf /service/output
RUN rm -rf /service/target
RUN rm -rf /service/src/test

EXPOSE 5701-5708

WORKDIR /service