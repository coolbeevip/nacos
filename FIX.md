mvn clean package -DskipTests -Dcheckstyle.skip -Parm64

## console-ui 打包

* node:v8.16.0
* npm:6.4.1

安装依赖

```shell
npm install --registry=https://registry.npm.taobao.org
```

打包

```shell
npm run build
```

cp ./dist/js/main.js ../console/src/main/resources/static/js/main.js 
cp ./dist/css/main.css ../console/src/main/resources/static/css/main.css
