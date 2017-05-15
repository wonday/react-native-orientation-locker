//
//  react-native-orientation-locker
//  
//
//  Created by Wonday on 17/5/12.
//  Copyright (c) wonday.org All rights reserved.
//

'use strict';
import React,{ Component, PropTypes } from 'react';

const OrientationNative = require('react-native').NativeModules.Orientation;
const { NativeEventEmitter, DeviceEventEmitter } = require('react-native');
const LocalEventEmitter =  new NativeEventEmitter(OrientationNative);

var listeners = {};

var id = 0;
var META = '__listener_id';

function getKey(listener){

    if (!listener.hasOwnProperty(META)){
        if (!Object.isExtensible(listener)) {
            return 'F';
        }
        Object.defineProperty(listener, META, {
            value: 'L' + ++id,
        });
    }
    return listener[META];

};

export default class Orientation {

    static getOrientation = (cb) => {

        OrientationNative.getOrientation((orientation) =>{
            cb(orientation);
        });

    };

    static lockToPortrait = () => {

        OrientationNative.lockToPortrait();

    };

    static lockToPortraitUpsideDown = () => {

        OrientationNative.lockToPortraitUpsideDown();

    };

    static lockToLandscapeRight = () => {

        OrientationNative.lockToLandscapeRight();

    };

    static lockToLandscapeLeft = () => {

        OrientationNative.lockToLandscapeLeft();

    };

    static unlockAllOrientations = () => {

        OrientationNative.unlockAllOrientations();

    };

    static addOrientationListener = (cb) => {

        var key = getKey(cb);
        listeners[key] = LocalEventEmitter.addListener("orientationDidChange",
            (body) => {
              cb(body.orientation);
            });

    };

    static removeOrientationListener = (cb) => {

        var key = getKey(cb);
        if (!listeners[key]) {
            return;
        }
        listeners[key].remove();
        listeners[key] = null;

    };

    static getInitialOrientation = () => {

        return OrientationNative.initialOrientation;

    };
};
