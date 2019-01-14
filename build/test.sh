#!/bin/bash -x

set -eu

current_directory="$( cd "$(dirname "$0")" ; pwd -P )"

cd "${current_directory}/.."

mvn versions:set -DnewVersion=$(git describe)

mvn clean test
