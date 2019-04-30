# Test CAS browser login

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
* Assert at login page

## Correct User

* Login with configured username and password
* Verify SCM open
* Logged in username equals configured display name
* Open Me page
* Display name equals configured display name
* Email equals configured email
* Groups contains configured ces admin group

