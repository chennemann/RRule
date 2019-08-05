[![Release](https://img.shields.io/github/release/PhilJay/rrule.svg?style=flat)](https://jitpack.io/#PhilJay/rrule)


# RRule
Kotlin implementation for handling iCalendar (RFC 5545) recurrence rules 

## Dependency

Add the following to your **build.gradle** file:
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.PhilJay:rrule:1.0.0'
}
```

Or add the following to your **pom.xml**:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.PhilJay</groupId>
    <artifactId>rrule</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Sample Usage

Transform iCalendar RFC 5545 String to RRule object:

```kotlin
val rrule = RRule("RRULE:FREQ=MONTHLY;INTERVAL=2;COUNT=10;BYDAY=1SU,-1SU")
```

Transform RRule object to iCalendar RFC 5545 String:

```kotlin
val rfc5545String = rrule.toRFC2445String()
```
