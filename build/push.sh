#!/bin/bash -x

set -eu

current_directory="$( cd "$(dirname "$0")" ; pwd -P )"

source "${current_directory}/common.sh"

cd "${current_directory}/.."

mvn versions:set -DnewVersion=$(git describe)

echo "Logging in to Dockerhub."
docker login -u "${DOCKERHUB_USERNAME}" -p "${DOCKERHUB_PASSWORD}"

project_name=$(xml2 < pom.xml | grep '/project/artifactId=' | sed 's/\/project\/artifactId=//')
docker_image_version=$(xml2 < pom.xml | grep '/project/version=' | sed 's/\/project\/version=//')
docker_image_tag="logreposit/${project_name}:${docker_image_version}"

echo "Pushing docker image ${docker_image_tag} ..."
docker push ${docker_image_tag}
echo "Successfully pushed ${docker_image_tag}"
