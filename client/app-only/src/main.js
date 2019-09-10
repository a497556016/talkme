import Vue from 'vue'

import http from './net/http'

require('./mock')

import '@/assets/style/index.less'

import 'font-awesome/css/font-awesome.min.css'

import iMServer from './util/io'

import dialog from './util/plugin/dialog'
Vue.use(dialog);

Vue.config.productionTip = false
Vue.prototype.axios = http;
Vue.prototype.iMServer = iMServer;

console.log(navigator)

import {setup} from "./setup"

//初始化系统
setup();