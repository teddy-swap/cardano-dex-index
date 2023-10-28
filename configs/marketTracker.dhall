let LogLevel = < Info | Error | Warn | Debug >
let format = "$time - $loggername - $prio - $msg" : Text
let fileHandlers = \(path : Text) -> \(level : LogLevel) -> {_1 = path, _2 = level, _3 = format}
let levelOverride = \(component : Text) -> \(level : LogLevel) -> {_1 = component, _2 = level}
in
{ txEventsProducerConfig =
    { producerBrokers = ["redpanda-1:19092"]
    , producerTimeout = 1000
    },
  ordersProducerConfig =
    { producerBrokers = ["redpanda-1:19092"]
    , producerTimeout = 1000
    },
  poolsProducerConfig =
    { producerBrokers = ["redpanda-1:19092"]
    , producerTimeout = 1000
    },
  mempoolOrdersProducerConfig =
    { producerBrokers = ["redpanda-1:19092"]
    , producerTimeout = 1000
    },  
  txEventsTopicName = "tx-events",
  ordersTopicName = "orders-topic",
  poolsTopicName = "pools-topic-name",
  mempoolOrdersTopicName = "mempool-orders-topic",
  scriptsConfig =
    { swapScriptPath    = "/scripts/swap.uplc"
    , depositScriptPath = "/scripts/deposit.uplc"
    , redeemScriptPath  = "/scripts/redeem.uplc"
    , poolScriptPath    = "/scripts/pool.uplc"
    },
  eventSourceConfig =
    { startAt =
        { slot = 31769708
        , hash = "5346d72822af688385cc3206bdf2c1dd1179221fabd5d3080ca52d300ac8343f"
        }
    },
  nodeConfigPath = "/config/cardano/config.json",
  lederStoreConfig =
    { storePath       = "/data/amm-executor"
    , createIfMissing = True
    },
  nodeSocketConfig =
    { nodeSocketPath = "/ipc/node.socket"
    , maxInFlight    = 256
    },
  loggingConfig =
    { fileHandlers = [fileHandlers "/dev/stdout" LogLevel.Info]
    , levelOverrides = [] : List { _1 : Text, _2 : LogLevel }
    , rootLogLevel = LogLevel.Info
    }
}