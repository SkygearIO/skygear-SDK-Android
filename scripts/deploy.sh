#!/bin/bash -e

if [[ -z "$BINTRAY_USER" ]]; then
  echo >&2 "Error: \$BINTRAY_USER is not set"
  exit 1
fi

if [[ -z "$BINTRAY_API_KEY" ]]; then
  echo >&2 "Error: \$BINTRAY_API_KEY is not set"
  exit 1
fi

./gradlew :skygear:bintrayUpload
