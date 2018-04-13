import Vue from 'vue'
import App from './App'
import router from './router'
import store from './store'
import fastclick from 'fastclick'
import VueLazyLoad from 'vue-lazyload'
import VConsole from 'vconsole'

import 'common/stylus/index.styl'

Vue.config.productionTip = false

// process只在node运行环境中作为node的全局变量存在，类比浏览器中的全局变量window
// process.env.NODE_ENV 在build/build.js开头中有定义，以供npm build使用
const debug = process.env.NODE_ENV !== 'production'
/* eslint-disable no-unused-vars */
let vConsole = debug ? new VConsole() : null

fastclick.attach(document.body)

Vue.use(VueLazyLoad, {
  loading: require('./common/image/default.png')
})

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
