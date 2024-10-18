mvn clean package -DskipTests -Dcheckstyle.skip

## console-ui 打包
* node:v14.20.1
* npm:6.14.17

安装依赖

```shell
nvm use 14.20.1
npm config set strict-ssl false  
npm install --registry=https://registry.npm.taobao.org
```

打包

```shell
cd console-ui
export PATH="./node_modules/.bin:$PATH"  
npm run build
```

上一步骤应该会自动复制到console项目

```
cp ./dist/js/main.js ../console/src/main/resources/static/js/main.js 
cp ./dist/css/main.css ../console/src/main/resources/static/css/main.css
```