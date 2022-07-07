docker run \
    -v /home/apps/rates-resolver-scala/conf/app.conf:/cardano-analytics-scala/conf/conf.env:ro \
    --restart=always \
    --network="dev" \
    --name=rates-resolver-scala \
    -d timooxaaa/rates-resolver-spectrum:0.0.1