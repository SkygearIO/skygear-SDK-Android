#!/bin/bash -e

if [ -n "$TRAVIS_TAG" ]; then

  if [[ -z "$BINTRAY_USER" ]]; then
    echo >&2 "Error: \$BINTRAY_USER is not set"
    exit 1
  fi

  if [[ -z "$BINTRAY_API_KEY" ]]; then
    echo >&2 "Error: \$BINTRAY_API_KEY is not set"
    exit 1
  fi

  ./gradlew :skygear:bintrayUpload
fi

# Update docs.skygear.io

if [ -n "$TRAVIS_TAG" ]; then
    if [ "$TRAVIS_TAG" -eq "latest" ]; then
        make doc-deploy VERSION=latest
    else
        make doc-deploy VERSION="v$TRAVIS_TAG"
    fi
else
    make doc-deploy VERSION="${TRAVIS_BRANCH/master/canary}"
fi
