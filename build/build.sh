#!/bin/bash -x

set -eu

current_directory="$( cd "$(dirname "$0")" ; pwd -P )"

cd "${current_directory}/.."

mvn versions:set -DnewVersion=$(git describe)

project_name=$(xml2 < pom.xml | grep '/project/artifactId=' | sed 's/\/project\/artifactId=//')

rm -f "target/${project_name}.jar"
mvn -DskipTests=true clean package
rm -f docker/app.jar
cp "target/${project_name}.jar" docker/app.jar

docker_image_version=$(xml2 < pom.xml | grep '/project/version=' | sed 's/\/project\/version=//')
docker_image_tag="logreposit/${project_name}:${docker_image_version}"

echo "Building docker image ${docker_image_tag} ..."
cd ./docker
docker build -t ${docker_image_tag} .
echo "Successfully built image ${docker_image_tag}"
