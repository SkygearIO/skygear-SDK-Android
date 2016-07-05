#!/bin/bash -e

VERSION=`git describe --always --tags`

if [[ -z "$SLACK_CHANNEL" ]]; then
  echo >&2 "Error: \$SLACK_CHANNEL is not set"
  exit 1
fi

if [[ -z "$SLACK_TOKEN" ]]; then
  echo >&2 "Error: \$SLACK_TOKEN is not set"
  exit 1
fi

cd skygear/build
tar czf skygear-SDK-Android-test-report.tar.gz reports

curl https://slack.com/api/files.upload \
    -F file=@skygear-SDK-Android-test-report.tar.gz \
    -F channels=$SLACK_CHANNEL \
    -F token=$SLACK_TOKEN \
    -F title="Android SDK Test Report ($VERSION)"
