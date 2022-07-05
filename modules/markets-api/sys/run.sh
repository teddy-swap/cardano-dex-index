docker run \
    -v /home/apps/analytics-api-scala/conf/app.conf:/cardano-analytics-scala/conf/conf.env:ro \
    --restart=always \
    --network="dev" \
    --name=analytics-api-scala \
    --expose 8081:8081 \
    -d timooxaaa/analytics-api-spectrum:0.0.1