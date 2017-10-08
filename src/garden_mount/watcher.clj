(ns garden-mount.watcher
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [garden.core :as garden]
            [hawk.core :as hawk])
  (:import (java.io File)))


(defn- reload-and-compile! [config]
  (let [{:keys [source-paths stylesheet compiler]} config]
    (println (str "Garden: compiling #'" stylesheet))
    (println "requiring namespace" (namespace stylesheet) "type: " (type (namespace stylesheet)))
    (require (symbol (namespace stylesheet)) :reload)
    (println "making parents" (:output-to compiler))
    (io/make-parents (:output-to compiler))
    (println "compiling" (var-get (find-var stylesheet)))
    (garden/css compiler (var-get (find-var stylesheet)))))


(defn- ns->file-name
  "Copied from clojure.tools.namespace.move because it's private there."
  [sym]
  (str (-> (name sym)
           (str/replace "-" "_")
           (str/replace "." File/separator))
       ".clj"))


(defn- file-for-config? [^File path config]
  (let [styles-ns     (namespace (:stylesheet config))
        reloaded-path (.getPath path)
        ns-file-name  (ns->file-name styles-ns)]
    (.endsWith reloaded-path ns-file-name)))


(defn- garden-reloader-handler [configs]
  (fn [_ctx {:keys [kind file] :as event}]
    (println "Handler for:  " event)
    (when (= kind :modify)
      (some->> configs
               (filter #(file-for-config? file %))
               (first)
               (reload-and-compile!)))))


(defn compile-garden-namespaces [configs]
  (run! reload-and-compile! configs))


(defn start!
  "Starts a new watcher for the given configs.
  Use (default-config) to read from 'project-clj'"
  [configs]
  (let [source-paths (flatten (map :source-paths configs))
        handler      (garden-reloader-handler configs)]
    (compile-garden-namespaces configs)
    (println "Garden: watching" (str/join ", " source-paths))
    (hawk/watch! [{:paths   source-paths
                   :handler handler}])))


(defn stop! [watch]
  (hawk/stop! watch)
  (println "Garden: stopped watching namespaces."))


(defn default-config []
  (if (.exists (io/file "garden.edn"))
    (edn/read-string (slurp "garden.edn"))
    (->> "project.clj"
         slurp
         edn/read-string
         (drop 3)
         (apply hash-map)
         :garden
         :builds)))