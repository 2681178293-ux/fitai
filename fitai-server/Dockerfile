 # 指定基础镜像，必须为第一个命令
FROM openjdk:21-ea-jdk
# 维护者信息
MAINTAINER "wanho"
# 将本地文件添加到容器中
ADD target/fitai-server-1.0.0.jar /opt/jar/app.jar
# 暴露端口
EXPOSE 8080
# 构建容器后调用
ENTRYPOINT ["java","-jar","/opt/jar/app.jar"]
