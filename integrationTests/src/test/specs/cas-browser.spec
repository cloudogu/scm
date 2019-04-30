# Test CAS login

* Open the start page
* Assert at login page

## Correct CAS URL

* Current urlPattern matches ".*/cas/login.*scm.*"

## Logout with SCM

* Login with configured username and password
* Verify SCM open
* Logout with SCM
* Logout was successful

## Keep login

* Login with configured username and password
* Verify SCM open
* Open the start page
* Verify SCM open

## Logout with CAS

* Login with configured username and password
* Verify SCM open
* Logout with CAS
* Logout was successful
* Open the start page
* Current urlPattern matches ".*/cas/login.*scm.*"

## Correct User

* Login with configured username and password
* Verify SCM open
* Logged in username equals configured display name
* Open Me page
* Email equals configured email
* Display name equals configured display name
* Groups contains configured ces admin group

