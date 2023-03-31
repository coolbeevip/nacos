mvn clean package -DskipTests -Dcheckstyle.skip

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

```
cp ./dist/js/main.js ../console/src/main/resources/static/js/main.js 
cp ./dist/css/main.css ../console/src/main/resources/static/css/main.css
```

## 关键代码

#### 默认允许跨域访问

console/src/main/java/com/alibaba/nacos/console/config/ConsoleConfig.java

    ```java
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.setMaxAge(18000L);
        config.addAllowedMethod("*");
        config.addAllowedOriginPattern("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }