# Political Preparedness

PolitcalPreparedness app for the Android Developer Nano-Degree course. This is the 5th and final
project for the Udacity Android Kotlin Developer Nanodegree Program.

PolitcalPreparedness is an example application built to demonstrate core Android Development skills
as presented in the Udacity Android Developers Kotlin curriculum. 

This app demonstrates the following views and techniques:

* [Retrofit](https://square.github.io/retrofit/) to make api calls to an HTTP web service.
* [Moshi](https://github.com/square/moshi) which handles the deserialization of the returned JSON to Kotlin data objects. 
* [Glide](https://bumptech.github.io/glide/) to load and cache images by URL.
* [Room](https://developer.android.com/training/data-storage/room) for local database storage.
  
It leverages the following components from the Jetpack library:

* [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
* [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
* [Data Binding](https://developer.android.com/topic/libraries/data-binding/) with binding adapters
* [Navigation](https://developer.android.com/topic/libraries/architecture/navigation/) with the SafeArgs plugin for parameter passing between fragments


## Google Civics API

Google Civics API requires an API key for access and use.
For privacy reasons, this key is not included in the public code repository.

In order to build the project, you need to provide your own Google Civics API key as described
below.

The build of this app is configured in a way that it uses a build configuration field for
providing the necessary API key in the source code. It requires the definition of a string
with name `GOOGLE_CIVIC_INFO_API_KEY` in a local `gradle.properties` file (e.g.
in `~/.gradle/gradle.properties`). Alternatively, you can override the value of the
defined `resValue` directly in `app/build.gradle` (not recommended).
