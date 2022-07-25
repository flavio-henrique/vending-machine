# Vending Machine App

It's a Spring boot App based on Java version 8, it was chosen this tech stack because is the stack that I have being working lately.


## Run Locally
Pre-requisites:
- Open JDK version 8
- Docker version 20

Run `./mvnw clean package`

Run `cp target/vending-machine-0.0.1-SNAPSHOT.jar docker`

Run `(cd ./docker/ && docker-compose up)`

The App will be running on `localhost:8080/api`