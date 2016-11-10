## 0.19.0 (2016-11-10)

No change since last release

## 0.18.0 (2016-10-26)

No change since last release

## 0.17.2 (2016-10-12)

### Features

- Allow setting `JSONObject.NULL` to record field (#61)
- Support transient include distance predicate (#56)

### Bug fixes

- Fix wss connection fail issue (#62)

## 0.17.1 (2016-10-04)

### Features

- Support distance related sorting predicates (#56)
- Support geo location (#56)
- Support transient and record reference (#49)
- Support asset (#43)


## 0.17.0 (2016-09-15)

### Features

- Add last login / last seen fields to user model (SkygearIO/skygear-server#110)

### Bug fixes

- Fix lambda function argument serialization issue (#46)


## 0.16.0 (2016-09-02)

### Features

- Add `whoami` API for querying and update currnetUser from server (SkygearIO/skygear-server#111)

### Other Notes

- Update gradle


## 0.15.0 (2016-08-17)

### Other Notes

- Make Endpoint / API Key configurable for Sample App (#35)


## 0.14.0 (2016-07-26)

### Features

- Allow to set pubsub handler to run in background (#20)
- Support for Skygear Lambda Function (#27)
- Enable to set admin / default roles (#14)

### Bug fixes

- Add exception for invalid pubsub data (SkygearIO/skygear-SDK-JS#27)


## 0.13.0 (2016-07-08)

### Features

- Enable to set request timeout (#18)
- Implement Public, Role-based and User-based ACL (#14)
- Implement user query and update role ($14)
- Implement Skygear Pubsub (#13)
- Update the example project

### Bug fixes

- Make `RecordSerializer` work with JSON{Object, Array} (#23)

### Other notes

- Upload Javadoc to [Documentation Site](https://docs.skygear.io/android/reference/)


## 0.0.1 (2016-06-28)

This is the initial release of Skygear Android SDK. The SDK includes the
followings features:

- Config for connnecting Skygear Server
- User Signup, Login and Logout
- Record Create, Query, Update and Delete
