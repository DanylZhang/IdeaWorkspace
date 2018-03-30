import jsonp from '../common/js/jsonp'
import {commonParams} from './config'

export function getVKey(songMid) {
  const url = 'https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg'

  const data = Object.assign({}, commonParams, {
    g_tk: 5381,
    loginUin: 0,
    hostUin: 0,
    format: 'json',
    inCharset: 'utf8',
    outCharset: 'utf-8',
    notice: 0,
    platform: 'yqq',
    needNewCode: 0,
    cid: 205361747,
    uin: 0,
    songmid: songMid,
    filename: `C400${songMid}.m4a`,
    guid: 4269110380
  })

  const options = {
    param: 'callback',
    prefix: 'MusicJsonCallback426946307139984'
  }

  return jsonp(url, data, options)
}
