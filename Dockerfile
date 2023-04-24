FROM registry.cn-hangzhou.aliyuncs.com/commonality/chrome-headless:0.0.3

RUN mkdir file
ADD ttp-web/target/*.jar app.jar
CMD ["java", "-jar", "-Xmx1500m", "-Xms1500m", "/home/admin/app.jar"]