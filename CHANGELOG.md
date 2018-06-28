## 1.6.0 (2018-06-28)

### Features

- Add Lambda support for Skygear Data Type
- Auto save asset when saving record and lambda
- Implement joining queries with 'and'

### Other Notes

- Upgrade websocket lib to 1.3.8

## 1.5.0 (2018-04-23)

### Features

- Implement request verification and submit code

### Bug Fixes

- resetPassword API bug fix, change expireAt from Date to long
- Fix JSON serialization error of auth_data in signup and login api which
    happen in Android API version <= 18
- Should allow `JSONObject.NULL` in lambda arguments

### Other Notes

- Add admin prefix to disable/enable user methods
- Remove default endpoint in container
- Count support in query #211

## 1.4.1 (2018-03-09)

### Other Notes

- Revert "Upgrade gradle to version 4" for fixing missing dependencies

## 1.4.0 (2018-03-07)

### Features

- Support enable/disable user account

### Bug Fixes

- Fix potential memory leak

### Other Notes

- Update error code list
- Change func name and args for consistency for sso unlinkOAuthProvider api
- Upgrade gradle to version 4
- Supports various argument classes for callLambdaFunction
- Add checkstyle lint checking

## 1.3.1 (2018-02-01)

### Features

- Add ErrorSerializer
- Add SSO Plugin
    - Initial loginOAuthProvider (#150)
    - Link oauth provider (#168)
    - Unlink provider (#169)
    - Login and link with access token (#167)

### Bug Fixes

- Fix maybe null pointer exeception
- Serialise profile when signup

## 1.3.0 (2018-01-04)

### Features

- Add PubsubListener to hook up with PubsubClient onOpen, onClose and onError
  (#173)
- Support calling lambda with map (#120)
- Add loginWithCustomToken (#172)

### Bug Fixes

- Check null value when serializing and deserializing reserved fields

### Other Notes

- Reduce log on android test
- Change tools:context in activity_login layout

## 1.2.0 (2017-12-11)

### Bug Fixes

- Set AccessControl.Entry and AccessControl.Level public

## 1.1.1 (2017-10-23)

### Features

- Implement change password (#147)
- Implement save records atomically (#134)
- Add sdk version to request header

### Bug Fixes

- Fix example project login crash (#152)

### Other Notes

- Upgrade websocket lib (#165)
- Add support for Android SDK version 26 (#158)

## 1.1.0 (2017-08-07)

### Incompatible Changes

- Authentication-related function will return Record instead of User object

  In previous version of SKYKit, authentication methods return a User object
  which contains user-related information such as User ID, username and
  email. These information is moved to user record and the authentication
  methods are updated to return Record instead.

### Features

- Move user related role api to AuthContainer
- New signup login, remove user object (#142)

### Other Notes

- Upload doc prefixed with version to s3 bucket for CI

## 1.0.0 (2017-06-30)

### Incompatible Changes

- Update API grouping (#133, SkygearIO/features#71)

### Features

- Add forgot password methods to AuthContainer

### Other Notes

- Add copyright notice
- Update gradle build to 2.3.0 (#131)

## 0.24.1 (2017-05-23)

Revert "Move skygear to skygear_core module"

## 0.24.0 (2017-05-23)

### Features

- Set target SDK version to 25 (#130)

### Other Notes

- Move skygear to skygear_core module (#123)

## 0.23.1 (2017-05-02)

### Bug Fixes

- Remove dependency on jitpack.io (#124)

## 0.23.0 (2017-04-20)

### Features

- Return user friendly error message in error (#89)

### Other Notes

- Remove ignoring lint error on example project
- Change VolleyPlus to Volley

## 0.22.2 (2017-03-31)

### Features

- Add user query by username request and related methods
- Support content type for asset serialization (#92)

### Bug Fixes

- Fix the crash on parsing date string (#109)

### Other Notes

- Replaced StringBuffer with StringBuilder

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
