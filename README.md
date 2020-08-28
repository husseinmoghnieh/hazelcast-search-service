# hazelcast-search-service

##Build docker image:
./build.sh or run: mvn install dockerfile:build -Ddocker.tag-1.0.0

## Run the service
docker-compose up

## Access H2 database console
http://localhost:17600/h2-console

## Access Swagger UI
http://localhost:17600/swagger-ui.html
