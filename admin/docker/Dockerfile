FROM nginx:latest
ENV LANG C.UTF-8
ENV TZ Asia/Shanghai
MAINTAINER caoqiang <caoqiang@jiuqi.com.cn>
# 使用自定义nginx.conf配置端口和监听
RUN rm -rf /etc/nginx/conf.d/default.conf
ADD default.template /etc/nginx/conf.d/nginx.conf.template
ADD dist /app/html
CMD ["/bin/bash", "-c", "envsubst '${BACKEND_URL},${BACKEND_PATH}' < /etc/nginx/conf.d/nginx.conf.template > /etc/nginx/conf.d/nginx.conf && exec nginx -g 'daemon off;'"]

