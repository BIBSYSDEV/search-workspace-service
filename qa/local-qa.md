# Testing localy

Follow the instructions to debug and test with a local opensearch

## Testing with invoking lambda

Prereqs:

- AWS Cli
- AWS Tookkit for Intellij
- Docker

### Local opensearch in docker

Create a virtual network in docker so that the SAM container and opensearch container can communicate

     docker network create sws-network

Start the docker image without security and in the new network

    docker run -p 9200:9200 -p 9600:9600 -e "discovery.type=single-node" -e "plugins.security.disabled=true" --network="sws-network" opensearchproject/opensearch:2.0.1



### Run the lambda
Use the RunConfig PutIndexWithConfig in Intellij
Run/Debug it
