/**
 * 添加加密依赖，目前未按需引入，因此打的包相对较大 add by wanjun @2021/04/26
 */
import * as CryptoJS from 'crypto-js';
/**
 *加密处理, add by wanjun @2021/04/26
 */
export const encryption = params => {
  let { data, type, param, key } = params;
  const result = JSON.parse(JSON.stringify(data));
  if (type === 'Base64') {
    param.forEach(ele => {
      result[ele] = btoa(result[ele]);
    });
  } else {
    param.forEach(ele => {
      let data = result[ele];
      key = CryptoJS.enc.Latin1.parse(key);
      let iv = key;
      // 加密
      let encrypted = CryptoJS.AES.encrypt(data, key, {
        iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.ZeroPadding,
      });
      result[ele] = encrypted.toString();
    });
  }
  return result;
};
