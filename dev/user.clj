(ns user
  (:require [mount.core :as mount]
            [garden-mount.watcher :as garden-watcher]
            [clojure.tools.namespace.repl :as ns-tools]))

(mount/defstate garden
  :start (garden-watcher/start! (garden-watcher/default-config))
  :stop (garden-watcher/stop! garden))

(defn start []
  (mount/start #'garden))

(defn stop []
  (mount/stop))

(defn refresh []
  (stop)
  (ns-tools/refresh))

(defn refresh-all []
  (stop)
  (ns-tools/refresh-all))

(defn go
  "starts all states defined by defstate"
  []
  (start)
  :ready)

(defn reset
  "stops all states defined by defstate, reloads modified source files, and restarts the states"
  []
  (stop)
  (ns-tools/refresh :after 'user/go))