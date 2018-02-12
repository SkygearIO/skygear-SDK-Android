#!/bin/bash -e

./gradlew :skygear_example:assembleRelease

VERSION=$(git describe --always --tags)

mkdir -p build/apk
cp skygear_example/build/outputs/apk/release/skygear_example-release.apk build/apk/skygear-example-latest.apk
cp skygear_example/build/outputs/apk/release/skygear_example-release.apk build/apk/skygear-example-$VERSION.apk
