
Android DownloadableFonts Sample With Custom Source
===================================================

This sample demonstrates how to use the Downloadable Fonts feature introduced in Android O, with
a custom source for downloading them. This way, we can have private fonts (such as ProximaNova,
or the one you are paying for) instead of bundling it in the APK. This means, there is no need
to bundle the font as an asset.

This repository is just a POC showing how you can achieve it. By no means you should have this code
in production environments, since it's just a demonstration on how to achieve it. It's up to  you
to create a more robust API to handle your own source of fonts.

This project will contain inside Roboto, while we have used ProximaNova in the images shown.

How
===

The method is quite simple:
- We create our own ContentProvider (actually, a FileProvider)
  * We declare it in our manifest, being able to serve files in the path 'file' (since FontContract expects them there)
  * We declare it in our manifest, being able to grant permission to uris, since we will be working with font files
- We handle the query of our FileProvider
  * We should check that the uri matches the ones we support
  * We should check authorities match
  * We should validate the selection criteria they asked for, and find the appropiate font they have asked for. This might require us to download it from our private source.
  * We will have to create a cursor (preferably a MatrixCursor, for easy manipulation of contents)
  * We will have to fill the cursor with rows based on the query, taking into account the projections asked for
  * We will have to create a content uri serving the font file they asked for. Be careful the name of the font file should be its ID
  * Grant permissions to the content uri you have served. So they can read the fd.
- When declaring a font (either by request or by a family xml), be sure to use your package, authority of the provider and certificate signature you have signed the APK containing the provider with.

That's the whole process. Quite easy :)

Usage
=====

In this case, we have defined the font 'Lobster Two' as our own. We will be "downloading it" (we have it as an asset, but we will never expose it, just simulate we download it) and serving it from our own content-provider.

If you install the APK, you will see a second text using 'Lobster Two' right from the start of the app (it's actually ProximaNova), with our own provider.

If you select 'Lobster Two' from the list, and request it (thus, in the middle of the app flow) you will also see our 'Lobster Two' (ProximaNova) getting queried and shown.

### Actual Lobster Two looks like:

![](https://raw.githubusercontent.com/saantiaguilera/android-poc-CustomDownloadableFonts/master/screenshots/real_lobster_two.png)

### The Lobster Two we see both from start and via request (ProximaNova):

![](https://raw.githubusercontent.com/saantiaguilera/android-poc-CustomDownloadableFonts/master/screenshots/our_lobster_two.png)

Introduction
------------

There are two ways of requesting a font to download.
To request a font to download from Java code, you need to create a [FontRequest][1] class first like
this:
```java
FontRequest request = new FontRequest(
    authority,
    package,
    query,  // Query
    R.array.certificates); // Certificates
```
The parameters `ProviderAuthority`, `ProviderPackage` are given by a font provider, by default we
usually use the Google authority and package as they have their own FontProvider that can handle
the fonts shown in [link](fonts.google.com). In our case, we will be creating our own content provider
that will be able to serve fonts, but with our own authority, package and certificates.

The third parameter is a query string about the requested font. The syntax of the query is defined
by the font provider.

Pass the request instance to the `requestFont` method in the [FontsContractCompat][2].
```java
FontsContractCompat.requestFont(context, request, callback, handler);
```
The downloaded font or an error code if the request failed will be passed to the callback.
The example above assumes you are using the classes from the support library. There are
corresponding classes in the framework, but the feature is available back to API level 14 if you
use the support library.

You can declare a downloaded font in an XML file and let the system download it for you and use it
in layouts.
```xml
<font-family xmlns:app="http://schemas.android.com/apk/res-auto"
        app:fontProviderAuthority="com.google.android.gms.fonts"
        app:fontProviderPackage="com.google.android.gms"
        app:fontProviderQuery="Lobster Two"
        app:fontProviderCerts="@array/com_google_android_gms_fonts_certs">
</font-family>
```
By defining the requested font in an XML file and putting the `preloaded_fonts` array and the
meta-data tag in the AndroidManifest, you can avoid the delay until the font is downloaded by the
first attempt.
```xml
<resources>
    <array name="preloaded_fonts" translatable="false">
        <item>@font/lobster_two</item>
    </array>
</resources>
```

```xml
<application >
    ...
    <meta-data android:name="preloaded_fonts" android:resource="@array/preloaded_fonts" />
    ...
</application>
```

[1]: https://developer.android.com/reference/android/support/v4/provider/FontRequest.html
[2]: https://developer.android.com/reference/android/support/v4/provider/FontsContractCompat.html

Pre-requisites
--------------

- Android SDK 27
- Android Build Tools v27.0.2
- Android Support Repository

Screenshots
-------------

<img src="screenshots/screenshot-1.png" height="400" alt="Screenshot"/> 

Getting Started
---------------

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

Support
-------

- Google+ Community: https://plus.google.com/communities/105153134372062985968
- Stack Overflow: http://stackoverflow.com/questions/tagged/android

If you've found an error in this sample, please file an issue:
https://github.com/googlesamples/android-DownloadableFonts

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.

License
-------

Copyright 2017 The Android Open Source Project, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
