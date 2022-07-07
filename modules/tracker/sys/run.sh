docker run \
    -v /home/apps/tracker-scala/conf/app.conf:/cardano-analytics-scala/conf/conf.env:ro \
    --restart=always \
    --network="dev" \
    --name=tracker-scala \
    -d timooxaaa/tracker-spectrum:0.0.1