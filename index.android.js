/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  Button,
  NativeModules,
  TouchableHighlight,
} from 'react-native';

// import native modules.
var TDA = NativeModules.TDA;
var buttonStyles = { width: 200, height: 80, marginTop: 8, marginBottom: 10, backgroundColor: '#dddddd', padding: 10 };

// Show Toast message
const onButtonPress = () => {
  TDA.show('Hello World', 2000);
};

export default class TDACardReader extends Component {

  constructor(props) {
    super(props)

    this.onInit = this.onInit.bind(this);
    this.onStartProcess = this.onStartProcess.bind(this);
    this.onReadText = this.onReadText.bind(this);
    this.onReadImage = this.onReadImage.bind(this);
  }

  // Init Library
onInit() {
  console.log('onInit')
  TDA.initLicense(
    {}, // not yet
    (response) => { console.log('__RESPONSE__', response) },
    (error) => { console.log('__ERROR__', error) },
  )
};

// Start process
onStartProcess() {
  console.log('onStartProcess')
  TDA.startProcess(
    {}, // not yet
    (response) => { console.log('__RESPONSE__', response) },
    (error) => { console.log('__ERROR__', error) },
  )
};

// Read text
onReadText() {
  console.log('onReadText')
  TDA.readText(
    {}, // not yet
    (response) => { console.log('__RESPONSE__', response) },
    (error) => { console.log('__ERROR__', error) },
  )
};

// Read image
onReadImage() {
  console.log('onReadImage')
  TDA.readImage(
    {}, // not yet
    (response) => { console.log('__RESPONSE__', response) },
    (error) => { console.log('__ERROR__', error) },
  )
};


  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Welcome to React Native!
        </Text>
        <Text style={styles.instructions}>
          To get started, edit index.android.js
        </Text>
        <Text style={styles.instructions}>
          Double tap R on your keyboard to reload,{'\n'}
          Shake or press menu button for dev menu
        </Text>
        <View style={{ flex: 1, flexDirection: 'column', justifyContent: 'center' }}>
          <Button title='Click' onPress={onButtonPress} />
          <Button title='Init License' onPress={this.onInit} />
          <Button title='Start Process' onPress={this.onStartProcess} />
          <Button title='Read Text' onPress={this.onReadText} />
          <Button title='REad Image' onPress={this.onReadImage} />
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

AppRegistry.registerComponent('TDACardReader', () => TDACardReader);
