(ns user
  (:require [mount.core :as mount]
            [garden-gnome.watcher :as garden-gnome]
            [clojure.tools.namespace.repl :as ns-tools]))

(mount/defstate gnome
  :start (garden-gnome/start! (garden-gnome/default-config))
  :stop (garden-gnome/stop! gnome))

(defn start []
  (mount/start #'gnome))

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