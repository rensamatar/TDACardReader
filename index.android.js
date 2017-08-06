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
  Image,
  NativeModules,
  TouchableHighlight,
} from 'react-native';
import _ from 'lodash';

// import native modules.
var TDA = NativeModules.TDACardReader;

// Show Toast message
const onButtonPress = () => {
  TDA.show('Clear data..', 2000);
};

export default class TDACardReader extends Component {

  constructor(props) {
    super(props)

    this.state = {
      resultText: null,
      base64ImageData: null,
      base64Text: null
    }

    this.onInit = this.onInit.bind(this);
    this.onReadCard = this.onReadCard.bind(this);
    this.onReadImage = this.onReadImage.bind(this);
    this.onClear = this.onClear.bind(this);
  }

  // Init Library
  onInit() {
    console.log('onInit')
    TDA.show('Load license...', 1000);
    TDA.initLicense()
      .then((response) => {
        console.log('__RESPONSE__', response)
        this.setState({ resultText: response })
      })
      .catch((ex) => {
        console.log('__ERROR__', ex)
        this.setState({ resultText: ex.code })
      })
  };

  // Read card
  onReadCard() {
    console.log('onReadText')
    TDA.readCard()
      .then((response) => {
        console.log('__RESPONSE__', response)
        this.setState({ resultText: response })
      })
      .catch((ex) => {
        console.log('__ERROR__', ex)
        this.setState({ resultText: ex.code })
      })
  };

  // Read image
  onReadImage() {
    console.log('onReadImage')
    TDA.readImage()
      .then((response) => {
        console.log('__RESPONSE__', response)
        this.setState({
          base64Text: 'data:image/png;base64,' + response,
          base64ImageData: 'data:image/png;base64,' + response,
        })
      })
      .catch((ex) => {
        console.log('__ERROR__', ex)
        this.setState({ resultText: ex.code })
      })
  };

  // Clear
  onClear() {
    console.log('onClear')
    TDA.exit().
      then((response) => {
        console.log('__RESPONSE__', response)
      })
      .catch((ex) => {
        console.log('__ERROR__', ex)
        this.setState({ resultText: ex.code })
      })
    this.setState({ resultText: null, base64ImageData: null, base64Text: null })
  }

  render() {
    //console.log('__TDA___', TDA);
    console.log('IMAGE SOURCE', this.state.base64ImageData)
    let replace = _.replace(this.state.resultText, '#', '\n');
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>
          Welcome to React Native!
        </Text>
        <View style={{ flexDirection: 'row', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <Button style={styles.buttonStyles} title='Init License' onPress={this.onInit} />
          <Button style={styles.buttonStyles} title='Read card' onPress={this.onReadCard} />
          <Button style={styles.buttonStyles} title='Read Image' onPress={this.onReadImage} />
          <Button style={styles.buttonStyles} title='Clear' onPress={this.onClear} />
        </View>
        <View style={{ marginHorizontal: 10, marginTop: 20, flexDirection: 'column' }}>
          <View style={{ flexDirection: 'row', justifyContent: 'space-around' }}>
            <View style={{ width: 200 }}>
              <Text>Result :</Text>
              <Text>{this.state.resultText}</Text>
            </View>
            <View style={{ width: 100 }}>
              <Text>Image :</Text>
              {
                (this.state.base64ImageData == null ?
                  <Image source={{ uri: 'https://facebook.github.io/react/img/logo_og.png' }} style={{ marginTop: 10, height: 100, width: 100 }} /> :
                  <Image source={{ uri: this.state.base64ImageData }} style={{ marginTop: 10, height: 100, width: 100 }} />
                )
              }
              <Text>Base 64:</Text>
              <Text numberOfLines={6} ellipsizeMode={'tail'}>{this.state.base64Text}</Text>
            </View>
          </View>
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingHorizontal: 5,
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
    alignItems: 'center',
    justifyContent: 'center'
  },
  buttonStyles: {
    height: 80,
    marginTop: 8,
    marginBottom: 10,
    backgroundColor: '#dddddd',
    marginHorizontal: 5
  }
});

AppRegistry.registerComponent('TDACardReader', () => TDACardReader);
