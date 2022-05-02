//
//  react-native-orientation-locker
//
//
//  Created by Wonday on 17/5/12.
//  Copyright (c) wonday.org All rights reserved.
//

"use strict";

import Orientation from './src/orientation';

export * from './src/hooks';
export * from './src/OrientationLocker';

export const OrientationType = {
  PORTRAIT: 'PORTRAIT',
  'PORTRAIT-UPSIDEDOWN': 'PORTRAIT-UPSIDEDOWN',
  'LANDSCAPE-LEFT': 'LANDSCAPE-LEFT',
  'LANDSCAPE-RIGHT': 'LANDSCAPE-RIGHT',
  'FACE-UP': 'FACE-UP',
  'FACE-DOWN': 'FACE-DOWN',
  UNKNOWN: 'UNKNOWN',
};

export default Orientation;
