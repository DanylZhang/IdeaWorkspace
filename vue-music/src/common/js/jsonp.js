import originJSONP from 'jsonp'

export default function jsonp(url, data, option) {
  url += (url.indexOf('?') < 0 ? '?' : '&') + param(data)

  return new Promise((resolve, reject) => {
    originJSONP(url, option, (err, data) => {
      if (!err) {
        resolve(data)
      } else {
        reject(err)
      }
    })
  })
}

function param(data) {
  let url = ''
  for (let k in data) {
    let value = data[k] !== undefined ? data[k] : ''
    // JSON.stringify会给字符串添加双引号，不是对象不需要双引号，否则有可能访问出错
    value = typeof value === 'object' ? JSON.stringify(value) : value
    url += `&${k}=${encodeURIComponent(value)}`
  }
  return url ? url.substr(1) : ''
}
