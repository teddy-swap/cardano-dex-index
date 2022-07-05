docker run \
    -v /home/apps/db-writer-scala/conf/app.conf:/cardano-analytics-scala/conf/conf.env:ro \
    --restart=always \
    --network="dev" \
    --name=db-writer-scala \
    -d timooxaaa/db-writer-spectrum:0.0.1