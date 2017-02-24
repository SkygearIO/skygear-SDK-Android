## 0.22.1 (2017-02-24)

### Features

- Support limit and offset for record query (#98)

## 0.22.0 (2017-02-10)

No changes since last release.

## 0.21.0 (2017-01-11)

### Features

- Provide package name when register device (SkygearIO/skygear-server#239)

    Send package name as `topic` parameter when registering device, it will
    help cloud code to send push notification to segment of device.


## 0.20.0 (2016-12-20)

### Features

- Support signup anonymously (#83)
- Support unregister device (SkygearIO/skygear-server#245, SkygearIO/skygear-server#249)
- Implement Parcelable on Record class
- Implement UnknownValue class (skygeario/skygear-server#231)
- Support Push Notification (#67)

### Incompatible changes

- Add error code to request error (#75)

    It makes a breaking change to all `*ResponseHandler` changing the
    interface of fail callbacks

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
