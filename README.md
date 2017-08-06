# TDACardReader

Simple TDACardReader for react-native

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them

```
yarn install
react-native link
```

### Installing

```
yarn install
react-native link
react-native run-android
```
And install TDAService.apk to android device. You can download from Google play service..
Because it need from TDA library.

## Running the tests

You can run the app and test..

Click `Install license` and this will download license file. After prepare card reader message show
Connect the reader to device and click `Read card` then we will see the result text.
Click `Read image` will load Base64 string and convert to image file. If click `Clear` it should stop the TDAService and clear the result data.

### Methods

Simple method in index.android.js

import NativeModule and call it TDA.

```
var TDA = NativeModules.TDACardReader;
```

Simple toast message (string, int)

```
TDA.show('message', duration);
```

Init license and start service

```
TDA.initLicense().then({...}).catch({...})
```

Read all text data from ID card

```
TDA.readCard().then({...}).catch({...})
```

Read image data from ID card

```
TDA.readImage().then({...}).catch({...})
```

Exit the service

```
TDA.exit().then({...}).catch({...})
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Authors

Sarawut Popadcha

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone who's code was used
* Inspiration
* etc
