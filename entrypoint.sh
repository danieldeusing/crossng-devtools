#!/bin/bash

sed -i "s|__BACKEND_URL__|${BACKEND_URL}|g" /app/assets/env.js
sed -i "s|__SECURITY_X_API_KEY__|${SECURITY_X_API_KEY}|g" /app/assets/env.js
sed -i "s|__REFRESH_INTERVAL__|${REFRESH_INTERVAL}|g" /app/assets/env.js

java -jar /app/crossng-devtools.jar
