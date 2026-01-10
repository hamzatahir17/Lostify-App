FROM nginx:alpine


COPY app/release/app-release.apk /usr/share/nginx/html/lostify.apk

# Dashboard Design
RUN echo '<html><body style="text-align:center; padding-top:50px; font-family:Arial, sans-serif; background:#f4f4f4;">' > /usr/share/nginx/html/index.html && \
    echo '<div style="background:white; padding:40px; border-radius:10px; display:inline-block; box-shadow:0 0 15px rgba(0,0,0,0.1);">' >> /usr/share/nginx/html/index.html && \
    echo '<h1 style="color:#007bff;">Lostify Project Dashboard</h1>' >> /usr/share/nginx/html/index.html && \
    echo '<p style="color:green; font-weight:bold;"></p>' >> /usr/share/nginx/html/index.html && \
    echo '<p>This container serves the signed version of the Lostify App.</p>' >> /usr/share/nginx/html/index.html && \
    echo '<br><a href="/lostify.apk" style="background:#28a745; color:white; padding:15px 30px; text-decoration:none; border-radius:5px; font-size:18px;">Download APK</a>' >> /usr/share/nginx/html/index.html && \
    echo '<hr style="margin:25px 0;">' >> /usr/share/nginx/html/index.html && \
    echo '<p style="text-align:left; font-size:14px; color:#555;"><b>Instruction:</b> Download and install on your device/emulator.</p>' >> /usr/share/nginx/html/index.html && \
    echo '</div></body></html>' >> /usr/share/nginx/html/index.html

EXPOSE 80