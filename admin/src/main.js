import Vue from 'vue'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

import '@/styles/index.scss' // global css

import App from './App'
import router from './router'

Vue.use(ElementUI)

Vue.config.productionTip = false

import VueClipboard from 'vue-clipboard2'

VueClipboard.config.autoSetContainer = true // add this line
Vue.use(VueClipboard)

/**
 * 替换所有匹配exp的字符串为指定字符串
 * @param exp 被替换部分的正则
 * @param newStr 替换成的字符串
 */
// eslint-disable-next-line no-extend-native
String.prototype.replaceAll = function(exp, newStr) {
  return this.replace(new RegExp(exp, 'gm'), newStr)
}

/**
 * 原型：字符串格式化
 * @param args 格式化参数值
 */
// eslint-disable-next-line no-extend-native
String.prototype.format = function(args) {
  let result = this
  if (arguments.length < 1) {
    return result
  }

  let data = arguments // 如果模板参数是数组
  if (arguments.length === 1 && typeof (args) === 'object') {
    // 如果模板参数是对象
    data = args
  }
  for (const key in data) {
    const value = data[key]
    if (undefined !== value) {
      result = result.replaceAll('\\{' + key + '\\}', value)
    }
  }
  return result
}

Vue.directive('select-load-more', {
  bind(el, binding) {
    // 获取element-ui定义好的scroll盒子
    const SELECTWRAP_DOM = el.querySelector('.el-select-dropdown .el-select-dropdown__wrap')

    SELECTWRAP_DOM.addEventListener('scroll', function() {
      /*
       * scrollHeight 获取元素内容高度(只读)
       * scrollTop 获取或者设置元素的偏移值,常用于, 计算滚动条的位置, 当一个元素的容器没有产生垂直方向的滚动条, 那它的scrollTop的值默认为0.
       * clientHeight 读取元素的可见高度(只读)
       * 如果元素滚动到底, 下面等式返回true, 没有则返回false:
       * ele.scrollHeight - ele.scrollTop === ele.clientHeight;
       */
      const CONDITION = this.scrollHeight - this.scrollTop - 1 <= this.clientHeight

      if (CONDITION) {
        binding.value()
      }
    })
  }
})

new Vue({
  el: '#app',
  router,
  render: h => h(App)
})
